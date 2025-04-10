package br.com.tecsus.sigaubs.repositories.Impl;

import br.com.tecsus.sigaubs.entities.MedicalSlot;
import br.com.tecsus.sigaubs.repositories.MedicalSlotRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public class MedicalSlotRepositoryCustomImpl implements MedicalSlotRepositoryCustom {

    private final EntityManager entityManager;

    @Autowired
    public MedicalSlotRepositoryCustomImpl(JpaContext jpaContext) {
        this.entityManager = jpaContext.getEntityManagerByManagedType(MedicalSlot.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalSlot> findMedicalSlotsPaginated(MedicalSlot medicalSlot, Pageable page) {

        TypedQuery<Long> medicalSlotIdsQueryPaginated = entityManager.createQuery("""
                        SELECT
                            ms.id
                        FROM
                            MedicalSlot ms
                        ORDER BY ms.referenceMonth DESC
        """, Long.class);

        medicalSlotIdsQueryPaginated.setFirstResult(page.getPageNumber() * page.getPageSize());
        medicalSlotIdsQueryPaginated.setMaxResults(page.getPageSize());

        var medicalSlotIdsPaginated = medicalSlotIdsQueryPaginated.getResultList();
        long totalCountMedicalSlots = medicalSlotIdsPaginated.size();

        if (medicalSlotIdsPaginated.size() >= page.getPageSize()) {
            TypedQuery<Long> count = entityManager.createQuery("""
                    SELECT
                        COUNT(ms.id)
                    FROM
                        MedicalSlot ms
            """, Long.class);

            totalCountMedicalSlots = count.getSingleResult();
        }

        TypedQuery<MedicalSlot> medicalSlotsQuery = entityManager.createQuery("""
                    SELECT
                            ms
                    FROM
                            MedicalSlot ms
                    JOIN FETCH ms.basicHealthUnit ubs
                    JOIN FETCH ms.medicalProcedure mp
                    JOIN FETCH mp.specialty s
                    WHERE ms.id IN :ids
                    ORDER BY ms.referenceMonth DESC
        """, MedicalSlot.class);

        medicalSlotsQuery.setParameter("ids", medicalSlotIdsPaginated);
        List<MedicalSlot> medicalSlots = medicalSlotsQuery.getResultList();

        return new PageImpl<>(medicalSlots, page, totalCountMedicalSlots);
    }
}
