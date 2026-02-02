package com.reynanwq.userprincipal_proxy_demo.service;

import com.reynanwq.userprincipal_proxy_demo.model.User;
import com.reynanwq.userprincipal_proxy_demo.repository.UserRepository;
import com.reynanwq.userprincipal_proxy_demo.security.SecurityProxyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityProxyFactory securityProxyFactory;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(User user) {
        // Criptografa senha antes de salvar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Principal getUserPrincipalWithProxy(String username) {
        User user = getUserByUsername(username);
        return securityProxyFactory.createPrincipal(user, true);
    }

    @Override
    public Principal getUserPrincipalWithoutProxy(String username) {
        User user = getUserByUsername(username);
        return securityProxyFactory.createPrincipal(user, false);
    }
}

