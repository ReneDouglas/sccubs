package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.entities.BasicHealthUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//@Repository
public interface BasicHealthUnitRepository extends JpaRepository<BasicHealthUnit, Long> {

    @Query("FROM BasicHealthUnit")
    @Override
    List<BasicHealthUnit> findAll();


}
