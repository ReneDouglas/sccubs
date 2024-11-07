package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.MedicalSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface MedicalSlotRepository extends JpaRepository<MedicalSlot, Long>, MedicalSlotRepositoryCustom {

    @Transactional(readOnly = true)
    MedicalSlot findByMedicalProcedureAndBasicHealthUnitAndContemplationsIsNull(MedicalProcedure medicalProcedure, BasicHealthUnit basicHealthUnit);

}
