import java.lang.reflect.Proxy;

public class Main {

    public static void main(String[] args) {
        DatabaseService realService = new DatabaseServiceImpl();

        DatabaseService proxyService = (DatabaseService) Proxy.newProxyInstance(
                DatabaseService.class.getClassLoader(),
                new Class[]{DatabaseService.class},
                new TransactionInvocationHandler(realService)
        );

        System.out.println("=== TESTE DE TRANSAÇÕES ===\n");

        proxyService.salvarUsuario("Reynan Paiva    ");
        System.out.println();

        proxyService.atualizarUsuario("Maria Santos");
        System.out.println();

        proxyService.consultarUsuario("Pedro Oliveira");
        System.out.println();

        try {
            proxyService.deletarUsuario("admin");
        } catch (Exception e) {
            System.out.println("  -> Exceção capturada no main: " + e.getMessage());
        }
        System.out.println();

        proxyService.deletarUsuario("João Silva");
    }
}