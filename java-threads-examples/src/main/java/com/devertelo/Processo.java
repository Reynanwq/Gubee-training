package com.devertelo;

import java.time.Duration;

public class Processo {

    public Runnable executar(int processoId, boolean usarSincronizado) {
        return () -> {
            System.out.println(Thread.currentThread() + " executando processo: " + processoId);

            ContadorSingleton contador = ContadorSingleton.getInstance();

            if (usarSincronizado) {
                contador.incrementarSincronizado();
            } else {
                contador.incrementar();
            }

            try {
                Thread.sleep(Duration.ofSeconds(1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println(Thread.currentThread() + " processo finalizado: " + processoId);
        };
    }
}