package ru.clevertec.product.repository.impl;

import ru.clevertec.product.entity.Product;
import ru.clevertec.product.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> data = new HashMap<>();

    @Override
    public Optional<Product> findById(UUID uuid) {
        return data.entrySet().stream()
                .filter(e -> e.getKey().equals(uuid))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return data.values()
                .stream()
                .toList();
    }

    @Override
    public Product save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException();
        }
        if (product.getUuid() == null) {
            product.setUuid(UUID.randomUUID());
            product.setCreated(LocalDateTime.now());
        }
        data.put(product.getUuid(), product);
        return product;
    }

    @Override
    public void delete(UUID uuid) {
        data.remove(uuid);
    }
}
