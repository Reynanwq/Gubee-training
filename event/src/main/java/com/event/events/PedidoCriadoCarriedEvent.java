package com.event.events;

import java.util.List;

public record PedidoCriadoCarriedEvent(
        Long pedidoId,
        String cliente,
        double valor,
        List<String> itens
) {}