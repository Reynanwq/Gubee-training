package com.event.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contas")
public class ContaBancariaEntity {

    @Id
    private Long id;
    private double saldo;
    private int totalEventos;

    public ContaBancariaEntity() {}

    public ContaBancariaEntity(Long id) {
        this.id = id;
        this.saldo = 0;
        this.totalEventos = 0;
    }

    public void depositar(double valor) {
        this.saldo += valor;
        this.totalEventos++;
    }

    public void sacar(double valor) {
        this.saldo -= valor;
        this.totalEventos++;
    }

    public void inicializar() {
        this.totalEventos++;
    }

    public Long getId() { return id; }
    public double getSaldo() { return saldo; }
    public int getTotalEventos() { return totalEventos; }

    @Override
    public String toString() {
        return "ContaBancaria{id=%d, saldo=%.2f, totalEventos=%d}".formatted(id, saldo, totalEventos);
    }
}
