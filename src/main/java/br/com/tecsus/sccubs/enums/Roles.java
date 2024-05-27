package br.com.tecsus.sccubs.enums;

public enum Roles {
    ROLE_ADMIN("Administrador do Sistema", false),
    ROLE_USER("Usuário", true),
    ROLE_ATENDENTE("Atendente", true),
    ROLE_ENFERMEIRO("Enfermeiro", true),
    ROLE_ACS("ACS", true),
    ROLE_SMS("Gestão", true);

    private final String description;
    private final Boolean permission;

    Roles(String description, boolean permission) {
        this.description = description;
        this.permission = permission;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getPermission() {
        return permission;
    }
}
