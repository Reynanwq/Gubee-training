package com.event.controller;

import com.event.model.ContaPontoNoTempoEntity;
import com.event.repository.ContaPontoNoTempoRepository;
import com.event.service.ReplayProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo/replay")
public class ReplayController {

    private final ReplayProcessor replayProcessor;
    private final ContaPontoNoTempoRepository pontoNoTempoRepository;

    public ReplayController(ReplayProcessor replayProcessor,
                            ContaPontoNoTempoRepository pontoNoTempoRepository) {
        this.replayProcessor = replayProcessor;
        this.pontoNoTempoRepository = pontoNoTempoRepository;
    }

    /**
     * Dispara o Replay Processor para reconstruir o estado de uma conta
     * até um momento específico no passado.
     *
     * Exemplo: POST /demo/replay/conta/42?pontoNoTempo=2024-01-10T14:00:00Z
     */
    @PostMapping("/conta/{contaId}")
    public ResponseEntity<Map<String, Object>> executarReplay(
            @PathVariable Long contaId,
            @RequestParam String pontoNoTempo) {

        Instant instant = Instant.parse(pontoNoTempo);
        ContaPontoNoTempoEntity snapshot = replayProcessor.executarReplay(contaId, instant);

        return ResponseEntity.ok(Map.of(
                "padrao", "Event Sourcing - Replay",
                "descricao", "Estado reconstruído a partir dos eventos até o momento informado",
                "contaId", snapshot.getContaId(),
                "pontoNoTempo", snapshot.getPontoNoTempo().toString(),
                "saldoReconstruido", snapshot.getSaldo(),
                "totalEventosAplicados", snapshot.getTotalEventos()
        ));
    }

    /**
     * Consulta todos os snapshots gerados pelo Replay Processor para uma conta.
     * Permite ver o histórico de todas as consultas point-in-time já realizadas.
     *
     * Exemplo: GET /demo/replay/conta/42/historico
     */
    @GetMapping("/conta/{contaId}/historico")
    public ResponseEntity<List<Map<String, Object>>> historico(@PathVariable Long contaId) {

        List<Map<String, Object>> historico = pontoNoTempoRepository
                .findByContaIdOrderByPontoNoTempoDesc(contaId)
                .stream()
                .map(s -> Map.<String, Object>of(
                        "contaId", s.getContaId(),
                        "pontoNoTempo", s.getPontoNoTempo().toString(),
                        "saldo", s.getSaldo(),
                        "totalEventos", s.getTotalEventos()
                ))
                .toList();

        return ResponseEntity.ok(historico);
    }
}