package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SystemUserRepositoryCustom {

    Page<SystemUser> findSystemUsersPaginated(SystemUser systemUser, Pageable pageable);

}
