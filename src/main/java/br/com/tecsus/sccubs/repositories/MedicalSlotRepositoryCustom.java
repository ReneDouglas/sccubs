package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.MedicalSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicalSlotRepositoryCustom {

    public Page<MedicalSlot> findMedicalSlotsPaginated(MedicalSlot medicalSlot, Pageable pageable);

}
