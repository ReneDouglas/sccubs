package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.SystemRole;
import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.enums.Roles;
import br.com.tecsus.sccubs.repositories.SystemRoleRepository;
import br.com.tecsus.sccubs.repositories.SystemUserRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SystemUserService implements UserDetailsService {

    private final SystemUserRepository systemUserRepository;
    private final SystemRoleRepository systemRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SystemUserService(SystemUserRepository systemUserRepository, SystemRoleRepository systemRoleRepository, PasswordEncoder passwordEncoder) {
        this.systemUserRepository = systemUserRepository;
        this.systemRoleRepository = systemRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, BadCredentialsException {

        SystemUser systemUser = systemUserRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não cadastrado."));

        return new SystemUserDetails(
                systemUser.getUsername(),
                systemUser.getPassword(),
                systemUser.getRoles().stream().map(SystemRole::getRole).map(SimpleGrantedAuthority::new).collect(Collectors.toSet()),
                systemUser.getName(),
                systemUser.getEmail(),
                systemUser.getActive(),
                (systemUser.getCityHall() != null) ? systemUser.getCityHall().getId() : null,
                (systemUser.getCityHall() != null) ? systemUser.getCityHall().getName() : null,
                (systemUser.getBasicHealthUnit() != null) ? systemUser.getBasicHealthUnit().getId() : null
                );

    }

    public void registerNotAdminSystemUser(SystemUser systemUser) throws Exception {

        SystemRole role = systemRoleRepository.findById(systemUser.getSelectedRoleId())
                .orElseThrow(() -> {
                    log.error("[insert user] Erro ao encontrar role [id = {}]", systemUser.getSelectedRoleId());
                    return new Exception("Erro ao cadastrar usuário.");
                });

        systemUser.setPassword(passwordEncoder.encode(systemUser.getPassword()));
        systemUser.setRoles(Set.of(role));
        systemUser.setCreationDate(new Date());
        systemUser.setCreationUser(SecurityContextHolder.getContext().getAuthentication().getName());
        systemUser.setActive(true);

        systemUserRepository.save(systemUser);

    }

    public void updateNotAdminSystemUser(SystemUser systemUser) throws Exception {

        SystemRole role = systemRoleRepository.findById(systemUser.getSelectedRoleId())
                .orElseThrow(() -> {
                    log.error("[update user] Erro ao encontrar role [id = {}]", systemUser.getSelectedRoleId());
                    return new Exception("Erro ao cadastrar usuário.");
                });

        systemUser.setPassword(passwordEncoder.encode(systemUser.getPassword()));
        systemUser.setUpdateUser(SecurityContextHolder.getContext().getAuthentication().getName());
        systemUser.setRoles(Set.of(role));
        systemUser.setUpdateDate(new Date());
        systemUser.setActive(systemUser.getActive());

        systemUserRepository.save(systemUser);
    }

    public List<SystemRole> getRolesNotAdmin() {
        return systemRoleRepository.findByRoleNot(Roles.ROLE_ADMIN.toString());
    }

    public List<SystemRole> getRolesNotAdminAndNotManagement() {
        return systemRoleRepository.findByRoleNotIn(List.of(Roles.ROLE_ADMIN.toString(), Roles.ROLE_SMS.toString()));
    }

    public List<SystemUser> findAllUsersByCreationUser() {
        return systemUserRepository.findAllByCreationUser(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional(readOnly = true)
    public SystemUser findSystemUserById(Long id) {
        return systemUserRepository.findById(id).get();
    }


    @Transactional
    public void deleteNotAdminSystemUser(Long id) throws Exception{
        SystemUser systemUser = systemUserRepository.findById(id).orElseThrow(() -> {
            log.error("Usuário [id = {}] não encontrado.", id);
            return new Exception("Erro ao deletar usuário.");
        });
        systemUserRepository.delete(systemUser);
    }
}
