package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.entities.Contemplation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContemplationRepository extends JpaRepository<Contemplation, Long>, ContemplationRepositoryCustom {

    @Query("""
           SELECT c
            FROM Contemplation c
            JOIN FETCH c.medicalSlot ms
            JOIN FETCH ms.basicHealthUnit ubs
            JOIN FETCH ms.medicalProcedure mp
            JOIN FETCH c.appointment a
            JOIN FETCH a.patient p
            JOIN FETCH mp.specialty s
            WHERE c.id = :id
    """)
    Contemplation loadFetchedContemplationById(Long id);

}
