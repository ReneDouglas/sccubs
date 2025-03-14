package br.com.tecsus.sccubs.jobs;

import br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import br.com.tecsus.sccubs.entities.Contemplation;
import br.com.tecsus.sccubs.entities.MedicalSlot;
import br.com.tecsus.sccubs.enums.AppointmentStatus;
import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.services.AppointmentService;
import br.com.tecsus.sccubs.services.AppointmentStatusHistoryService;
import br.com.tecsus.sccubs.services.ContemplationService;
import br.com.tecsus.sccubs.services.MedicalSlotService;
import br.com.tecsus.sccubs.utils.ContemplationScheduleStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.tecsus.sccubs.utils.DefaultValues.QUATRO_MESES;

@Slf4j
@Component
public class ContemplationScheduleV2 {

    private final MedicalSlotService medicalSlotService;
    private final AppointmentService appointmentService;
    private final ContemplationService contemplationService;
    private final AppointmentStatusHistoryService appointmentStatusHistoryService;

    private static final int NEXT_CONTEMPLATED = 1;
    private static final int MAX_ATTEMPTS = 4;
    private static final String USERNAME_JOB = "Rotina de Contemplação";

    @Autowired
    public ContemplationScheduleV2(MedicalSlotService medicalSlotService, AppointmentService appointmentService, ContemplationService contemplationService, AppointmentStatusHistoryService appointmentStatusHistoryService) {
        this.medicalSlotService = medicalSlotService;
        this.appointmentService = appointmentService;
        this.contemplationService = contemplationService;
        this.appointmentStatusHistoryService = appointmentStatusHistoryService;
    }

