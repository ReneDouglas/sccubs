package br.com.tecsus.sccubs.dtos;

import br.com.tecsus.sccubs.enums.AppointmentStatus;
import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.enums.ProcedureType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record PatientAppointmentsHistoryDTO(LocalDateTime requestDate,
                                            LocalDateTime contemplationDate,
                                            Priorities contemplatedBy,
                                            Priorities priorityRegistered,
                                            AppointmentStatus appointmentStatus,
                                            ProcedureType procedureType,
                                            String medicalProcedure,
                                            String specialty,
                                            String observations,
                                            Long medicalProcedureId,
                                            Long appointmentId,
                                            Long contemplationId,
                                            Long patientId) {

    public String formattedRequestDate(){
        if (requestDate == null) {
            return "";
        }
        return requestDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    public String formattedContemplationDate(){
        if (contemplationDate == null) {
            return "";
        }
        return contemplationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String nullableContemplatedBy() {
        if (contemplatedBy == null) {
            return "";
        }
        return contemplatedBy.getDescription();
    }

}
