package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.SystemUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    @EntityGraph(value = "SystemUserGraph")
    @Transactional(readOnly = true)
    Optional<SystemUser> findByUsername(String username);

    @EntityGraph(value = "SystemUserGraph")
    @Transactional(readOnly = true)
    List<SystemUser> findAllByCreationUser(@Param("creationUser") String creationUser);

    @EntityGraph(value = "SystemUserGraph")
    @Override
    Optional<SystemUser> findById(Long aLong);

}
