package com.example.kafka.pattern4;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Pattern4PriceService {

    private final Map<String, Double> prices = new ConcurrentHashMap<>();

    public boolean isPriceAvailable(String itemId) {
        return prices.containsKey(itemId);
    }

    public double getPrice(String itemId) {
        return prices.getOrDefault(itemId, 0.0);
    }

    public void setPrice(String itemId, double price) {
        prices.put(itemId, price);
    }
}
