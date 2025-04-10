package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.entities.MedicalSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicalSlotRepositoryCustom {

    public Page<MedicalSlot> findMedicalSlotsPaginated(MedicalSlot medicalSlot, Pageable pageable);

}
