import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * EXEMPLO SIMPLES: PROXY DE LOG EM PRODUTO
 *
 * O que faz: Adiciona LOG toda vez que um produto e adicionado
 */
public class ExemploSimplesProduto {

    // 1. INTERFACE (obrigatorio para proxy)

    interface Produto {
        void adicionar(String nome, double preco);
    }

    // 2. CLASSE REAL (faz o trabalho de verdade)

    static class ProdutoReal implements Produto {
        private List<String> produtos = new ArrayList<>();

        @Override
        public void adicionar(String nome, double preco) {
            produtos.add(nome + " - R$ " + preco);
        }
    }

    // 3. HANDLER (adiciona o LOG)

    static class LogHandler implements InvocationHandler {
        private Object objetoReal;

        public LogHandler(Object objetoReal) {
            this.objetoReal = objetoReal;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // LOG ANTES
            String hora = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            System.out.println("\n   [LOG] Adicionando produto...");
            System.out.println("      Hora: " + hora);
            System.out.println("      Produto: " + args[0]);
            System.out.println("      Preco: R$ " + args[1]);

            // EXECUTA O METODO REAL
            Object resultado = method.invoke(objetoReal, args);

            // LOG DEPOIS
            System.out.println("      [LOG] Adicao concluida!");

            return resultado;
        }
    }

    // 4. MAIN - TESTE

    public static void main(String[] args) {

        System.out.println("================================================");
        System.out.println("  PROXY SIMPLES: LOG DE PRODUTOS");
        System.out.println("================================================");

        // SEM PROXY (normal)

        System.out.println("\n[SEM PROXY]:");
        System.out.println("--------------------------------------------------");

        Produto semProxy = new ProdutoReal();
        semProxy.adicionar("Mouse", 50.00);

        System.out.println("\n   (Nao teve log detalhado)");

        // COM PROXY (com log)

        System.out.println("\n\n[COM PROXY]:");
        System.out.println("--------------------------------------------------");

        // CRIAR O PROXY (3 passos)

        // 1. Objeto real
        Produto objetoReal = new ProdutoReal();

        // 2. Handler
        LogHandler handler = new LogHandler(objetoReal);

        // 3. Proxy
        Produto comProxy = (Produto) Proxy.newProxyInstance(
                Produto.class.getClassLoader(),
                new Class<?>[]{Produto.class},
                handler
        );

        // USAR
        comProxy.adicionar("Teclado", 150.00);
        comProxy.adicionar("Monitor", 800.00);

        // EXPLICACAO

        System.out.println("\n\n==================================================");
        System.out.println("O QUE ACONTECEU:");
        System.out.println("==================================================");
        System.out.println();
        System.out.println("Sem Proxy:");
        System.out.println("  Cliente -> adicionar() -> Salva");
        System.out.println();
        System.out.println("Com Proxy:");
        System.out.println("  Cliente -> Proxy -> Handler");
        System.out.println("                    |-- Log ANTES");
        System.out.println("                    |-- adicionar() -> Salva");
        System.out.println("                    |-- Log DEPOIS");
        System.out.println();
        System.out.println("O Proxy INTERCEPTA e adiciona LOG automatico!");
    }
}