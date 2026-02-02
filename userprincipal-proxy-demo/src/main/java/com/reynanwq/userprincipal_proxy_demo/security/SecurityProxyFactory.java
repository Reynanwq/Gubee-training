package com.reynanwq.userprincipal_proxy_demo.security;

import com.reynanwq.userprincipal_proxy_demo.model.User;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.security.Principal;

/**
 * SecurityProxyFactory - Abstract Factory Pattern
 * Responsável por criar diferentes tipos de proxies de segurança
 */
@Component
public class SecurityProxyFactory {

    /**
     * Cria um Proxy Dinâmico para UserPrincipal
     *
     * @param user Usuário autenticado
     * @return Proxy do Principal com interceptação
     */
    public Principal createUserPrincipalProxy(User user) {
        // Cria o objeto real
        CustomUserPrincipal realPrincipal = new CustomUserPrincipal(user);

        // Cria o InvocationHandler
        UserPrincipalProxy handler = new UserPrincipalProxy(realPrincipal);

        // Cria o Proxy Dinâmico
        return (Principal) Proxy.newProxyInstance(
                realPrincipal.getClass().getClassLoader(),
                realPrincipal.getClass().getInterfaces(),
                handler
        );
    }

    /**
     * Cria um CustomUserPrincipal sem proxy (direto)
     */
    public CustomUserPrincipal createUserPrincipal(User user) {
        return new CustomUserPrincipal(user);
    }

    /**
     * Método factory para decidir qual tipo de principal criar
     */
    public Principal createPrincipal(User user, boolean useProxy) {
        if (useProxy) {
            System.out.println("[FACTORY] Criando Principal COM Proxy Dinâmico");
            return createUserPrincipalProxy(user);
        } else {
            System.out.println("[FACTORY] Criando Principal SEM Proxy");
            return createUserPrincipal(user);
        }
    }
}
