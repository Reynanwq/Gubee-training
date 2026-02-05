public class Main {

    public static void main(String[] args) {
        System.out.println("=== TESTE COM FACTORY E MÚLTIPLOS PROXIES ===\n");

        // Teste 1: Proxy com transações
        System.out.println("--- TESTE 1: PROXY COM TRANSAÇÕES ---");
        DatabaseService transactionalService = DatabaseServiceFactory.createService(ProxyType.WITH_TRANSACTION);
        testarServico(transactionalService);

        System.out.println("\n--- TESTE 2: SERVIÇO DIRETO (SEM PROXY) ---");
        DatabaseService realService = DatabaseServiceFactory.createService(ProxyType.NO_PROXY);
        testarServico(realService);

        System.out.println("\n--- TESTE 3: PROXY SIMPLES (SEM TRANSAÇÕES) ---");
        DatabaseService simpleProxyService = DatabaseServiceFactory.createService(ProxyType.SIMPLE_PROXY);
        testarServico(simpleProxyService);
    }

    private static void testarServico(DatabaseService service) {
        System.out.println();

        service.salvarUsuario("Reynan Paiva");
        System.out.println();

        service.atualizarUsuario("Maria Santos");
        System.out.println();

        service.consultarUsuario("Pedro Oliveira");
        System.out.println();

        try {
            service.deletarUsuario("admin");
        } catch (Exception e) {
            System.out.println("  -> Exceção capturada: " + e.getMessage());
        }
        System.out.println();

        service.deletarUsuario("João Silva");
    }
}