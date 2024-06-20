package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.dtos.UBSsystemUserDTO;
import br.com.tecsus.sccubs.entities.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SystemUserRepositoryCustom {

    Page<SystemUser> findSystemUsersPaginated(SystemUser systemUser, Pageable pageable);
    List<UBSsystemUserDTO> findSystemUsersNameByNameContains(String name, Long cityId);

}
