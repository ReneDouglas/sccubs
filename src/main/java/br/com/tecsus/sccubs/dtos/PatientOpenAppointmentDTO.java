package br.com.tecsus.sccubs.dtos;

import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.enums.SocialSituationRating;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public record PatientOpenAppointmentDTO(LocalDateTime requestDate,
                                        Priorities priority,
                                        ProcedureType procedureType,
                                        Long medicalProcedureId,
                                        String medicalProcedureDescription,
                                        String specialty,
                                        String observations,
                                        Long appointmentId,
                                        Long patientId,
                                        String patientName,
                                        String patientCPF,
                                        String patientGender,
                                        LocalDate patientBirthDate,
                                        SocialSituationRating patientSocialSituationRating) {

    public String formattedRequestDate(){
        return requestDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));
    }

    public String getBirthDateWithAge() {
        if (patientBirthDate == null) {
            return null;
        }
        Period period = Period.between(patientBirthDate, LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = patientBirthDate.format(formatter);
        if (period.getMonths() > 0) {
            return formattedDate + " (" + period.getYears() + " anos e " + period.getMonths() + " meses)";
        }
        return formattedDate + " (" + period.getYears() + " anos)";
    }
}
