package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.SystemRole;
import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.enums.SystemMessages;
import br.com.tecsus.sccubs.enums.Roles;
import br.com.tecsus.sccubs.repositories.SystemRoleRepository;
import br.com.tecsus.sccubs.repositories.SystemUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SystemUserService implements UserDetailsService {

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private SystemRoleRepository systemRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, BadCredentialsException {

        SystemUser systemUser = systemUserRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não cadastrado."));

        String uname = systemUser.getUsername();
        String password = systemUser.getPassword(); /*passwordEncoder.encode(systemUser.getPassword());*/
        List<String> authorities = systemUser.getRoles().stream().map(SystemRole::getRole).toList();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        authorities.forEach((role) -> {
            GrantedAuthority auth = new SimpleGrantedAuthority(role);
            grantedAuthorities.add(auth);
        });

        return new User(uname, password, grantedAuthorities);
    }

    public String registerNotAdminUser(SystemUser user) throws Exception {

        SystemRole role = systemRoleRepository.findById(user.getSelectedRoleId())
                .orElseThrow(() -> {
                    log.error("Erro ao encontrar role [id = {}]", user.getSelectedRoleId());
                    return new Exception("Erro ao cadastrar usuário.");
                });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(role));
        user.setCreationDate(new Date());
        user.setCreationUser(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setActive(true);

        try {
            systemUserRepository.save(user);
            log.info("Usuário cadastrado com sucesso.");
            return SystemMessages.SUCCESS_01.getCode();
        } catch (DataIntegrityViolationException e) {
            log.error("Erro ao cadastrar usuário: {}", e.getMessage());
            return SystemMessages.ERROR_02.getCode();
        }

    }

    public List<SystemRole> getRolesNotAdmin() {
        return systemRoleRepository.findByRoleNot(Roles.ROLE_ADMIN.toString());
    }
}
