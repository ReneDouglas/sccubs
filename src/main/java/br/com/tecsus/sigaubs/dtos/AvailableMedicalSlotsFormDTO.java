package br.com.tecsus.sigaubs.dtos;

import br.com.tecsus.sigaubs.entities.MedicalSlot;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AvailableMedicalSlotsFormDTO {

    private List<MedicalSlot> availableMedicalSlots = new ArrayList<>();

    public AvailableMedicalSlotsFormDTO() {
    }

    public void addRow(MedicalSlot availableMedicalSlot) {
        this.availableMedicalSlots.add(availableMedicalSlot);
    }

    public void removeRow(int index) {
        this.availableMedicalSlots.remove(index);
    }

}
