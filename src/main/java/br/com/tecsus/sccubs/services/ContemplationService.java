package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.Contemplation;
import br.com.tecsus.sccubs.entities.MedicalSlot;
import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.AppointmentRepository;
import br.com.tecsus.sccubs.repositories.ContemplationRepository;
import br.com.tecsus.sccubs.repositories.MedicalSlotRepository;
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
    private final MedicalSlotRepository medicalSlotRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public ContemplationService(ContemplationRepository contemplationRepository, MedicalSlotRepository medicalSlotRepository, AppointmentRepository appointmentRepository) {
        this.contemplationRepository = contemplationRepository;
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.medicalSlotRepository = medicalSlotRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Page<Contemplation> findContemplationsByUBSAndSpecialty(ProcedureType type,
                                                                   Long ubsId,
                                                                   Long specialtyId,
                                                                   String referenceMonth,
                                                                   String confirmed,
                                                                   Pageable page) {

        Boolean status = confirmed == null || confirmed.equals("null") ? null : Boolean.parseBoolean(confirmed);

        YearMonth yearMonth = YearMonth.parse(referenceMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        return contemplationRepository
                .findConsultationsByUBSAndSpecialtyPaginated(type, ubsId, specialtyId, yearMonth, status, page);
    }

    public Contemplation loadContemplatedById(long contemplationId) {
        return contemplationRepository.loadFetchedContemplationById(contemplationId);
    }

    @Transactional
    public void cancelContemplation(Long contemplatedId, String reason, SystemUserDetails loggedUser) throws CancelContemplationException {

        Contemplation contemplated = contemplationRepository.getReferenceById(contemplatedId);
        contemplated.setCanceled(true);
        contemplated.setUpdateUser(loggedUser.getName());
        contemplated.setUpdateDate(LocalDateTime.now());

        if (contemplated.isEmptyObservation()) {
            contemplated.setObservation("Cancelado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter) + " -- Motivo: " + reason);
        } else {
            contemplated.setObservation(contemplated.getObservation() + " -- Cancelado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter) + " -- Motivo: " + reason);
        }

        contemplationRepository.save(contemplated);

        MedicalSlot medicalSlot = contemplated.getMedicalSlot();
        medicalSlot.setCurrentSlots(medicalSlot.getCurrentSlots() + 1);

        medicalSlotRepository.save(medicalSlot);

        log.info("Slots disponíveis atualizados.");

    }

    @Transactional
    public void confirmContemplation(Long contemplationId, SystemUserDetails loggedUser) throws ConfirmContemplationException {

        Contemplation contemplated = contemplationRepository.getReferenceById(contemplationId);
        contemplated.setConfirmed(true);
        contemplated.setUpdateUser(loggedUser.getName());
        contemplated.setUpdateDate(LocalDateTime.now());
        contemplated.setObservation("Confirmado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter));

        contemplationRepository.save(contemplated);
    }

    @Transactional
    public void contemplateAppointmentByAdmin(Long appointmentId, String reason, Long medicalSlotId, SystemUserDetails loggedUser) {

        MedicalSlot medicalSlot = medicalSlotRepository.getReferenceById(medicalSlotId);
        Contemplation contemplation = new Contemplation();

        contemplation.setContemplationDate(LocalDateTime.now());
        contemplation.setContemplatedBy(Priorities.ADMINISTRATIVO);
        contemplation.setConfirmed(true);
        contemplation.setCanceled(false);
        contemplation.setCreationDate(LocalDateTime.now());
        contemplation.setCreationUser(loggedUser.getUsername());
        contemplation.setAppointment(appointmentRepository.getReferenceById(appointmentId));
        contemplation.setMedicalSlot(medicalSlot);
        contemplation.setObservation("Paciente contemplado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter) + " -- Motivo: " + reason);

        medicalSlot.setCurrentSlots(medicalSlot.getCurrentSlots() - 1);

        log.info("Atualizando quantidade de slots disponíveis: {}", medicalSlot.getCurrentSlots());
        medicalSlotRepository.save(medicalSlot);

        log.info("Salvando contemplação via administrativo.");
        contemplationRepository.save(contemplation);

    }

    public void registerContemplation(Contemplation contemplation) {
        contemplationRepository.save(contemplation);
    }

}
