package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sccubs.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, AppointmentRepositoryCustom {

    @Transactional(readOnly = true)
    @Query("""
        SELECT
            new br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO(
                a.requestDate,
                a.priority,
                mp.procedureType,
                mp.id,
                mp.description,
                s.title,
                COALESCE(a.observation, "Sem observações."),
                a.id,
                a.patient.id,
                null,
                null,
                null,
                null)
        FROM
            Appointment a
        LEFT JOIN a.contemplation c
        LEFT JOIN a.medicalProcedure mp
        LEFT JOIN mp.specialty s
        WHERE
            a.patient.id = :id
        AND c.appointment IS NULL
        AND a.canceled = false
        ORDER BY a.requestDate DESC
    """)
    List<PatientOpenAppointmentDTO> findPatientOpenAppointments(@Param("id") Long id);

}
