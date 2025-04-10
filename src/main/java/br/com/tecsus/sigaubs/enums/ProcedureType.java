package br.com.tecsus.sigaubs.enums;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public enum ProcedureType {
    CONSULTA("Consulta"),
    EXAME("Exame"),
    CIRURGIA("Cirurgia");

    private final String description;

    ProcedureType(String description) {
        this.description = description;
    }

    public static ProcedureType getProcedureTypeByDescription(String d) {
        return Arrays
                .stream(values())
                .filter(type -> type.description.equals(d))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Erro ao encontrar procedimento."));
    }

    public static List<String> getMedicalProceduresDescription() {
        return Arrays.stream(values()).map(ProcedureType::getDescription).toList();
    }
}
