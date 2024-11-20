package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.CityHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

//@Repository
public interface CityHallRepository extends JpaRepository<CityHall, Long> {

    @Transactional(readOnly = true)
    @Query("SELECT ch FROM CityHall ch WHERE ch.id = :id")
    public CityHall findCityHallByIdNoFetch(@Param("id") Long id);

}
