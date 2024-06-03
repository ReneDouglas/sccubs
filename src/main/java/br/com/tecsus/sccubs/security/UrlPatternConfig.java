package br.com.tecsus.sccubs.security;

public final class UrlPatternConfig {

    public static final String[] PUBLIC_MATCHERS = {
            "/css/**",
            "/images/**",
            "/js/**",
            "/login",
            "/login-error",
            "/logout",
            "/error",
            "/expired",
            "/webjars/**"
    };

}
