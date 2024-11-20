package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


//@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    @Query("""
            SELECT s
            FROM Specialty s
            LEFT JOIN FETCH s.medicalProcedures
            WHERE s.id = :id
            """)
    public Specialty loadByIdWithProcedures(@Param("id") Long id);

    public List<Specialty> findAllByOrderByTitleAsc();


}
