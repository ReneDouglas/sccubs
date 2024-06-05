package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.CityHall;
import br.com.tecsus.sccubs.entities.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CityHallRepository extends JpaRepository<CityHall, Long> {
}
