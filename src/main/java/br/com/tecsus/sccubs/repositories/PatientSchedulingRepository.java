package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.PatientScheduling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientSchedulingRepository extends JpaRepository<PatientScheduling, Long> {
}
