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
            "/webjars/**",
            "/favicon.ico",
            /*"/actuator/**"*/
    };

    public static final String[] PRIVATE_MATCHERS = {
            "/",
            "/systemUser-insert/**",
            "/systemUser-list/**",
            "/basicHealthUnit-management/**",
            "/patient-management/**",
            "/patient-list/**",
            "/appointment-management/**",
            "/specialty-management/**",
            "/medicalSlot-management/**",
            "/contemplation-management/**"
    };

}
