package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import br.com.tecsus.sccubs.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long>, PatientRepositoryCustom {

    public Patient findByIdAndBasicHealthUnit(Long id, BasicHealthUnit ubs);

}
