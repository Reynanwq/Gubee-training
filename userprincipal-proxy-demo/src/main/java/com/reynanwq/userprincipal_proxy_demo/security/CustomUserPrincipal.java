package com.reynanwq.userprincipal_proxy_demo.security;

import com.reynanwq.userprincipal_proxy_demo.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * CustomUserPrincipal - Representa o usuário autenticado
 * Implementa Principal (Java EE) e UserDetails (Spring Security)
 */
@Getter
public class CustomUserPrincipal implements Principal, UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(User user) {
        this.user = user;
        this.authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // Métodos do Principal (Java EE)
    @Override
    public String getName() {
        return user.getUsername();
    }

    // Métodos do UserDetails (Spring Security)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    // Métodos de conveniência
    public String getEmail() {
        return user.getEmail();
    }

    public Long getUserId() {
        return user.getId();
    }
}
