package br.com.tecsus.sigaubs.dtos;

import br.com.tecsus.sigaubs.entities.MedicalProcedure;
import br.com.tecsus.sigaubs.enums.ProcedureType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableMedicalSlotRowDTO {

    private int totalSlots;
    private ProcedureType procedureType;
    private MedicalProcedure medicalProcedure;

    public AvailableMedicalSlotRowDTO() {
    }
}
