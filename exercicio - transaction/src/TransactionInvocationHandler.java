import java.lang.reflect.*;

public class TransactionInvocationHandler implements InvocationHandler {
    private final Object target;

    public TransactionInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method realMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());

        if (realMethod.isAnnotationPresent(Transaction.class)) {
            String className = target.getClass().getSimpleName();
            String methodName = method.getName();

            System.out.println("Iniciando execução do método " + methodName + "." + className);

            try {
                Object result = method.invoke(target, args);
                System.out.println("Finalizando execução do método " + methodName + "." + className + " com sucesso");
                return result;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                System.out.println("Finalizando execução do método " + methodName + "." + className + " com erro");
                throw cause;
            }
        }

        return method.invoke(target, args);
    }
}