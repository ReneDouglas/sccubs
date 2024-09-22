package br.com.tecsus.sccubs.dtos;

import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.Specialty;
import br.com.tecsus.sccubs.enums.ProcedureType;
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
