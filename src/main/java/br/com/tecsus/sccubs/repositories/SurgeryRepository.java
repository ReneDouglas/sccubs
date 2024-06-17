package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
}
