package com.reynanwq.userprincipal_proxy_demo.security;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.Principal;
import java.time.LocalDateTime;

/**
 * UserPrincipalProxy - Proxy Dinâmico
 * Intercepta chamadas ao UserPrincipal para adicionar:
 * - Logging
 * - Auditoria
 * - Validação de segurança
 */
public class UserPrincipalProxy implements InvocationHandler {

    private final Principal realPrincipal;

    public UserPrincipalProxy(Principal realPrincipal) {
        this.realPrincipal = realPrincipal;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // Logging ANTES da execução
        String methodName = method.getName();
        System.out.println("===========================================");
        System.out.println("[PROXY] Interceptando método: " + methodName);
        System.out.println("[PROXY] Timestamp: " + LocalDateTime.now());
        System.out.println("[PROXY] Usuário: " + realPrincipal.getName());

        // Auditoria específica para métodos sensíveis
        if (methodName.equals("getPassword")) {
            System.out.println("[AUDITORIA] ⚠️ ACESSO A SENHA DETECTADO!");
        }

        // Executa o método real
        long startTime = System.currentTimeMillis();
        Object result = method.invoke(realPrincipal, args);
        long endTime = System.currentTimeMillis();

        // Logging DEPOIS da execução
        System.out.println("[PROXY] Método executado em: " + (endTime - startTime) + "ms");
        System.out.println("[PROXY] Resultado: " + (result != null ? result.getClass().getSimpleName() : "null"));
        System.out.println("===========================================\n");

        return result;
    }
}

