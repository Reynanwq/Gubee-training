package com.event.events.conta;

public record DepositoEvento(Long contaId, double valor) implements ContaEvento {}
