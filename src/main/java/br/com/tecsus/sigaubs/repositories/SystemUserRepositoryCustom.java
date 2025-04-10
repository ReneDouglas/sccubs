package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.dtos.UBSsystemUserDTO;
import br.com.tecsus.sigaubs.entities.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SystemUserRepositoryCustom {

    Page<SystemUser> findSystemUsersPaginated(SystemUser systemUser, Pageable pageable);
    List<UBSsystemUserDTO> findSystemUsersNameByNameContains(String name);

}
