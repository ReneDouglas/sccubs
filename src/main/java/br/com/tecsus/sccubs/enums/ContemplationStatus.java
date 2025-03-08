package br.com.tecsus.sccubs.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ContemplationStatus {
    CONTEMPLACAO_AGUARDANDO_CONFIRMACAO("Aguardando Confirmação"),
    CONTEMPLACAO_CONFIRMACAO_PACIENTE("Confirmado pelo paciente"),
    CONTEMPLACAO_CONFIRMACAO_SMS("Confirmado pela SMS"),
    CONTEMPLACAO_CANCELADA_PACIENTE("Cancelado pelo paciente"),
    CONTEMPLACAO_CANCELADA_SMS("Cancelado pela SMS"),
    FINALIZADO("Finalizado");

    private final String description;

    ContemplationStatus(String description) {
        this.description = description;
    }

    public static ContemplationStatus getByDescription(String d) {
        return Arrays
                .stream(values())
                .filter(status -> status.description.equals(d))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Erro ao encontrar status."));
    }
}
