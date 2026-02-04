package com.reynanwq.userprincipal_proxy_demo.service.proxy;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorizationCache {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 5 * 60 * 1000;

    public void put(String key, Object value) {
        cache.put(key, value);
        expirationTimes.put(key, System.currentTimeMillis() + CACHE_TTL_MS);
    }

    public Object get(String key) {
        Long expiration = expirationTimes.get(key);
        if (expiration != null && System.currentTimeMillis() > expiration) {
            cache.remove(key);
            expirationTimes.remove(key);
            return null;
        }
        return cache.get(key);
    }

    public void remove(String key) {
        cache.remove(key);
        expirationTimes.remove(key);
    }

    public void clear() {
        cache.clear();
        expirationTimes.clear();
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key) &&
                expirationTimes.containsKey(key) &&
                System.currentTimeMillis() <= expirationTimes.get(key);
    }
}
