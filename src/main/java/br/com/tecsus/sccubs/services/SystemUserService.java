package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.SystemRole;
import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.repositories.SystemRoleRepository;
import br.com.tecsus.sccubs.repositories.SystemUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SystemUserService extends InMemoryUserDetailsManager /*implements UserDetailsService*/ {

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

    public SystemUser register(SystemUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<SystemRole> role = systemRoleRepository.findByRole("ADMINISTRADOR");
        user.setRoles(List.of(role.get()));
        user.setName("Teste teste");
        user.setCreationUser("test");
        user.setCreationDate(new Date());
        user.setEmail("teste@gmail.com");

        return systemUserRepository.save(user);
    }
}
