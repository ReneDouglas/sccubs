package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasicHealthUnitRepository extends JpaRepository<BasicHealthUnit, Long> {

    @Transactional(readOnly = true)
    @Query("SELECT ubs FROM BasicHealthUnit ubs WHERE ubs.cityHall.id = :id")
    List<BasicHealthUnit> findByCityHallId(@Param("id") Long cityHallId);

}
