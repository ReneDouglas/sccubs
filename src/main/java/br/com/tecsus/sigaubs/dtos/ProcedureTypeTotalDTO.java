package br.com.tecsus.sigaubs.dtos;

import br.com.tecsus.sigaubs.enums.ProcedureType;

public record ProcedureTypeTotalDTO(ProcedureType procedureType,
                                    Long total) {
}
