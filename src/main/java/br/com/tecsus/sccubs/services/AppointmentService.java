package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.dtos.PatientAppointmentsHistoryDTO;
import br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sccubs.entities.Appointment;
import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.entities.Specialty;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.MedicalProcedureRepository;
import br.com.tecsus.sccubs.repositories.AppointmentRepository;
import br.com.tecsus.sccubs.repositories.PatientRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.exceptions.AppointmentRegistrationFailureException;
import br.com.tecsus.sccubs.services.exceptions.CancelAppointmentException;
import br.com.tecsus.sccubs.services.exceptions.DuplicateAppointmentRegistrationException;
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

    @Transactional(readOnly = true)
    public List<PatientOpenAppointmentDTO> findPatientOpenAppointments(Long patientId) {
        return appointmentRepository.findOpenAppointmentsById(patientId);
    }

    @Transactional
    public void registerAppointment(Appointment appointment, SystemUserDetails loggedUser) throws AppointmentRegistrationFailureException, DuplicateAppointmentRegistrationException {

        List<PatientOpenAppointmentDTO> patientOpenAppointments = appointmentRepository.findOpenAppointmentsById(appointment.getPatient().getId());

        boolean isDuplicated = patientOpenAppointments
                .stream()
                .anyMatch(patientOpenAppointment -> Objects.equals(patientOpenAppointment.medicalProcedureId(), appointment.getMedicalProcedure().getId()));

        if (isDuplicated) {
            throw new DuplicateAppointmentRegistrationException("Existe, pelo menos, uma consulta marcada para este procedimento em aberto.");
        }

        Patient patient = patientRepository.getReferenceById(appointment.getPatient().getId());
        MedicalProcedure medicalProcedure = medicalProcedureRepository.getReferenceById(appointment.getMedicalProcedure().getId());

        appointment.setPatient(patient);
        appointment.setMedicalProcedure(medicalProcedure);
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

}
