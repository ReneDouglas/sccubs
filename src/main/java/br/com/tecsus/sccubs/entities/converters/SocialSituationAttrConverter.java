package br.com.tecsus.sccubs.entities.converters;

import br.com.tecsus.sccubs.enums.SocialSituationRating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SocialSituationAttrConverter implements AttributeConverter<SocialSituationRating, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SocialSituationRating attribute) {

        if (attribute == null) {
            return null;
        }

        return switch (attribute) {
            case ATE_UM_SALARIO_MINIMO -> 1;
            case DE_UM_A_TRES_SALARIOS_MINIMOS -> 2;
            case DE_TRES_A_CINCO_SALARIOS_MINIMOS -> 3;
            case DE_CINCO_A_QUINZE_SALARIOS_MINIMOS -> 4;
            case MAIS_DE_15_SALARIOS_MINIMOS -> 5;
        };
    }

    @Override
    public SocialSituationRating convertToEntityAttribute(Integer dbData) {

        if (dbData == null) {
            return null;
        }

        return switch (dbData) {
            case 1 -> SocialSituationRating.ATE_UM_SALARIO_MINIMO;
            case 2 -> SocialSituationRating.DE_UM_A_TRES_SALARIOS_MINIMOS;
            case 3 -> SocialSituationRating.DE_TRES_A_CINCO_SALARIOS_MINIMOS;
            case 4 -> SocialSituationRating.DE_CINCO_A_QUINZE_SALARIOS_MINIMOS;
            case 5 -> SocialSituationRating.MAIS_DE_15_SALARIOS_MINIMOS;
            default -> throw new IllegalStateException("Unexpected value: " + dbData);
        };
    }
}
