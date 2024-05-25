package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemRoleRepository extends JpaRepository<SystemRole, Long> {

    Optional<SystemRole> findByRole(String role);

}
