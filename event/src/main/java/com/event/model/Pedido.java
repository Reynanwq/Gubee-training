package com.event.model;

import java.util.List;

public record Pedido(
        Long id,
        String cliente,
        double valor,
        List<String> itens
) {}