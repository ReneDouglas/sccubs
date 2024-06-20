package br.com.tecsus.sccubs.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcedureDTO {
    private String description;
    private String procedureType;

    public ProcedureDTO() {
    }
}
