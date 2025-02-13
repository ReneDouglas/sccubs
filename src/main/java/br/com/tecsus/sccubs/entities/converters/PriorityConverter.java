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
            case PRIORITARIO -> 4;
            case IDADE -> 5;
            case SITUACAO_SOCIAL -> 6;
            case SEXO -> 7;
            case ELETIVO -> 8;
            case ADMINISTRATIVO -> 9;
            default -> throw new IllegalStateException("Unexpected value: " + priority);
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
            case 4 -> Priorities.PRIORITARIO;
            case 5 -> Priorities.IDADE;
            case 6 -> Priorities.SITUACAO_SOCIAL;
            case 7 -> Priorities.SEXO;
            case 8 -> Priorities.ELETIVO;
            case 9 -> Priorities.ADMINISTRATIVO;
            default -> throw new IllegalStateException("Unexpected value: " + dbData);
        };

        /*for (Priorities p : Priorities.values()) {
            if (p.getValue() == priority) {
                return p;
            }
        }*/
    }
}
