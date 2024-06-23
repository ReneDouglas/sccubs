package br.com.tecsus.sccubs.enums;

import lombok.Getter;

@Getter
public enum Priorities {
    MAIS_DE_3_MESES(1, "Mais de 3 meses"), // Prioridade passiva
    URGENCIA(2, "Urgência"), // Prioridade ativa
    RETORNO(3, "Retorno"), // Prioridade ativa
    COM_HISTORICO(3, "Com histórico"), // Prioridade ativa: É utilizado para exames e cirurgias
    IDADE(4, "Idade"), // Prioridade passiva
    SITUACAO_SOCIAL(5, "Situação Social"), // Prioridade passiva
    SEXO(6, "Sexo"), // Prioridade passiva
    ELETIVO(7, "Eletivo"); // Caso eletivo, será escolhida uma prioridade passiva


    // 1 é o maior nível de prioridade.
    private final int priority;
    private final String description;

    Priorities(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }
}
