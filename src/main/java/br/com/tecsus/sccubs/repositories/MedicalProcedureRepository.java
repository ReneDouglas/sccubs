package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.MedicalProcedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface MedicalProcedureRepository extends JpaRepository<MedicalProcedure, Long> {
}
