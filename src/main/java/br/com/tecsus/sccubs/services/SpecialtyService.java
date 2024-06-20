package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.dtos.ProcedureDTO;
import br.com.tecsus.sccubs.dtos.SpecialtyDTO;
import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.Specialty;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.MedicalProcedureRepository;
import br.com.tecsus.sccubs.repositories.SpecialtyRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final MedicalProcedureRepository medicalProcedureRepository;

    public SpecialtyService(SpecialtyRepository specialtyRepository, MedicalProcedureRepository medicalProcedureRepository) {
        this.specialtyRepository = specialtyRepository;
        this.medicalProcedureRepository = medicalProcedureRepository;
    }

    public List<Specialty> findSpecialties() {
        return specialtyRepository.findAllByOrderByTitleAsc();
    }

    public SpecialtyDTO findFetchedSpecialty(Long id) {

        Specialty specialty = specialtyRepository.loadByIdWithProcedures(id);
        SpecialtyDTO specialtyDTO = new SpecialtyDTO();

        specialtyDTO.setId(specialty.getId());
        specialtyDTO.setTitle(specialty.getTitle());
        specialtyDTO.setDescription(specialty.getDescription());
        specialtyDTO.setActive(specialty.getActive());

        var proceduresDTO = specialty.getMedicalProcedures().stream().map(medicalProcedure -> {
            ProcedureDTO procedureDTO = new ProcedureDTO();
            procedureDTO.setDescription(medicalProcedure.getDescription());
            procedureDTO.setProcedureType(medicalProcedure.getProcedureType().getDescription());
            return procedureDTO;
        }).toList();
        specialtyDTO.setProcedures(proceduresDTO);

        return specialtyDTO;
    }

    @Transactional
    public void registerSpecialty(SpecialtyDTO specialtyDTO, SystemUserDetails loggedUser) throws Exception {

        Specialty specialty = new Specialty();
        Set<MedicalProcedure> procedures = new HashSet<>();
        specialty.setId(null);
        specialty.setTitle(specialtyDTO.getTitle());
        specialty.setDescription(specialtyDTO.getDescription());
        specialty.setActive(true);
        specialty.setCreationUser(loggedUser.getUsername());
        specialty.setCreationDate(LocalDateTime.now());

        for (ProcedureDTO procedureDTO : specialtyDTO.getProcedures()) {
            MedicalProcedure medicalProcedure = new MedicalProcedure();
            medicalProcedure.setId(null);
            medicalProcedure.setDescription(procedureDTO.getDescription());
            medicalProcedure.setProcedureType(ProcedureType.getProcedureTypeByDescription(procedureDTO.getProcedureType()));
            medicalProcedure.setCreationUser(loggedUser.getUsername());
            medicalProcedure.setCreationDate(LocalDateTime.now());
            medicalProcedure.setSpecialty(specialty);
            procedures.add(medicalProcedure);
        }

        specialty.setMedicalProcedures(procedures);
        specialtyRepository.save(specialty);
    }

    @Transactional
    public void updateSpecialty(SpecialtyDTO specialtyDTO, SystemUserDetails loggedUser) throws Exception {

        Specialty specialty = new Specialty();
        specialty.setId(specialtyDTO.getId());
        specialty.setTitle(specialtyDTO.getTitle());
        specialty.setDescription(specialtyDTO.getDescription());
        specialty.setActive(specialtyDTO.getActive());

        specialty = specialtyRepository.save(specialty);

        for (ProcedureDTO procedureDTO : specialtyDTO.getProcedures()) {
            var medicalProcedure = new MedicalProcedure();
            medicalProcedure.setId(null);
            medicalProcedure.setDescription(procedureDTO.getDescription());
            medicalProcedure.setProcedureType(ProcedureType.getProcedureTypeByDescription(procedureDTO.getProcedureType()));
            medicalProcedure.setCreationUser(loggedUser.getUsername());
            medicalProcedure.setCreationDate(LocalDateTime.now());
            medicalProcedure.setSpecialty(specialty);
            medicalProcedureRepository.save(medicalProcedure);
        }
    }
}
