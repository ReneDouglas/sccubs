package br.com.tecsus.sccubs.config;

import br.com.tecsus.sccubs.services.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    // authorization
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authConfig -> {
            authConfig.requestMatchers("/", "/css/**", "/registration", "/logout", "/login", "/login-error", "/error")
                    .permitAll();
            authConfig.requestMatchers(HttpMethod.GET, "/user")/*.hasAnyRole("ADMIN", "USER")*/;
            authConfig.requestMatchers(HttpMethod.GET, "/admin")/*.hasRole("ADMIN")*/;
            authConfig.anyRequest().authenticated();
        }).formLogin(login -> {
            login.loginPage("/login");
            login.defaultSuccessUrl("/home", true);
            login.failureUrl("/login-error");
        }).logout(logout -> {
            //logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
            logout.logoutSuccessUrl("/login");
            logout.permitAll();
            logout.deleteCookies("JSESSIONID");
            logout.invalidateHttpSession(true);
        }).csrf(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults());

        /*http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/home", "/styles/**", "/registration", "/hello", "/logout", "/login", "/login-error")
                        .permitAll()
                        .requestMatchers("/user").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/admin").hasAuthority("ADMIN")
                        .anyRequest()
                        .authenticated()
                ).formLogin(login -> {
                    login.loginPage("/login");
                    login.defaultSuccessUrl("/hello", true);
                    login.failureUrl("/login-error");
                }).logout(logout -> {
                            //logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
                            logout.permitAll();
                            logout.logoutSuccessUrl("/login");
                            logout.deleteCookies("JSESSIONID");
                            logout.invalidateHttpSession(true);
                }).csrf().disable();*/

        return http.build();
    }

    // authentication
    @Bean
    public UserDetailsService userDetailsService(){
        return new SystemUserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Método importante para 'fazer enxergar' o userDetailsService
    /*@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }*/
    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

}
