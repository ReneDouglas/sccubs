package br.com.tecsus.sccubs.dtos;

import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.enums.ProcedureType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record PatientOpenAppointmentDTO(LocalDateTime requestDate,
                                        Priorities priority,
                                        ProcedureType type,
                                        Long medicalProcedureId,
                                        String procedure,
                                        String specialty,
                                        String observations,
                                        Long appointmentId,
                                        Long patientId) {

    public String formattedRequestDate(){
        return requestDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));
    }
}
