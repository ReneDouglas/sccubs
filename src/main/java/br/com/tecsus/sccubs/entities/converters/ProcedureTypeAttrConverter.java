package br.com.tecsus.sccubs.entities.converters;

import br.com.tecsus.sccubs.enums.ProcedureType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ProcedureTypeAttrConverter implements AttributeConverter<ProcedureType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ProcedureType attribute) {

        if (attribute == null) {
            return null;
        }

        return switch (attribute) {
            case CONSULTA -> 1;
            case EXAME -> 2;
            case CIRURGIA -> 3;
        };
    }

    @Override
    public ProcedureType convertToEntityAttribute(Integer dbData) {

        if (dbData == null) {
            return null;
        }

        return switch (dbData) {
            case 1 -> ProcedureType.CONSULTA;
            case 2 -> ProcedureType.EXAME;
            case 3 -> ProcedureType.CIRURGIA;
            default -> throw new IllegalStateException("Unexpected value: " + dbData);
        };
    }
}
