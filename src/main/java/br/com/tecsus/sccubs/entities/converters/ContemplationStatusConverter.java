package br.com.tecsus.sccubs.entities.converters;

import br.com.tecsus.sccubs.enums.ContemplationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class ContemplationStatusConverter implements AttributeConverter<ContemplationStatus, String> {

    @Override
    public String convertToDatabaseColumn(ContemplationStatus contemplationStatus) {
        if (contemplationStatus == null) {
            return null;
        }
        return contemplationStatus.getDescription();
    }

    @Override
    public ContemplationStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(ContemplationStatus.values())
                .filter(status -> status.getDescription().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
