package br.com.tecsus.sigaubs.dtos;

import br.com.tecsus.sigaubs.enums.ProcedureType;

public record MedicalProceduresTotalDTO(String description,
                                        ProcedureType type,
                                        Long total) {
}
