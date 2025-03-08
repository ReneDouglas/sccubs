package br.com.tecsus.sccubs.repositories.Impl;

import br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sccubs.entities.Appointment;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.AppointmentRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;

import java.time.LocalDateTime;

import static br.com.tecsus.sccubs.utils.DefaultValues.QUATRO_MESES;


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
                AND a.status = br.com.tecsus.sccubs.enums.AppointmentStatus.AGUARDANDO
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
        long totalCountOpenAppointmentsQueue = openAppointmentsQueueIdsPaginated.size();

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
                    AND a.status = br.com.tecsus.sccubs.enums.AppointmentStatus.AGUARDANDO
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
                    null,
                    COALESCE(a.observation, 'Sem observações.'),
                    a.id,
                    p.id,
                    p.name,
                    p.cpf,
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


    @Override
    public Page<PatientOpenAppointmentDTO> findOpenAppointmentsQueuePaginatedV2(Long ubsId, Long specialtyId, Long medicalProcedureId, Pageable pageable) {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT a.id FROM Appointment a ");
        queryBuilder.append("LEFT JOIN a.contemplation c ");
        queryBuilder.append("LEFT JOIN a.medicalProcedure mp ");
        queryBuilder.append("LEFT JOIN mp.specialty s ");
        queryBuilder.append("LEFT JOIN a.patient p ");
        queryBuilder.append("LEFT JOIN p.basicHealthUnit ubs ");
        queryBuilder.append("WHERE c.appointment IS NULL ");

        if (ubsId != null) queryBuilder.append("AND ubs.id = :ubsId ");
        if (specialtyId != null) queryBuilder.append("AND s.id = :specialtyId ");
        if (medicalProcedureId != null) queryBuilder.append("AND mp.id = :medicalProcedureId ");

        queryBuilder.append("AND a.status = br.com.tecsus.sccubs.enums.AppointmentStatus.AGUARDANDO ");
        queryBuilder.append("ORDER BY ");

        if (ubsId == null) queryBuilder.append("ubs.name, ");
        if (specialtyId == null) queryBuilder.append("s.title, ");
        if (medicalProcedureId == null) queryBuilder.append("mp.description, ");

        queryBuilder.append("CASE WHEN a.requestDate <= :dateLimit THEN 1 ELSE 2 END ASC, ");
        queryBuilder.append("a.priority ASC, ");
        queryBuilder.append("p.birthDate ASC, ");
        queryBuilder.append("p.socialSituationRating ASC, ");
        queryBuilder.append("a.requestDate ASC");

        TypedQuery<Long> openAppointmentsIdsQueryPaginated = entityManager.createQuery(queryBuilder.toString(), Long.class);
        /*TypedQuery<Long> openAppointmentsIdsQueryPaginated = entityManager.createQuery("""
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
                AND mp.id = :medicalProcedureId
                AND ubs.id = :ubsId
                AND a.status = br.com.tecsus.sccubs.enums.AppointmentStatus.AGUARDANDO
            ORDER BY
                    CASE WHEN a.requestDate <= :dateLimit THEN 1 ELSE 2 END ASC,
                    a.priority ASC,
                    p.birthDate ASC,
                    p.socialSituationRating ASC,
                    a.requestDate ASC
        """, Long.class);*/
        //   CASE WHEN a.priority = br.com.tecsus.sccubs.enums.Priorities.ELETIVO THEN p.birthDate END ASC,
        //   CASE WHEN a.priority = br.com.tecsus.sccubs.enums.Priorities.ELETIVO THEN p.socialSituationRating END ASC,

        // remover specialtyId. Não é necessário
        //openAppointmentsIdsQueryPaginated.setParameter("specialtyId", specialtyId);
        openAppointmentsIdsQueryPaginated.setParameter("dateLimit", LocalDateTime.now().minusMonths(QUATRO_MESES));
        if (medicalProcedureId != null) openAppointmentsIdsQueryPaginated.setParameter("medicalProcedureId", medicalProcedureId);
        if (ubsId != null) openAppointmentsIdsQueryPaginated.setParameter("ubsId", ubsId);
        if (specialtyId != null) openAppointmentsIdsQueryPaginated.setParameter("specialtyId", specialtyId);

        openAppointmentsIdsQueryPaginated.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        openAppointmentsIdsQueryPaginated.setMaxResults(pageable.getPageSize());

        var openAppointmentsQueueIdsPaginated = openAppointmentsIdsQueryPaginated.getResultList();
        long totalCountOpenAppointmentsQueue = openAppointmentsQueueIdsPaginated.size();

        if (openAppointmentsQueueIdsPaginated.size() >= pageable.getPageSize()) {

            StringBuilder countBuilder = new StringBuilder();
            countBuilder.append("SELECT COUNT(a.id) FROM Appointment a ");
            countBuilder.append("LEFT JOIN a.contemplation c ");
            countBuilder.append("LEFT JOIN a.medicalProcedure mp ");
            countBuilder.append("LEFT JOIN mp.specialty s ");
            countBuilder.append("LEFT JOIN a.patient p ");
            countBuilder.append("LEFT JOIN p.basicHealthUnit ubs ");
            countBuilder.append("WHERE c.appointment IS NULL ");
            countBuilder.append("AND a.status = br.com.tecsus.sccubs.enums.AppointmentStatus.AGUARDANDO ");
            if (ubsId != null) countBuilder.append("AND ubs.id = :ubsId ");
            if (specialtyId != null) countBuilder.append("AND s.id = :specialtyId ");
            if (medicalProcedureId != null) countBuilder.append("AND mp.id = :medicalProcedureId ");


            /*TypedQuery<Long> count = entityManager.createQuery("""
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
                    AND mp.id = :medicalProcedureId
                    AND ubs.id = :ubsId
                    AND a.status = br.com.tecsus.sccubs.enums.AppointmentStatus.AGUARDANDO
            """, Long.class);*/

            TypedQuery<Long> count = entityManager.createQuery(countBuilder.toString(), Long.class);

            if (medicalProcedureId != null) count.setParameter("medicalProcedureId", medicalProcedureId);
            if (ubsId != null) count.setParameter("ubsId", ubsId);
            if(specialtyId != null) count.setParameter("specialtyId", specialtyId);

            totalCountOpenAppointmentsQueue = count.getSingleResult();
        }

        StringBuilder queueBuilder = new StringBuilder();
        queueBuilder.append("SELECT new br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO(");
        queueBuilder.append("a.requestDate, ");
        queueBuilder.append("a.priority, ");
        queueBuilder.append("mp.procedureType, ");
        queueBuilder.append("mp.id, ");
        queueBuilder.append("mp.description, ");
        queueBuilder.append("s.title, ");
        queueBuilder.append("ubs.name, ");
        queueBuilder.append("COALESCE(a.observation, 'Sem observações.'), ");
        queueBuilder.append("a.id, ");
        queueBuilder.append("p.id, ");
        queueBuilder.append("p.name, ");
        queueBuilder.append("p.cpf, ");
        queueBuilder.append("p.gender, ");
        queueBuilder.append("p.birthDate, ");
        queueBuilder.append("p.socialSituationRating) ");
        queueBuilder.append("FROM ");
        queueBuilder.append("Appointment a ");
        queueBuilder.append("LEFT JOIN a.medicalProcedure mp ");
        queueBuilder.append("LEFT JOIN mp.specialty s ");
        queueBuilder.append("LEFT JOIN a.patient p ");
        queueBuilder.append("LEFT JOIN p.basicHealthUnit ubs ");
        queueBuilder.append("WHERE a.id IN :ids ");
        queueBuilder.append("ORDER BY ");
        if (ubsId == null) queueBuilder.append("ubs.name, ");
        if (specialtyId == null) queueBuilder.append("s.title, ");
        if (medicalProcedureId == null) queueBuilder.append("mp.description, ");
        queueBuilder.append("CASE WHEN a.requestDate <= :dateLimit THEN 1 ELSE 2 END ASC, ");
        queueBuilder.append("a.priority ASC, ");
        queueBuilder.append("p.birthDate ASC, ");
        queueBuilder.append("p.socialSituationRating ASC, ");
        queueBuilder.append("a.requestDate ASC");

        TypedQuery<PatientOpenAppointmentDTO> openAppointmentsQueueQuery = entityManager
                .createQuery(queueBuilder.toString(), PatientOpenAppointmentDTO.class);

        /*TypedQuery<PatientOpenAppointmentDTO> openAppointmentsQueueQuery = entityManager.createQuery("""
                SELECT
                    new br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO(
                    a.requestDate,
                    a.priority,
                    mp.procedureType,
                    mp.id,
                    mp.description,
                    s.title,
                    ubs.name,
                    COALESCE(a.observation, 'Sem observações.'),
                    a.id,
                    p.id,
                    p.name,
                    p.cpf,
                    p.gender,
                    p.birthDate,
                    p.socialSituationRating)
                FROM
                    Appointment a
                LEFT JOIN a.medicalProcedure mp
                LEFT JOIN mp.specialty s
                LEFT JOIN a.patient p
                LEFT JOIN p.basicHealthUnit ubs
                WHERE a.id IN :ids
                ORDER BY
                    CASE WHEN a.requestDate <= :dateLimit THEN 1 ELSE 2 END ASC,
                    a.priority ASC,
                    p.birthDate ASC,
                    p.socialSituationRating ASC,
                    a.requestDate ASC
            """, PatientOpenAppointmentDTO.class);*/

        openAppointmentsQueueQuery.setParameter("dateLimit", LocalDateTime.now().minusMonths(QUATRO_MESES));
        openAppointmentsQueueQuery.setParameter("ids", openAppointmentsQueueIdsPaginated);
        var openAppointmentsQueue = openAppointmentsQueueQuery.getResultList();

        return new PageImpl<>(openAppointmentsQueue, pageable, totalCountOpenAppointmentsQueue);
    }
}
