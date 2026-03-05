package com.event.events.conta;

public record SaqueEvento(Long contaId, double valor) implements ContaEvento {}
