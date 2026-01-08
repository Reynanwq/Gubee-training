package com.devertelo;

public class ContadorSingleton {
    private static ContadorSingleton instance;
    private int contador = 0;

    private ContadorSingleton() {}

    public static synchronized ContadorSingleton getInstance() {
        if (instance == null) {
            instance = new ContadorSingleton();
        }
        return instance;
    }

    public void incrementar() {
        contador++;
    }

    public synchronized void incrementarSincronizado() {
        contador++;
    }

    public int getContador() {
        return contador;
    }

    public void zerarContador() {
        contador = 0;
    }
}