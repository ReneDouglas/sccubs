package br.com.tecsus.sccubs.security.config;

import br.com.tecsus.sccubs.repositories.SystemRoleRepository;
import br.com.tecsus.sccubs.repositories.SystemUserRepository;
import br.com.tecsus.sccubs.services.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static br.com.tecsus.sccubs.security.UrlPatternConfig.*;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.*;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private SystemUserRepository systemUserRepository;
    private SystemRoleRepository systemRoleRepository;

    @Autowired
    public void setSystemRoleRepository(SystemRoleRepository systemRoleRepository) {
        this.systemRoleRepository = systemRoleRepository;
    }

    @Autowired
    public void setSystemUserRepository(SystemUserRepository systemUserRepository) {
        this.systemUserRepository = systemUserRepository;
    }

    // authorization
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authConfig -> {
            authConfig.requestMatchers(PUBLIC_MATCHERS).permitAll();
            authConfig.requestMatchers(PRIVATE_MATCHERS).authenticated();
            //authConfig.anyRequest().authenticated();
        });
        http.formLogin(login -> {
            login.loginPage("/login");
            login.defaultSuccessUrl("/", true);
            //login.failureUrl("/login-error");
        });
        http.logout(logout -> {
            logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
            logout.logoutSuccessUrl("/login");
            logout.permitAll();
            logout.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(COOKIES)));
            logout.invalidateHttpSession(true);
        });
        //http.csrf(AbstractHttpConfigurer::disable);
        http.csrf(Customizer.withDefaults());
        http.httpBasic(withDefaults());
        http.sessionManagement(session -> {
            session.sessionConcurrency(concurrency -> {
                concurrency.maximumSessions(1).expiredUrl("/expired");//.maxSessionsPreventsLogin(true)
            });
        });
        //http.requiresChannel(channel -> channel.anyRequest().requiresSecure()); // habilita https
        //http.requiresChannel(channel -> channel.requestMatchers(new AntPathRequestMatcher("/login")).requiresSecure());

        return http.build();
    }

    // authentication
    @Bean
    public UserDetailsService userDetailsService(){
        return new SystemUserService(systemUserRepository, systemRoleRepository, passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    // Método importante para 'fazer enxergar' o userDetailsService
    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Type");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
