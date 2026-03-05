package com.event.events.conta;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Padrão 3 - Event Sourcing
// Interface base para todos os eventos da conta
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContaCriadaEvento.class,  name = "CONTA_CRIADA"),
        @JsonSubTypes.Type(value = DepositoEvento.class,     name = "DEPOSITO"),
        @JsonSubTypes.Type(value = SaqueEvento.class,        name = "SAQUE")
})
public sealed interface ContaEvento permits ContaCriadaEvento, DepositoEvento, SaqueEvento {
    Long contaId();
}