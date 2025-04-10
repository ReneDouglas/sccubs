package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.entities.SystemUser;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
