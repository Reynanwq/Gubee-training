package com.reynanwq.userprincipal_proxy_demo.security;
import com.reynanwq.userprincipal_proxy_demo.model.User;
import com.reynanwq.userprincipal_proxy_demo.service.proxy.AuthorizationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.security.Principal;

/**
 * SecurityProxyFactory - Abstract Factory Pattern
 * Responsável por criar diferentes tipos de proxies de segurança
 */
@Component
public class SecurityProxyFactory {

    private final AuthorizationCache authorizationCache;

    @Autowired
    public SecurityProxyFactory(AuthorizationCache authorizationCache) {
        this.authorizationCache = authorizationCache;
    }

    /**
     * Cria um Proxy Dinâmico para UserPrincipal com cache
     */
    public Principal createUserPrincipalProxy(User user) {
        CustomUserPrincipal realPrincipal = new CustomUserPrincipal(user);

        UserPrincipalProxy handler = new UserPrincipalProxy(realPrincipal, authorizationCache);

        return (Principal) Proxy.newProxyInstance(
                realPrincipal.getClass().getClassLoader(),
                realPrincipal.getClass().getInterfaces(),
                handler
        );
    }

    /**
     * Cria um Proxy de Validação (apenas para métodos de segurança)
     */
    public Principal createValidationProxy(User user) {
        CustomUserPrincipal realPrincipal = new CustomUserPrincipal(user);

        ValidationProxy handler = new ValidationProxy(realPrincipal);

        return (Principal) Proxy.newProxyInstance(
                realPrincipal.getClass().getClassLoader(),
                realPrincipal.getClass().getInterfaces(),
                handler
        );
    }

    /**
     * Cria um CustomUserPrincipal sem proxy
     */
    public CustomUserPrincipal createUserPrincipal(User user) {
        return new CustomUserPrincipal(user);
    }

    /**
     * Factory method com diferentes tipos de proxy
     */
    public Principal createPrincipal(User user, ProxyType proxyType) {
        System.out.println("[FACTORY] Criando Principal do tipo: " + proxyType);

        switch (proxyType) {
            case CACHE:
                return createUserPrincipalProxy(user);
            case VALIDATION:
                return createValidationProxy(user);
            case NO_PROXY:
                return createUserPrincipal(user);
            default:
                throw new IllegalArgumentException("Tipo de proxy não suportado: " + proxyType);
        }
    }

    /**
     * Enum para tipos de proxy disponíveis
     */
    public enum ProxyType {
        CACHE,
        VALIDATION,
        NO_PROXY
    }
}