package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.Contemplation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ContemplationRepository extends JpaRepository<Contemplation, Long>, ContemplationRepositoryCustom {

    @Transactional(readOnly = true)
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
    public Contemplation loadFetchedContemplationById(long id);

}
