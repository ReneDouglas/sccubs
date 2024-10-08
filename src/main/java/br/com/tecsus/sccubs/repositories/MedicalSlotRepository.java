package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.MedicalSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalSlotRepository extends JpaRepository<MedicalSlot, Long>, MedicalSlotRepositoryCustom {

}
