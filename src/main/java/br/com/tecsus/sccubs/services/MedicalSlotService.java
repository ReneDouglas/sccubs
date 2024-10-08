package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.dtos.AvailableMedicalSlotsFormDTO;
import br.com.tecsus.sccubs.entities.MedicalSlot;
import br.com.tecsus.sccubs.repositories.MedicalSlotRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.exceptions.DistinctAvailableMedicalSlotException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MedicalSlotService {

    private final MedicalSlotRepository medicalSlotRepository;

    public MedicalSlotService(MedicalSlotRepository medicalSlotRepository) {
        this.medicalSlotRepository = medicalSlotRepository;
    }

    @Transactional
    public void registerAvailableMedicalSlotsBatch(AvailableMedicalSlotsFormDTO availableMedicalSlotsFormDTO, SystemUserDetails loggedUser) throws DistinctAvailableMedicalSlotException {

        long referenceUbsId = availableMedicalSlotsFormDTO.getAvailableMedicalSlots().get(0).getBasicHealthUnit().getId();
        boolean isDistinct = availableMedicalSlotsFormDTO.getAvailableMedicalSlots().stream().anyMatch(slotUbs -> !slotUbs.getBasicHealthUnit().getId().equals(referenceUbsId));

        if (isDistinct) {
            throw new DistinctAvailableMedicalSlotException("Cadastre as vagas para uma UBS de cada vez.");
        }


        for (MedicalSlot medicalSlot : availableMedicalSlotsFormDTO.getAvailableMedicalSlots()) {
            medicalSlot.setCurrentSlots(medicalSlot.getTotalSlots());
            medicalSlot.setCreationUser(loggedUser.getName());
            medicalSlot.setCreationDate(LocalDateTime.now());
        }

        medicalSlotRepository.saveAll(availableMedicalSlotsFormDTO.getAvailableMedicalSlots());

    }

    public Page<MedicalSlot> findMedicalSlotsPaginated(Pageable page) {
        return medicalSlotRepository.findMedicalSlotsPaginated(null, page);
    }
}
