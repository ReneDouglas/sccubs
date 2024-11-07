package br.com.tecsus.sccubs.entities.converters;

import br.com.tecsus.sccubs.enums.Priorities;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PriorityConverter implements AttributeConverter<Priorities, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Priorities priority) {
        if (priority == null) {
            return null;
        }
        //return priority.getValue();
        return switch (priority) {
            case MAIS_DE_QUATRO_MESES -> 1;
            case URGENCIA -> 2;
            case RETORNO -> 3;
            case IDADE -> 4;
            case SITUACAO_SOCIAL -> 5;
            case SEXO -> 6;
            case ELETIVO -> 7;
        };
    }

    @Override
    public Priorities convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        return switch (dbData) {
            case 1 -> Priorities.MAIS_DE_QUATRO_MESES;
            case 2 -> Priorities.URGENCIA;
            case 3 -> Priorities.RETORNO;
            case 4 -> Priorities.IDADE;
            case 5 -> Priorities.SITUACAO_SOCIAL;
            case 6 -> Priorities.SEXO;
            case 7 -> Priorities.ELETIVO;
            default -> throw new IllegalStateException("Unexpected value: " + dbData);
        };

        /*for (Priorities p : Priorities.values()) {
            if (p.getValue() == priority) {
                return p;
            }
        }*/
    }
}
