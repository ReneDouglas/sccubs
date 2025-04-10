package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.entities.BasicHealthUnit;
import br.com.tecsus.sigaubs.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long>, PatientRepositoryCustom {

    Patient findByIdAndBasicHealthUnit(Long id, BasicHealthUnit ubs);

}
