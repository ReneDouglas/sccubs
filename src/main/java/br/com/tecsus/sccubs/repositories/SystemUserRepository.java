package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.SystemUser;
//import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

//@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long>, SystemUserRepositoryCustom {

    @EntityGraph(value = "SystemUserGraph")
    @Transactional(readOnly = true)
    Optional<SystemUser> findByUsername(String username);

    @EntityGraph(value = "SystemUserGraph")
    @Transactional(readOnly = true)
    List<SystemUser> findAllByCreationUser(String creationUser);

    @Override
    @EntityGraph(attributePaths = {"roles", "basicHealthUnit"})
    @Transactional(readOnly = true)
    <S extends SystemUser> Page<S> findAll(Example<S> example, Pageable pageable);

    @Override
    @EntityGraph(value = "SystemUserGraph")
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    Optional<SystemUser> findById(Long aLong);


}
