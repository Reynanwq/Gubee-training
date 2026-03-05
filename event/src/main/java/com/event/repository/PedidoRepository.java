package com.event.repository;


import com.event.model.Pedido;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Simula um banco de dados em memória
@Repository
public class PedidoRepository {

    private final Map<Long, Pedido> db = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Pedido salvar(String cliente, double valor, List<String> itens) {
        Long id = idGenerator.getAndIncrement();
        Pedido pedido = new Pedido(id, cliente, valor, itens);
        db.put(id, pedido);
        return pedido;
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return Optional.ofNullable(db.get(id));
    }
}
