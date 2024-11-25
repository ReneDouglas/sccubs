package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.dtos.MedicalProceduresTotalDTO;
import br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sccubs.dtos.ProcedureTypeTotalDTO;
import br.com.tecsus.sccubs.entities.*;
import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.*;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.exceptions.AppointmentRegistrationFailureException;
import br.com.tecsus.sccubs.services.exceptions.CancelAppointmentException;
import br.com.tecsus.sccubs.services.exceptions.DuplicateAppointmentRegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final MedicalProcedureRepository medicalProcedureRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, MedicalProcedureRepository medicalProcedureRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.medicalProcedureRepository = medicalProcedureRepository;
        this.patientRepository = patientRepository;
    }


    public List<MedicalProcedure> findBySpecialtyIdAndProcedureType(Long specialtyId, ProcedureType procedureType) {
        Specialty specialty = new Specialty();
        specialty.setId(specialtyId);
        return medicalProcedureRepository.findAllBySpecialtyAndProcedureType(specialty, procedureType);
    }

    public List<PatientOpenAppointmentDTO> findPatientOpenAppointments(Long patientId) {
        return appointmentRepository.findPatientOpenAppointments(patientId);
    }

    @Transactional
    public void registerAppointment(Appointment appointment, SystemUserDetails loggedUser) throws AppointmentRegistrationFailureException, DuplicateAppointmentRegistrationException {

        List<PatientOpenAppointmentDTO> patientOpenAppointments = appointmentRepository.findPatientOpenAppointments(appointment.getPatient().getId());
        MedicalProcedure medicalProcedure;

        boolean isDuplicated = patientOpenAppointments
                .stream()
                .anyMatch(patientOpenAppointment -> Objects.equals(patientOpenAppointment.medicalProcedureId(), appointment.getMedicalProcedure().getId()));

        if (isDuplicated) {
            throw new DuplicateAppointmentRegistrationException("Existe, pelo menos, uma consulta marcada para este procedimento em aberto.");
        }


        //Patient patient = patientRepository.getReferenceById(appointment.getPatient().getId());
        //medicalProcedure = medicalProcedureRepository.getReferenceById(appointment.getMedicalProcedure().getId());

        //appointment.setPatient(patient);
        //appointment.setMedicalProcedure(medicalProcedure);
        appointment.setContemplation(null);
        appointment.setCreationUser(loggedUser.getName());
        appointment.setRequestDate(LocalDateTime.now());

        appointmentRepository.save(appointment);

    }

    @Transactional
    public void cancelSolicitation(Long id, SystemUserDetails loggedUser) throws CancelAppointmentException {

        Appointment appt = appointmentRepository.getReferenceById(id);

        appt.setCanceled(true);
        appt.setUpdateUser(loggedUser.getName());
        appt.setUpdateDate(LocalDateTime.now());

        appointmentRepository.save(appt);

    }

    @Transactional(readOnly = true)
    public Page<PatientOpenAppointmentDTO> findOpenAppointmentsQueuePaginated(ProcedureType type, Long ubsId, Long specialtyId, Pageable pageable) {
        return appointmentRepository.findOpenAppointmentsQueuePaginated(type, ubsId, specialtyId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PatientOpenAppointmentDTO> findOpenAppointmentsQueuePaginatedV2(Long ubsId, Long specialtyId, Long medicalProcedureId, Pageable pageable) {
        return appointmentRepository.findOpenAppointmentsQueuePaginatedV2(ubsId, specialtyId, medicalProcedureId, pageable);
    }

    @Transactional(readOnly = true)
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public List<ProcedureTypeTotalDTO> findProcedureTypeTotal(Long ubsId, Long specialtyId) {
        return appointmentRepository.totalByProcedureTypeAndUBSAndSpecialty(ubsId, specialtyId);
    }

    public List<MedicalProceduresTotalDTO> findMedicalProceduresTotal(Long ubsId, Long specialtyId) {
        return appointmentRepository.totalByMedicalProceduresAndUBSAndSpecialty(ubsId, specialtyId);
    }

}
