package com.reynanwq.userprincipal_proxy_demo.security;
import com.reynanwq.userprincipal_proxy_demo.service.proxy.AuthorizationCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * UserPrincipalProxy - Proxy Dinâmico
 * Implementa padrão Proxy para:
 * - Cache de autorizações
 * - Logging de segurança
 * - Auditoria
 * - Validação de acesso
 */
public class UserPrincipalProxy implements InvocationHandler {

    private final Principal realPrincipal;
    private final AuthorizationCache authorizationCache;

    @Autowired
    public UserPrincipalProxy(Principal realPrincipal, AuthorizationCache authorizationCache) {
        this.realPrincipal = realPrincipal;
        this.authorizationCache = authorizationCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        String username = realPrincipal.getName();

        if ("getAuthorities".equals(methodName)) {
            String cacheKey = "authorities:" + username;

            if (authorizationCache.containsKey(cacheKey)) {
                System.out.println("[PROXY-CACHE] Recuperando authorities do cache para: " + username);
                return authorizationCache.get(cacheKey);
            }

            System.out.println("[PROXY-CACHE] Authorities não encontradas no cache, buscando do banco...");
        }

        logSecurityMethod(methodName, username);

        long startTime = System.currentTimeMillis();

        Object result = method.invoke(realPrincipal, args);

        long executionTime = System.currentTimeMillis() - startTime;

        if ("getAuthorities".equals(methodName) && result != null) {
            String cacheKey = "authorities:" + username;
            authorizationCache.put(cacheKey, result);
            System.out.println("[PROXY-CACHE] Authorities armazenadas no cache");
        }

        validateSecurityResult(methodName, result);

        logExecution(methodName, username, executionTime, result);

        return result;
    }

    private void logSecurityMethod(String methodName, String username) {
        System.out.println("===========================================");
        System.out.println("[PROXY] Método: " + methodName);
        System.out.println("[PROXY] Usuário: " + username);
        System.out.println("[PROXY] Hora: " + LocalDateTime.now());

        // Alertas para métodos críticos
        switch (methodName) {
            case "getPassword":
                System.out.println("[SEGURANÇA] ACESSO A SENHA - Auditoria necessária!");
                break;
            case "isAccountNonLocked":
                System.out.println("[SEGURANÇA] Verificação de bloqueio de conta");
                break;
            case "hasRole":
                System.out.println("[SEGURANÇA] Verificação de permissão específica");
                break;
        }
    }

    private void validateSecurityResult(String methodName, Object result) {
        if ("isEnabled".equals(methodName) && Boolean.FALSE.equals(result)) {
            System.out.println("[SEGURANÇA] Conta desabilitada acessada!");
        }

        if ("isCredentialsNonExpired".equals(methodName) && Boolean.FALSE.equals(result)) {
            System.out.println("[SEGURANÇA] Credenciais expiradas!");
        }
    }

    private void logExecution(String methodName, String username, long executionTime, Object result) {
        System.out.println("[PROXY] Tempo de execução: " + executionTime + "ms");

        if (result instanceof Collection) {
            System.out.println("[PROXY] Resultado: Collection com " +
                    ((Collection<?>) result).size() + " itens");
        } else if (result instanceof String) {
            System.out.println("[PROXY] Resultado: \"" + result + "\"");
        } else if (result instanceof Boolean) {
            System.out.println("[PROXY] Resultado: " + ((Boolean) result ? "Verdadeiro" : "Falso"));
        } else {
            System.out.println("[PROXY] Resultado: " +
                    (result != null ? result.getClass().getSimpleName() : "null"));
        }

        System.out.println("===========================================\n");
    }
}