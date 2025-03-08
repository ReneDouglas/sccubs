package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
public interface BasicHealthUnitRepository extends JpaRepository<BasicHealthUnit, Long> {

    @Query("FROM BasicHealthUnit")
    @Override
    List<BasicHealthUnit> findAll();


}
