package br.com.tecsus.sccubs.enums;

import java.util.Arrays;

public enum SystemMessages {
    SUCCESS_01("scc01", "Cadastro realizado com sucesso."),
    ERROR_01("err01","Erro ao realizar o cadastro."),
    ERROR_02("err02","Já cadastrado no sistema.");

    private final String code;
    private final String description;

    SystemMessages(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescription(String code) {
        return Arrays.stream(values())
                .filter(msg -> msg.getCode().equals(code))
                .findFirst()
                .get()
                .getDescription();
    }
}
