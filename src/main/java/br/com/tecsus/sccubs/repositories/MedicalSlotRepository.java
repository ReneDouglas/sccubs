package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.MedicalSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MedicalSlotRepository extends JpaRepository<MedicalSlot, Long>, MedicalSlotRepositoryCustom {

    @Transactional(readOnly = true)
    MedicalSlot findByMedicalProcedureAndBasicHealthUnitAndContemplationsIsNull(MedicalProcedure medicalProcedure, BasicHealthUnit basicHealthUnit);

    @Transactional(readOnly = true)
    @Query("""
        SELECT ms
        FROM MedicalSlot ms
        WHERE ms.medicalProcedure.id = :medicalProcedureId
        AND ms.basicHealthUnit.id = :ubsId
        AND ms.currentSlots < ms.totalSlots
    """)
    Optional<MedicalSlot> findAvailableSlotsByMedicalProcedureAndUBS(Long medicalProcedureId, Long ubsId);

    @Transactional(readOnly = true)
    @Query("""
        SELECT ms
        FROM MedicalSlot ms
        LEFT JOIN FETCH ms.medicalProcedure
        LEFT JOIN FETCH ms.basicHealthUnit
        WHERE MONTH(ms.referenceMonth) = MONTH(NOW())
        AND ms.currentSlots > 0
    """)
    List<MedicalSlot> findAllAvailableSlotsByReferenceMonth();

}
