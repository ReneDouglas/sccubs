package br.com.tecsus.sccubs.dtos;

import br.com.tecsus.sccubs.enums.ProcedureType;

public record ProcedureTypeTotalDTO(ProcedureType procedureType,
                                    long total) {
}
