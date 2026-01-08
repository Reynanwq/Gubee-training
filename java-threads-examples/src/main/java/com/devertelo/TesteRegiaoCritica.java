package com.devertelo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TesteRegiaoCritica {

    private static class ContadorProblema {
        private int contador = 0;

        public void incrementarLento() {
            int temp = contador;
            try {
                Thread.sleep(0, 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            temp = temp + 1;
            contador = temp;
        }

        public synchronized void incrementarSincronizado() {
            contador++;
        }

        public int getContador() {
            return contador;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("TESTE FORÇADO DE CONDIÇÃO DE CORRIDA\n");

        testar("Sem sincronização", false);
        Thread.sleep(1000);
        testar("Com sincronização", true);
    }

    private static void testar(String descricao, boolean sincronizado) throws InterruptedException {
        System.out.println("\n" + descricao + ":");
        System.out.println("=".repeat(50));

        ContadorProblema contador = new ContadorProblema();
        int numThreads = 1000;
        int incrementosPorThread = 100;

        ExecutorService executor = Executors.newFixedThreadPool(100);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementosPorThread; j++) {
                    if (sincronizado) {
                        contador.incrementarSincronizado();
                    } else {
                        contador.incrementarLento();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        int valorFinal = contador.getContador();
        int esperado = numThreads * incrementosPorThread;

        System.out.println("Valor final do contador: " + valorFinal);
        System.out.println("Esperado: " + esperado);
        System.out.println("Resultado: " + (valorFinal == esperado ? "CORRETO" : "INCORRETO"));
        if (valorFinal != esperado) {
            System.out.println("Perda de dados: " + (esperado - valorFinal) + " incrementos");
            System.out.println("Perda percentual: " +
                    String.format("%.2f", (double)(esperado - valorFinal)/esperado * 100) + "%");
        }
    }
}