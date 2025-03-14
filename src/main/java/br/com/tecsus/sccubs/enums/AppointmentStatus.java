package br.com.tecsus.sccubs.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AppointmentStatus {
    AGUARDANDO("Aguardando Contemplação", false),
    CANCELADO("Marcação Cancelada/Desistiu", false),
    CONTEMPLACAO_AGUARDANDO_CONFIRMACAO("Aguardando Confirmação", true),
    CONTEMPLACAO_CONFIRMACAO_PACIENTE("Confirmado pelo paciente", true),
    CONTEMPLACAO_CONFIRMACAO_SMS("Confirmado pela SMS", true),
    CONTEMPLACAO_CANCELADA_PACIENTE("Cancelado pelo paciente", true),
    CONTEMPLACAO_CANCELADA_SMS("Cancelado pela SMS", true),
    FINALIZADO("Finalizado", true);

    private final String description;
    private final boolean contemplationFilter;

    AppointmentStatus(String description, boolean contemplationFilter) {
        this.description = description;
        this.contemplationFilter = contemplationFilter;
    }

    public static AppointmentStatus getByDescription(String d) {
        return Arrays
                .stream(values())
                .filter(status -> status.description.equals(d))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Erro ao encontrar status."));
    }

    public static List<AppointmentStatus> getContemplationValues() {
        return Arrays
                .stream(values())
                .filter(status -> status.contemplationFilter)
                .toList();
    }
}
