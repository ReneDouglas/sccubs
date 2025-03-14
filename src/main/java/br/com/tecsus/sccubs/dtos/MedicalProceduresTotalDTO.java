package br.com.tecsus.sccubs.dtos;

import br.com.tecsus.sccubs.enums.ProcedureType;

public record MedicalProceduresTotalDTO(String description,
                                        ProcedureType type,
                                        Long total) {
}
