package com.event.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conta_ponto_no_tempo")
public class ContaPontoNoTempoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contaId;
    private double saldo;
    private int totalEventos;

    // Momento exato até onde o replay foi executado
    private Instant pontoNoTempo;

    public ContaPontoNoTempoEntity() {}

    public ContaPontoNoTempoEntity(Long contaId, double saldo, int totalEventos, Instant pontoNoTempo) {
        this.contaId = contaId;
        this.saldo = saldo;
        this.totalEventos = totalEventos;
        this.pontoNoTempo = pontoNoTempo;
    }

    public Long getId() { return id; }
    public Long getContaId() { return contaId; }
    public double getSaldo() { return saldo; }
    public int getTotalEventos() { return totalEventos; }
    public Instant getPontoNoTempo() { return pontoNoTempo; }
}