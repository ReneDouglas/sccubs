package br.com.tecsus.sccubs.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum SocialSituationRating {
    ATE_UM_SALARIO_MINIMO(1, "Até um salário mínimo"),
    DE_UM_A_TRES_SALARIOS_MINIMOS(2, "De um a três salários mínimos"),
    DE_TRES_A_CINCO_SALARIOS_MINIMOS(3, "De três a cinco salários mínimos"),
    DE_CINCO_A_QUINZE_SALARIOS_MINIMOS(4, "De cinco a quinze salários mínimos"),
    MAIS_DE_15_SALARIOS_MINIMOS(5, "Mais de quinze salários mínimos");

    // Crescente, onde 1 é o maior nível de prioridade.
    private final int priority;
    private final String description;

    SocialSituationRating(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }

    public static List<String> getDescriptionSortedByRating() {
        return Arrays.stream(values()).map(SocialSituationRating :: getDescription).toList();
    }
}
