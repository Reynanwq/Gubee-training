import java.lang.reflect.Proxy;

public class DatabaseServiceFactory {

    public static DatabaseService createService(ProxyType proxyType) {
        DatabaseService realService = new DatabaseServiceImpl();

        switch (proxyType) {
            case WITH_TRANSACTION:
                return createTransactionProxy(realService);

            case NO_PROXY:
                return realService;  // Retorna serviço real diretamente

            case SIMPLE_PROXY:
                return createSimpleProxy(realService);

            default:
                throw new IllegalArgumentException("Tipo de proxy não suportado: " + proxyType);
        }
    }

    private static DatabaseService createTransactionProxy(DatabaseService realService) {
        return (DatabaseService) Proxy.newProxyInstance(
                DatabaseService.class.getClassLoader(),
                new Class[]{DatabaseService.class},
                new TransactionInvocationHandler(realService)
        );
    }

    private static DatabaseService createSimpleProxy(DatabaseService realService) {
        return (DatabaseService) Proxy.newProxyInstance(
                DatabaseService.class.getClassLoader(),
                new Class[]{DatabaseService.class},
                new SimpleInvocationHandler(realService)
        );
    }
}