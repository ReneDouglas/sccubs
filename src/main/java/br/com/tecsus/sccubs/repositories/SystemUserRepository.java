package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    @Transactional(readOnly = true)
    Optional<SystemUser> findByUsername(String username);

}
