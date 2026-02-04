package com.reynanwq.userprincipal_proxy_demo.controller;

import com.reynanwq.userprincipal_proxy_demo.model.User;
import com.reynanwq.userprincipal_proxy_demo.security.CustomUserPrincipal;
import com.reynanwq.userprincipal_proxy_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Endpoint público para criar usuário
     */
    @PostMapping("/public/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        User created = userService.createUser(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuário criado com sucesso");
        response.put("username", created.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Demonstra uso do UserPrincipal via @AuthenticationPrincipal
     */
    @GetMapping("/user/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Map<String, Object> info = new HashMap<>();
        info.put("username", principal.getName());
        info.put("email", principal.getEmail());
        info.put("userId", principal.getUserId());
        info.put("authorities", principal.getAuthorities());

        return ResponseEntity.ok(info);
    }

    /**
     * Demonstra uso do UserPrincipal via Authentication
     */
    @GetMapping("/user/details")
    public ResponseEntity<Map<String, Object>> getUserDetails(Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        Map<String, Object> details = new HashMap<>();
        details.put("username", principal.getUsername());
        details.put("email", principal.getEmail());
        details.put("enabled", principal.isEnabled());
        details.put("accountNonExpired", principal.isAccountNonExpired());

        return ResponseEntity.ok(details);
    }

    /**
     * Demonstra uso do UserPrincipal via Principal (Java EE)
     */
    @GetMapping("/user/principal")
    public ResponseEntity<Map<String, String>> getPrincipalInfo(Principal principal) {

        Map<String, String> info = new HashMap<>();
        info.put("principalName", principal.getName());
        info.put("principalClass", principal.getClass().getName());

        return ResponseEntity.ok(info);
    }

    /**
     * Demonstra criação de Principal COM Proxy Dinâmico
     */
    @GetMapping("/demo/with-proxy/{username}")
    public ResponseEntity<Map<String, Object>> demoWithProxy(@PathVariable String username) {

        System.out.println("\n\n========== TESTE COM PROXY DINÂMICO ==========\n");

        Principal principal = userService.getUserPrincipalWithProxy(username);

        String name = principal.getName();

        Map<String, Object> result = new HashMap<>();
        result.put("username", name);
        result.put("proxyUsed", true);
        result.put("message", "Verifique o console para ver os logs do proxy!");

        return ResponseEntity.ok(result);
    }

    /**
     * Demonstra criação de Principal SEM Proxy
     */
    @GetMapping("/demo/without-proxy/{username}")
    public ResponseEntity<Map<String, Object>> demoWithoutProxy(@PathVariable String username) {

        System.out.println("\n\n========== TESTE SEM PROXY ==========\n");

        Principal principal = userService.getUserPrincipalWithoutProxy(username);

        String name = principal.getName();

        Map<String, Object> result = new HashMap<>();
        result.put("username", name);
        result.put("proxyUsed", false);
        result.put("message", "Chamada direta - sem interceptação");

        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint admin apenas
     */
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    /**
     * Testa múltiplos acessos para demonstrar o cache
     */
    @GetMapping("/demo/cache-test/{username}")
    public ResponseEntity<Map<String, Object>> cacheTest(@PathVariable String username) {

        System.out.println("\n\n========== TESTE DE CACHE ==========\n");

        Principal principal = userService.getUserPrincipalWithProxy(username);
        Authentication auth = (Authentication) principal;

        // Primeira chamada - vai buscar do banco
        System.out.println("\n--- Primeira chamada getAuthorities() ---");
        auth.getAuthorities();

        // Segunda chamada - deve usar cache
        System.out.println("\n--- Segunda chamada getAuthorities() (deve usar cache) ---");
        auth.getAuthorities();

        // Terceira chamada - ainda no cache
        System.out.println("\n--- Terceira chamada getAuthorities() (ainda no cache) ---");
        auth.getAuthorities();

        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("test", "Cache de authorities");
        result.put("message", "Verifique os logs para ver o cache funcionando");

        return ResponseEntity.ok(result);
    }

    /**
     * Testa proxy de validação
     */
    @GetMapping("/demo/validation-proxy/{username}")
    public ResponseEntity<Map<String, Object>> validationProxyTest(@PathVariable String username) {

        System.out.println("\n\n========== TESTE DE VALIDAÇÃO ==========\n");

        Principal principal = userService.getValidationProxy(username);

        // Testa diferentes métodos
        System.out.println("\n--- Testando métodos de validação ---");
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) principal;

            System.out.println("isEnabled: " + userDetails.isEnabled());
            System.out.println("isAccountNonLocked: " + userDetails.isAccountNonLocked());
            System.out.println("isCredentialsNonExpired: " + userDetails.isCredentialsNonExpired());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("proxyType", "Validation Proxy");
        result.put("horaAtual", java.time.LocalTime.now().toString());

        return ResponseEntity.ok(result);
    }

    /**
     * Compara performance com e sem proxy
     */
    @GetMapping("/demo/performance/{username}")
    public ResponseEntity<Map<String, Object>> performanceTest(@PathVariable String username) {

        System.out.println("\n\n========== TESTE DE PERFORMANCE ==========\n");

        Map<String, Object> result = new HashMap<>();
        result.put("username", username);

        // Teste SEM proxy
        System.out.println("\n--- Performance SEM proxy ---");
        long startWithoutProxy = System.currentTimeMillis();
        Principal withoutProxy = userService.getUserPrincipalWithoutProxy(username);
        for (int i = 0; i < 100; i++) {
            withoutProxy.getName();
            if (withoutProxy instanceof org.springframework.security.core.userdetails.UserDetails) {
                ((org.springframework.security.core.userdetails.UserDetails) withoutProxy).getAuthorities();
            }
        }
        long endWithoutProxy = System.currentTimeMillis();
        result.put("tempoSemProxy", (endWithoutProxy - startWithoutProxy) + "ms");

        // Teste COM proxy
        System.out.println("\n--- Performance COM proxy (com cache) ---");
        long startWithProxy = System.currentTimeMillis();
        Principal withProxy = userService.getUserPrincipalWithProxy(username);
        for (int i = 0; i < 100; i++) {
            withProxy.getName();
            if (withProxy instanceof org.springframework.security.core.userdetails.UserDetails) {
                ((org.springframework.security.core.userdetails.UserDetails) withProxy).getAuthorities();
            }
        }
        long endWithProxy = System.currentTimeMillis();
        result.put("tempoComProxy", (endWithProxy - startWithProxy) + "ms");

        return ResponseEntity.ok(result);
    }
}

