package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, PatientRepositoryCustom {
}
