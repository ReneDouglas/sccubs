package br.com.tecsus.sigaubs.entities.converters;

import br.com.tecsus.sigaubs.enums.AppointmentStatus;
import br.com.tecsus.sigaubs.enums.Priorities;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class PriorityConverter implements AttributeConverter<Priorities, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Priorities priority) {
        if (priority == null) {
            return null;
        }
        return priority.getValue();
    }

    @Override
    public Priorities convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(Priorities.values())
                .filter(p -> p.getValue() == dbData)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unexpected value: " + dbData));

    }
}
