package br.com.tecsus.sccubs.enums;

import lombok.Getter;

@Getter
public enum Priorities {
    MAIS_DE_3_MESES(1, "Mais de 4 meses", false), // Prioridade passiva
    URGENCIA(2, "Urgência", true), // Prioridade ativa
    RETORNO(3, "Retorno", true), // Prioridade ativa
    //COM_HISTORICO(3, "Com histórico", true), // Prioridade ativa: É utilizado para exames e cirurgias
    IDADE(4, "Idade", false), // Prioridade passiva
    SITUACAO_SOCIAL(5, "Situação Social", false), // Prioridade passiva
    SEXO(6, "Sexo", false), // Prioridade passiva
    ELETIVO(7, "Eletivo", true); // Caso eletivo, será escolhida uma prioridade passiva


    // 1 é o maior nível de prioridade.
    private final int priority;
    private final String description;
    private final Boolean manual;

    Priorities(int priority, String description, Boolean manual) {
        this.priority = priority;
        this.description = description;
        this.manual = manual;
    }
}
