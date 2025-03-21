package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.dtos.MedicalProceduresTotalDTO;
import br.com.tecsus.sccubs.dtos.ProcedureTypeTotalDTO;
import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.Specialty;
import br.com.tecsus.sccubs.enums.ProcedureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface MedicalProcedureRepository extends JpaRepository<MedicalProcedure, Long> {

    @Transactional(readOnly = true)
    List<MedicalProcedure> findAllBySpecialtyAndProcedureType(Specialty specialty, ProcedureType procedureType);

    @Transactional(readOnly = true)
    @Query("""
        SELECT mp
        FROM MedicalProcedure mp
        JOIN FETCH mp.specialty
        WHERE mp.id = :id
    """)
    MedicalProcedure findFetchedMedicalProcedure(Long id);

    @Transactional(readOnly = true)
    @Query("""
        SELECT mp
        FROM MedicalProcedure mp
        WHERE mp.procedureType = :procedureType
        AND mp.specialty.id = :specialtyId
    """)
    MedicalProcedure findMedicalProcedureIdByTypeAndSpecialt(ProcedureType procedureType, Long specialtyId);

}
