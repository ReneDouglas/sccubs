package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.dtos.PatientAppointmentsHistoryDTO;
import br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sccubs.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

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
                a.patient.id)
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
    List<PatientOpenAppointmentDTO> findOpenAppointmentsById(@Param("id") Long id);

}
