package br.com.tecsus.sccubs.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AppointmentStatus {
    AGUARDANDO("Aguardando Contemplação"),
    CANCELADO("Marcação Cancelada/Desistiu"),
    CONTEMPLADO("Paciente Contemplado");

    private final String description;

    AppointmentStatus(String description) {
        this.description = description;
    }

    public static AppointmentStatus getByDescription(String d) {
        return Arrays
                .stream(values())
                .filter(status -> status.description.equals(d))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Erro ao encontrar status."));
    }
}
