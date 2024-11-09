package br.com.tecsus.sccubs.repositories.Impl;

import br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sccubs.entities.Appointment;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.AppointmentRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;


public class AppointmentRepositoryCustomImpl implements AppointmentRepositoryCustom {

    private final EntityManager entityManager;

    @Autowired
    public AppointmentRepositoryCustomImpl(JpaContext jpaContext) {
        this.entityManager = jpaContext.getEntityManagerByManagedType(Appointment.class);
    }

    @Override
    public Page<PatientOpenAppointmentDTO> findOpenAppointmentsQueuePaginated(ProcedureType type, Long ubsId, Long specialtyId, Pageable pageable) {

        TypedQuery<Long> openAppointmentsIdsQueryPaginated = entityManager.createQuery("""
            SELECT
                a.id
            FROM
                Appointment a
            LEFT JOIN a.contemplation c
            LEFT JOIN a.medicalProcedure mp
            LEFT JOIN mp.specialty s
            LEFT JOIN a.patient p
            LEFT JOIN p.basicHealthUnit ubs
            WHERE c.appointment IS NULL
                AND s.id = :specialtyId
                AND mp.procedureType = :type
                AND ubs.id = :ubsId
                AND a.canceled = false
            ORDER BY
                    a.priority ASC,
                    p.birthDate ASC,
                    p.socialSituationRating ASC,
                    a.requestDate ASC
        """, Long.class);
        //   CASE WHEN a.priority = br.com.tecsus.sccubs.enums.Priorities.ELETIVO THEN p.birthDate END ASC,
        //   CASE WHEN a.priority = br.com.tecsus.sccubs.enums.Priorities.ELETIVO THEN p.socialSituationRating END ASC,


        openAppointmentsIdsQueryPaginated.setParameter("specialtyId", specialtyId);
        openAppointmentsIdsQueryPaginated.setParameter("type", type);
        openAppointmentsIdsQueryPaginated.setParameter("ubsId", ubsId);

        openAppointmentsIdsQueryPaginated.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        openAppointmentsIdsQueryPaginated.setMaxResults(pageable.getPageSize());

        var openAppointmentsQueueIdsPaginated = openAppointmentsIdsQueryPaginated.getResultList();
        long totalCountOpenAppointmentsQueue = pageable.getPageSize();

        if (openAppointmentsQueueIdsPaginated.size() >= pageable.getPageSize()) {
            TypedQuery<Long> count = entityManager.createQuery("""
                SELECT
                    COUNT(a.id)
                FROM
                    Appointment a
                LEFT JOIN a.contemplation c
                LEFT JOIN a.medicalProcedure mp
                LEFT JOIN mp.specialty s
                LEFT JOIN a.patient p
                LEFT JOIN p.basicHealthUnit ubs
                WHERE c.appointment IS NULL
                    AND s.id = :specialtyId
                    AND mp.procedureType = :type
                    AND ubs.id = :ubsId
                    AND a.canceled = false
            """, Long.class);

            count.setParameter("specialtyId", specialtyId);
            count.setParameter("type", type);
            count.setParameter("ubsId", ubsId);

            totalCountOpenAppointmentsQueue = count.getSingleResult();
        }

        TypedQuery<PatientOpenAppointmentDTO> openAppointmentsQueueQuery = entityManager.createQuery("""
                SELECT
                    new br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO(
                    a.requestDate,
                    a.priority,
                    mp.procedureType,
                    mp.id,
                    mp.description,
                    s.title,
                    COALESCE(a.observation, 'Sem observações.'),
                    a.id,
                    p.id,
                    p.name,
                    p.gender,
                    p.birthDate,
                    p.socialSituationRating)
                FROM
                    Appointment a
                LEFT JOIN a.medicalProcedure mp
                LEFT JOIN mp.specialty s
                LEFT JOIN a.patient p
                WHERE a.id IN :ids
            """, PatientOpenAppointmentDTO.class);

        openAppointmentsQueueQuery.setParameter("ids", openAppointmentsQueueIdsPaginated);
        var openAppointmentsQueue = openAppointmentsQueueQuery.getResultList();

        return new PageImpl<>(openAppointmentsQueue, pageable, totalCountOpenAppointmentsQueue);
    }
}