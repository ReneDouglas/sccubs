package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    @Query("""
            SELECT s
            FROM Specialty s
            LEFT JOIN FETCH s.exams
            LEFT JOIN FETCH s.surgeries
            WHERE s.id = :id
            """)
    public Specialty loadByIdWithExamsAndSurgeries(@Param("id") Long id);


}
