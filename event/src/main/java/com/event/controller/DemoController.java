package com.event.controller;

import com.event.consumer.EventSourcingConsumer;
import com.event.model.ContaBancaria;
import com.event.model.Pedido;
import com.event.producer.CarriedDataProducer;
import com.event.producer.EventSourcingProducer;
import com.event.producer.NotificationProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final NotificationProducer notificationProducer;
    private final CarriedDataProducer carriedDataProducer;
    private final EventSourcingProducer eventSourcingProducer;
    private final EventSourcingConsumer eventSourcingConsumer;

    private final AtomicLong pedidoIdCarried = new AtomicLong(100);

    public DemoController(NotificationProducer notificationProducer,
                          CarriedDataProducer carriedDataProducer,
                          EventSourcingProducer eventSourcingProducer,
                          EventSourcingConsumer eventSourcingConsumer) {
        this.notificationProducer = notificationProducer;
        this.carriedDataProducer = carriedDataProducer;
        this.eventSourcingProducer = eventSourcingProducer;
        this.eventSourcingConsumer = eventSourcingConsumer;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PADRÃO 1 - Event Notification
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/notification/pedido")
    public ResponseEntity<Map<String, Object>> criarPedidoNotification(
            @RequestParam(defaultValue = "Reynan") String cliente,
            @RequestParam(defaultValue = "250.0") double valor,
            @RequestParam(defaultValue = "Notebook,Mouse") String itens) {

        List<String> listaItens = List.of(itens.split(","));
        Pedido pedido = notificationProducer.criarPedidoENotificar(cliente, valor, listaItens);

        return ResponseEntity.ok(Map.of(
                "padrao", "Event Notification",
                "descricao", "Apenas o ID foi publicado no Kafka. O consumer vai buscar os dados.",
                "eventoPublicado", Map.of("pedidoId", pedido.id()),
                "dadosNoBanco", pedido
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PADRÃO 2 - Event-Carried Data Transfero
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/carried-data/pedido")
    public ResponseEntity<Map<String, Object>> criarPedidoCarriedData(
            @RequestParam(defaultValue = "Reynan") String cliente,
            @RequestParam(defaultValue = "250.0") double valor,
            @RequestParam(defaultValue = "Notebook,Mouse") String itens) {

        Long id = pedidoIdCarried.getAndIncrement();
        List<String> listaItens = List.of(itens.split(","));
        carriedDataProducer.criarPedidoEPublicar(id, cliente, valor, listaItens);

        return ResponseEntity.ok(Map.of(
                "padrao", "Event-Carried Data Transfer",
                "descricao", "Todos os dados foram publicados no Kafka. O consumer não precisa buscar nada.",
                "eventoPublicado", Map.of(
                        "pedidoId", id,
                        "cliente", cliente,
                        "valor", valor,
                        "itens", listaItens
                )
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PADRÃO 3 - Event Sourcing
    // POST /demo/event-sourcing/conta/{id}/criar
    // POST /demo/event-sourcing/conta/{id}/depositar?valor=300
    // POST /demo/event-sourcing/conta/{id}/sacar?valor=100
    // GET  /demo/event-sourcing/conta/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/event-sourcing/conta/{contaId}/criar")
    public ResponseEntity<Map<String, Object>> criarConta(@PathVariable Long contaId) {
        eventSourcingProducer.criarConta(contaId);
        return ResponseEntity.ok(Map.of(
                "padrao", "Event Sourcing",
                "acao", "ContaCriada",
                "eventoGravado", Map.of("contaId", contaId),
                "descricao", "Evento gravado no Kafka. O estado será reconstruído pelo consumer."
        ));
    }

    @PostMapping("/event-sourcing/conta/{contaId}/depositar")
    public ResponseEntity<Map<String, Object>> depositar(
            @PathVariable Long contaId,
            @RequestParam double valor) {
        eventSourcingProducer.depositar(contaId, valor);
        return ResponseEntity.ok(Map.of(
                "padrao", "Event Sourcing",
                "acao", "Deposito",
                "eventoGravado", Map.of("contaId", contaId, "valor", valor)
        ));
    }

    @PostMapping("/event-sourcing/conta/{contaId}/sacar")
    public ResponseEntity<Map<String, Object>> sacar(
            @PathVariable Long contaId,
            @RequestParam double valor) {
        eventSourcingProducer.sacar(contaId, valor);
        return ResponseEntity.ok(Map.of(
                "padrao", "Event Sourcing",
                "acao", "Saque",
                "eventoGravado", Map.of("contaId", contaId, "valor", valor)
        ));
    }

    @GetMapping("/event-sourcing/conta/{contaId}")
    public ResponseEntity<?> consultarConta(@PathVariable Long contaId) {
        ContaBancaria conta = eventSourcingConsumer.getContas().get(contaId);
        if (conta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
                "contaId", conta.getId(),
                "saldo", conta.getSaldo(),
                "totalEventosAplicados", conta.getTotalEventos(),
                "descricao", "Saldo reconstruído a partir da sequência de eventos no Kafka"
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Executabos 3 padrões de uma vez
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/executar-tudo")
    public ResponseEntity<Map<String, Object>> executarTudo() throws InterruptedException {
        // Padrão 1
        Pedido p1 = notificationProducer.criarPedidoENotificar("Reynan", 250.0, List.of("Notebook"));

        // Padrão 2
        Long id2 = pedidoIdCarried.getAndIncrement();
        carriedDataProducer.criarPedidoEPublicar(id2, "Reynan", 500.0, List.of("Monitor", "Teclado"));

        // Padrão 3
        eventSourcingProducer.criarConta(42L);
        eventSourcingProducer.depositar(42L, 300.0);
        eventSourcingProducer.depositar(42L, 200.0);
        eventSourcingProducer.sacar(42L, 100.0);
        eventSourcingProducer.depositar(42L, 100.0);

        return ResponseEntity.ok(Map.of(
                "mensagem", "Os 3 padrões foram disparados! Verifique os logs para acompanhar o fluxo.",
                "endpoints", Map.of(
                        "consultarContaEventSourcing", "/demo/event-sourcing/conta/42",
                        "dica", "Aguarde 1-2s e consulte o estado reconstruído da conta 42"
                )
        ));
    }
}