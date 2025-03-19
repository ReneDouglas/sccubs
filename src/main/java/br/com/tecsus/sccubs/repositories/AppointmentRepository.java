package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.dtos.MedicalProceduresTotalDTO;
import br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sccubs.dtos.ProcedureTypeTotalDTO;
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
                null,
                COALESCE(a.observation, "Sem observações."),
                a.id,
                a.patient.id,
                null,
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
        AND a.contemplation IS NULL
        AND a.status = br.com.tecsus.sccubs.enums.AppointmentStatus.AGUARDANDO_CONTEMPLACAO
        ORDER BY a.requestDate DESC
    """)
    List<PatientOpenAppointmentDTO> findPatientOpenAppointments(@Param("id") Long id);


    @Transactional(readOnly = true)
    @Query("""
        SELECT
            new br.com.tecsus.sccubs.dtos.ProcedureTypeTotalDTO(
                mp.procedureType,
                COUNT(mp.id))
        FROM
            Appointment a
        LEFT JOIN a.medicalProcedure mp
        LEFT JOIN mp.specialty s
        LEFT JOIN a.patient p
        LEFT JOIN p.basicHealthUnit ubs
        WHERE
            ubs.id = :basicHealthUnit
            AND s.id = :specialty
        GROUP BY mp.procedureType
    """)
    List<ProcedureTypeTotalDTO> totalByProcedureTypeAndUBSAndSpecialty(Long basicHealthUnit, Long specialty);


    @Transactional(readOnly = true)
    @Query("""
        SELECT
            new br.com.tecsus.sccubs.dtos.MedicalProceduresTotalDTO(
                mp.description,
                mp.procedureType,
                COUNT(mp.id))
        FROM
            Appointment a
        LEFT JOIN a.medicalProcedure mp
        LEFT JOIN mp.specialty s
        LEFT JOIN a.patient p
        LEFT JOIN p.basicHealthUnit ubs
        WHERE
            ubs.id = :basicHealthUnit
            AND s.id = :specialty
            AND mp.procedureType <> br.com.tecsus.sccubs.enums.ProcedureType.CONSULTA
        GROUP BY mp.id
    """)
    List<MedicalProceduresTotalDTO> totalByMedicalProceduresAndUBSAndSpecialty(Long basicHealthUnit, Long specialty);

}
