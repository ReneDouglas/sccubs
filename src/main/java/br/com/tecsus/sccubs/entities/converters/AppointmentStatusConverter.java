package br.com.tecsus.sccubs.entities.converters;

import br.com.tecsus.sccubs.enums.AppointmentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class AppointmentStatusConverter implements AttributeConverter<AppointmentStatus, String> {

    @Override
    public String convertToDatabaseColumn(AppointmentStatus appointmentStatus) {
        if (appointmentStatus == null) {
            return null;
        }
        return appointmentStatus.getDescription();
    }

    @Override
    public AppointmentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(AppointmentStatus.values())
                .filter(status -> status.getDescription().equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Status de agendamento não encontrado para: " + dbData));
    }
}
