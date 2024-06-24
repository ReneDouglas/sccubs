package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.Specialty;
import br.com.tecsus.sccubs.enums.ProcedureType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MedicalProcedureRepository extends JpaRepository<MedicalProcedure, Long> {

    List<MedicalProcedure> findAllBySpecialtyAndProcedureType(Specialty specialty, ProcedureType procedureType);
}
