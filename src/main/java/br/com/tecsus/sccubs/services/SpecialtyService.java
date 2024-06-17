package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.Specialty;
import br.com.tecsus.sccubs.repositories.SpecialtyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyService(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    public List<Specialty> findSpecialties() {
        return specialtyRepository.findAll();
    }

    public Specialty findFetchedSpecialties(Long id) {
        return specialtyRepository.loadByIdWithExamsAndSurgeries(id);
    }
}
