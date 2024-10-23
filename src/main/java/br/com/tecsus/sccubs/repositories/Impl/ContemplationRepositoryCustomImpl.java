package br.com.tecsus.sccubs.repositories.Impl;

import br.com.tecsus.sccubs.entities.Contemplation;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.ContemplationRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

public class ContemplationRepositoryCustomImpl implements ContemplationRepositoryCustom {

    private final EntityManager em;

    @Autowired
    public ContemplationRepositoryCustomImpl(JpaContext jpa) {
        this.em = jpa.getEntityManagerByManagedType(Contemplation.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Contemplation> findConsultationsByUBSAndSpecialtyPaginated(ProcedureType type,
                                                                           Long ubsId,
                                                                           Long specialtyId,
                                                                           YearMonth referenceMonth,
                                                                           Boolean confirmed,
                                                                           Pageable pageable) {

        TypedQuery<Long> contemplationIdsQueryPaginated = em.createQuery("""
            SELECT c.id
            FROM Contemplation c
            JOIN c.medicalSlot ms
            JOIN ms.basicHealthUnit ubs
            JOIN ms.medicalProcedure mp
            JOIN c.appointment a
            JOIN a.patient p
            JOIN mp.specialty s
            WHERE ubs.id = :ubsId
            AND s.id = :specialtyId
            AND mp.procedureType = :type
            AND (:confirmed IS NULL OR c.confirmed = :confirmed)
            AND MONTH(ms.referenceMonth) = :month
            AND YEAR(ms.referenceMonth) = :year
            AND c.canceled IS FALSE
            ORDER BY p.name
        """, Long.class);

        contemplationIdsQueryPaginated.setParameter("ubsId", ubsId);
        contemplationIdsQueryPaginated.setParameter("specialtyId", specialtyId);
        contemplationIdsQueryPaginated.setParameter("type", type);
        contemplationIdsQueryPaginated.setParameter("confirmed", confirmed);
        contemplationIdsQueryPaginated.setParameter("month", referenceMonth.getMonthValue());
        contemplationIdsQueryPaginated.setParameter("year", referenceMonth.getYear());

        contemplationIdsQueryPaginated.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        contemplationIdsQueryPaginated.setMaxResults(pageable.getPageSize());

        var contemplationIdsPaginated = contemplationIdsQueryPaginated.getResultList();
        long totalCountContemplations = pageable.getPageSize();

        if (contemplationIdsPaginated.size() >= pageable.getPageSize()) {
            TypedQuery<Long> count = em.createQuery("""
                SELECT COUNT(c.id)
                FROM Contemplation c
                JOIN c.medicalSlot ms
                JOIN ms.basicHealthUnit ubs
                JOIN ms.medicalProcedure mp
                JOIN c.appointment a
                JOIN a.patient p
                JOIN mp.specialty s
                WHERE ubs.id = :ubsId
                AND s.id = :specialtyId
                AND mp.procedureType = :type
                AND (:confirmed IS NULL OR c.confirmed = :confirmed)
                AND MONTH(ms.referenceMonth) = :month
                AND YEAR(ms.referenceMonth) = :year
                AND c.canceled IS FALSE
                ORDER BY p.name
            """, Long.class);

            count.setParameter("ubsId", ubsId);
            count.setParameter("specialtyId", specialtyId);
            count.setParameter("type", type);
            count.setParameter("confirmed", confirmed);
            count.setParameter("month", referenceMonth.getMonthValue());
            count.setParameter("year", referenceMonth.getYear());

            totalCountContemplations = count.getSingleResult();
        }

        TypedQuery<Contemplation> contemplationsQuery = em.createQuery("""
            SELECT c
            FROM Contemplation c
            JOIN FETCH c.medicalSlot ms
            JOIN FETCH ms.basicHealthUnit ubs
            JOIN FETCH ms.medicalProcedure mp
            JOIN FETCH c.appointment a
            JOIN FETCH a.patient p
            JOIN FETCH mp.specialty s
            WHERE c.id IN :contemplationIds
            ORDER BY p.name
        """, Contemplation.class);

        contemplationsQuery.setParameter("contemplationIds", contemplationIdsPaginated);
        var contemplations = contemplationsQuery.getResultList();

        return new PageImpl<>(contemplations, pageable, totalCountContemplations);

    }
}
