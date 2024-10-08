package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.Contemplation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContemplationRepository extends JpaRepository<Contemplation, Long> {
}
