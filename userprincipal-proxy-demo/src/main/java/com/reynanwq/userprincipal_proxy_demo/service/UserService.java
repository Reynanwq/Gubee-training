package com.reynanwq.userprincipal_proxy_demo.service;

import com.reynanwq.userprincipal_proxy_demo.model.User;

import java.security.Principal;
import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    Principal getUserPrincipalWithProxy(String username);

    Principal getUserPrincipalWithoutProxy(String username);

    Principal getValidationProxy(String username);
}