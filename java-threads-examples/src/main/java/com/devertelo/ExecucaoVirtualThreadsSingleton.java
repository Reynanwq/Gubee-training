package com.devertelo;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecucaoVirtualThreadsSingleton {

    public static void main(String[] args) {
        ContadorSingleton.getInstance().zerarContador();
        boolean usarSincronizado = false;

        long tempoInicio = System.currentTimeMillis();

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < 1000; i++) {
                var processo = new Processo().executar(i, usarSincronizado);
                executorService.submit(processo);
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int valorFinal = ContadorSingleton.getInstance().getContador();
        System.out.println("Tempo total: " +
                Duration.ofMillis(System.currentTimeMillis() - tempoInicio).toSeconds() + " segundos");
        System.out.println("Valor final do contador: " + valorFinal);
        System.out.println("Esperado: 1000");
        System.out.println("Perda de dados: " + (1000 - valorFinal));
    }
}