    @Transactional
    @Scheduled(cron = "${schedule.cron.contemplation}")
    @Retryable(retryFor = RuntimeException.class,
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = 5000))
    public void processContemplationTask() throws RuntimeException {

        if (RetrySynchronizationManager.getContext().getRetryCount() > 0) {
            log.warn("[retry] A rotina de contemplação falhou.");
            log.warn("[retry] Mensagem de erro: {}", RetrySynchronizationManager.getContext().getLastThrowable().getMessage());
            log.warn("[retry] Tentativa {} de {}", RetrySynchronizationManager.getContext().getRetryCount(), MAX_ATTEMPTS);
        }

        log.info(" ");
        log.info("========================================");
        log.info("=== INICIANDO ROTINA DE CONTEMPLAÇÃO ===");
        log.info("========================================");
        log.info(" ");

        ContemplationScheduleStatus.status = ContemplationScheduleStatus.Status.RUNNING;
        ContemplationScheduleStatus.startTime = LocalDateTime.now();

        YearMonth referenceMonth = YearMonth.now();
        var availableSlots = medicalSlotService.findAvailableSlotsByReferenceMonth();

        log.info("==> Carregando vagas disponíveis");
        log.info("> Mês de Referência: {}", referenceMonth.getMonth().name().toUpperCase());

        if (availableSlots.isEmpty()) {
            log.info("> Total de Vagas: 0");
            log.info("========================================");
            log.info("=== ROTINA DE CONTEMPLAÇÃO FINALIZADA ===");
            log.info("========================================");
            return;
        }

        log.info("> Total de Vagas: {}", availableSlots.stream().mapToInt(MedicalSlot::getCurrentSlots).sum());

        Map<BasicHealthUnit, List<MedicalSlot>> slotsByUBS = availableSlots.stream().collect(Collectors.groupingBy(MedicalSlot::getBasicHealthUnit));

        processSlotsByUBS(slotsByUBS);

        ContemplationScheduleStatus.status = ContemplationScheduleStatus.Status.DONE;
        ContemplationScheduleStatus.endTime = LocalDateTime.now();

        log.info(" ");
        log.info("========================================");
        log.info("=== ROTINA DE CONTEMPLAÇÃO FINALIZADA ===");
        log.info("========================================");
    }

    private void processSlotsByUBS(Map<BasicHealthUnit, List<MedicalSlot>> slotsByUBS) {

        log.info(" ");
        log.info("======== INICIANDO CONTEMPLAÇÕES POR UBS ========");
        log.info(" ");

        slotsByUBS.forEach((ubs, slots) -> {
            log.info("::::::::::::::::::INICIO DA CONTEMPLAÇÃO [{}] ::::::::::::::::::", ubs.getName());
            slots.forEach(this::processSlotsByProcedure);
            log.info("::::::::::::::::::: FIM DA CONTEMPLAÇÃO [{}] :::::::::::::::::::", ubs.getName());
        });

        log.info(" ");
        log.info("========================================");
        log.info("=== ROTINA DE CONTEMPLAÇÃO FINALIZADA ===");
        log.info("========================================");
    }

    private void processSlotsByProcedure(MedicalSlot slotsByProcedure) {

        log.info(" ");
        log.info(">>> Vagas disponíveis para {}[{}][{}]: {}",
                slotsByProcedure.getMedicalProcedure().getDescription(),
                slotsByProcedure.getMedicalProcedure().getProcedureType().name(),
                slotsByProcedure.getMedicalProcedure().getSpecialty().getDescription(),
                slotsByProcedure.getCurrentSlots());

        var queue = loadAppointmentQueue(slotsByProcedure);
        log.info(">> [{}] pacientes carregados da fila de espera.", queue.getContent().size());
        log.info(">> Iniciando contemplação...");
        log.info("::::::::: [NOME DO PACIENTE] ::::::::: [CPF] ::::::::: [CONTEMPLADO POR] :::::::::");

        for (int slot = 0; slot < slotsByProcedure.getCurrentSlots(); slot++) {
            contemplatePatient(
                    queue.getContent().get(slot),
                    queue.getContent().get(slot + NEXT_CONTEMPLATED),
                    slotsByProcedure
            );
        }

        log.info("[X]------[X]------[X]------[X]------[X]------[X]------[X]");
    }

    private Page<PatientOpenAppointmentDTO> loadAppointmentQueue(MedicalSlot slotsByProcedure) {
        return appointmentService.findOpenAppointmentsQueuePaginatedV2(
                slotsByProcedure.getBasicHealthUnit().getId(),
                null,
                slotsByProcedure.getMedicalProcedure().getId(),
                PageRequest.of(0, slotsByProcedure.getCurrentSlots() + NEXT_CONTEMPLATED));
    }

    private void contemplatePatient(PatientOpenAppointmentDTO currentContemplated, PatientOpenAppointmentDTO nextContemplated, MedicalSlot slotsByProcedure) {

        var appt = appointmentService.findReferenceById(currentContemplated.appointmentId());
        appt.setStatus(AppointmentStatus.PACIENTE_CONTEMPLADO);

        var contemplated = new Contemplation();
        contemplated.setMedicalSlot(slotsByProcedure);
        contemplated.setAppointment(appt);
        contemplated.setContemplatedBy(contemplatedBy(currentContemplated, nextContemplated));
        contemplated.setContemplationDate(LocalDateTime.now());
        contemplated.setCreationDate(LocalDateTime.now());
        contemplated.setCreationUser(USERNAME_JOB);

        appt = appointmentService.updateAppointment(appt);
        contemplationService.registerContemplation(contemplated);
        appointmentStatusHistoryService.registerAppointmentStatusHistory(appt, USERNAME_JOB);
        medicalSlotService.removeSlot(slotsByProcedure);

        log.info("> Contemplado: [{}] [{}] [{}]",
                currentContemplated.patientName(),
                currentContemplated.patientCPF(),
                contemplated.getContemplatedBy().getDescription());
    }

    @Recover
    private void failedContemplationScheduleTask(RuntimeException e) {

        log.error("[retry] Todas as tentativas da rotina de contemplação falharam.");
        log.error("[retry] Número de tentativas: {}", RetrySynchronizationManager.getContext().getRetryCount());
        log.error("[retry] Erro: {}", e.getMessage());
        log.error("[retry] Finalizando rotina de contemplação.");

        ContemplationScheduleStatus.status = ContemplationScheduleStatus.Status.FAILED;
        ContemplationScheduleStatus.endTime = LocalDateTime.now();
    }

    private Priorities contemplatedBy(PatientOpenAppointmentDTO currentContemplated, PatientOpenAppointmentDTO nextContemplated) {

        if (currentContemplated.requestDate().isBefore(LocalDateTime.now().minusMonths(QUATRO_MESES))) {
            return Priorities.MAIS_DE_QUATRO_MESES;
        } else if (currentContemplated.priority().getValue() > nextContemplated.priority().getValue()) {
            return currentContemplated.priority();
        } else if (currentContemplated.patientBirthDate().isBefore(nextContemplated.patientBirthDate())) {
            return Priorities.IDADE;
        } else if (currentContemplated.patientSocialSituationRating().getPriority() > nextContemplated.patientSocialSituationRating().getPriority()) {
            return Priorities.SITUACAO_SOCIAL;
        } else if (currentContemplated.patientGender().equals("Feminino")) {
            return Priorities.SEXO;
        } else {
            return Priorities.DATA_DA_MARCACAO;
        }
    }

}
