package br.com.tecsus.sccubs.config;

import br.com.tecsus.sccubs.services.SystemUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import static br.com.tecsus.sccubs.config.UrlPatternConfig.*;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    // authorization
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authConfig -> {
            authConfig.requestMatchers(PUBLIC_MATCHES).permitAll();
            /*authConfig.requestMatchers("/css/**", "/registration", "/logout", "/login", "/login-error", "/error", "/expired", "/invalid")
                    .permitAll();*/
            authConfig.requestMatchers(HttpMethod.GET, "/user")/*.hasAnyRole("ADMIN", "USER")*/;
            authConfig.requestMatchers(HttpMethod.GET, "/admin")/*.hasRole("ADMIN")*/;
            authConfig.requestMatchers("/user/**");
            authConfig.anyRequest().authenticated();
        });
        http.formLogin(login -> {
            login.loginPage("/login");
            login.defaultSuccessUrl("/", true);
            login.failureUrl("/login-error");
        });
        http.logout(logout -> {
            //logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
            logout.logoutSuccessUrl("/login");
            logout.permitAll();
            logout.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(COOKIES)));
            logout.invalidateHttpSession(true);
        });
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(withDefaults());
        http.sessionManagement(session -> {
            session.sessionConcurrency(concurrency -> {
                concurrency.maximumSessions(1).expiredUrl("/expired")/*.maxSessionsPreventsLogin(true)*/;
            });
            //session.invalidSessionUrl("/invalid"); // quando a sessão expira após um tempo (30 min)
        });

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

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
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
