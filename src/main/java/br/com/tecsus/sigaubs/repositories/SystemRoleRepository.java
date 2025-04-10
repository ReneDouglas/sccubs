package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.entities.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//@Repository
public interface SystemRoleRepository extends JpaRepository<SystemRole, Long> {

    @Transactional(readOnly = true)
    Optional<SystemRole> findByRole(String role);

    @Transactional(readOnly = true)
    List<SystemRole> findByRoleNot(String role);

    @Transactional(readOnly = true)
    List<SystemRole> findByRoleNotIn(Collection<String> roles);


}
