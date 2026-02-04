package com.reynanwq.userprincipal_proxy_demo.security;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.Principal;

/**
 * ValidationProxy - Proxy especializado apenas em validações
 */
public class ValidationProxy implements InvocationHandler {

    private final Principal realPrincipal;

    public ValidationProxy(Principal realPrincipal) {
        this.realPrincipal = realPrincipal;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();

        if (methodName.startsWith("is") || methodName.startsWith("get")) {
            System.out.println("[VALIDATION-PROXY] Validando acesso ao método: " + methodName);

            if (!isWithinBusinessHours()) {
                System.out.println("[VALIDATION-PROXY] Acesso fora do horário comercial");
            }
        }
        return method.invoke(realPrincipal, args);
    }

    private boolean isWithinBusinessHours() {
        // Simulação: horário comercial 9h-18h
        java.time.LocalTime now = java.time.LocalTime.now();
        return now.isAfter(java.time.LocalTime.of(9, 0)) &&
                now.isBefore(java.time.LocalTime.of(18, 0));
    }
}