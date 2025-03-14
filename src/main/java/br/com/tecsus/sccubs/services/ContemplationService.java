package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.Appointment;
import br.com.tecsus.sccubs.entities.Contemplation;
import br.com.tecsus.sccubs.entities.MedicalSlot;
import br.com.tecsus.sccubs.enums.AppointmentStatus;
import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.ContemplationRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.exceptions.CancelContemplationException;
import br.com.tecsus.sccubs.services.exceptions.ConfirmContemplationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class ContemplationService {

    private final ContemplationRepository contemplationRepository;
    private final DateTimeFormatter formatter;
    private final MedicalSlotService medicalSlotService;
    private final AppointmentService appointmentService;
    private final AppointmentStatusHistoryService appointmentStatusHistoryService;

    @Autowired
    public ContemplationService(ContemplationRepository contemplationRepository, MedicalSlotService medicalSlotService, AppointmentService appointmentService, AppointmentStatusHistoryService appointmentStatusHistoryService) {
        this.contemplationRepository = contemplationRepository;
        this.medicalSlotService = medicalSlotService;
        this.appointmentStatusHistoryService = appointmentStatusHistoryService;
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.appointmentService = appointmentService;
    }

    public Page<Contemplation> findContemplationsByUBSAndSpecialty(ProcedureType type,
                                                                   Long ubsId,
                                                                   Long specialtyId,
                                                                   String referenceMonth,
                                                                   String status,
                                                                   Pageable page) {
        YearMonth yearMonth = null;

        if (referenceMonth != null && !referenceMonth.isEmpty()) {
            yearMonth = YearMonth.parse(referenceMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        return contemplationRepository
                .findConsultationsByUBSAndSpecialtyPaginated(type,
                        ubsId,
                        specialtyId,
                        yearMonth,
                        status == null || status.isEmpty() ? null : AppointmentStatus.getByDescription(status),
                        page);
    }

    @Transactional(readOnly = true)
    public Contemplation loadContemplatedById(Long contemplationId) {
        return contemplationRepository.loadFetchedContemplationById(contemplationId);
    }

    @Transactional
    public void cancelContemplationByAdmin(Long contemplatedId, String reason, SystemUserDetails loggedUser) throws CancelContemplationException {

        Contemplation contemplated = contemplationRepository.getReferenceById(contemplatedId);
        contemplated.getAppointment().setStatus(AppointmentStatus.CONTEMPLACAO_CANCELADA_SMS);
        contemplated.setUpdateUser(loggedUser.getName());
        contemplated.setUpdateDate(LocalDateTime.now());

        if (contemplated.isEmptyObservation()) {
            contemplated.setObservation("Cancelado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter) + " -- Motivo: " + reason);
        } else {
            contemplated.setObservation(contemplated.getObservation() + " -- Cancelado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter) + " -- Motivo: " + reason);
        }

        contemplationRepository.save(contemplated);
        appointmentStatusHistoryService.registerAppointmentStatusHistory(contemplated.getAppointment(), loggedUser.getName());

        log.info("Recuperando slot disponível.");
        medicalSlotService.addSlot(contemplated.getMedicalSlot());

    }

    @Transactional
    public void confirmContemplationByAdmin(Long contemplationId, SystemUserDetails loggedUser) throws ConfirmContemplationException {

        Contemplation contemplated = contemplationRepository.getReferenceById(contemplationId);
        contemplated.getAppointment().setStatus(AppointmentStatus.CONTEMPLACAO_CONFIRMACAO_SMS);
        contemplated.setUpdateUser(loggedUser.getName());
        contemplated.setUpdateDate(LocalDateTime.now());
        contemplated.setObservation("Confirmado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter));

        contemplationRepository.save(contemplated);
        appointmentStatusHistoryService.registerAppointmentStatusHistory(contemplated.getAppointment(), loggedUser.getName());
    }

    @Transactional
    public void contemplateAppointmentByAdmin(Long appointmentId, String reason, Long medicalSlotId, SystemUserDetails loggedUser) {

        Appointment appt = appointmentService.findReferenceById(appointmentId);
        appt.setStatus(AppointmentStatus.CONTEMPLACAO_CONFIRMACAO_SMS);

        MedicalSlot medicalSlot = new MedicalSlot();
        medicalSlot.setId(medicalSlotId);

        log.info("Removendo slot disponível.");
        medicalSlot = medicalSlotService.removeSlot(medicalSlot);

        Contemplation contemplation = new Contemplation();

        contemplation.setContemplationDate(LocalDateTime.now());
        contemplation.setContemplatedBy(Priorities.ADMINISTRATIVO);
        contemplation.setCreationDate(LocalDateTime.now());
        contemplation.setCreationUser(loggedUser.getUsername());
        contemplation.setAppointment(appt);
        contemplation.setMedicalSlot(medicalSlot);
        contemplation.setObservation("Paciente contemplado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter) + " -- Motivo: " + reason);

        log.info("Salvando contemplação via administrativo.");
        appt = appointmentService.updateAppointment(appt);
        contemplationRepository.save(contemplation);
        appointmentStatusHistoryService.registerAppointmentStatusHistory(appt, loggedUser.getName());

    }

    @Transactional
    public void registerContemplation(Contemplation contemplation) {
        contemplationRepository.save(contemplation);
    }

}
