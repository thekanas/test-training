package ru.clevertec.product.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.util.ProductTestData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class InMemoryProductRepositoryIT {

    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
    }

    @Test
    void findById() {
        // given
        Product product1 = productRepository.save(new Product(null, "Банан", "Желтый", BigDecimal.valueOf(4.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        Product product2 = productRepository.save(new Product(null, "Дыня", "Желтая", BigDecimal.valueOf(8.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        Product expected = productRepository.save(new Product(null, "Апельсин", "Оранжевый", BigDecimal.valueOf(4.52), LocalDateTime.of(2023, 10, 29, 19, 1)));

        // when
        Product actual = productRepository.findById(expected.getUuid())
                .orElseThrow();

        // then
        assertEquals(expected, actual);

    }

    @Test
    void findByIdShouldReturnOptionalEmpty_whenUuidIsNotFound() {
        // given
        UUID uuid = UUID.fromString("25486810-43dd-41e8-ab60-98aa2d200ac9");
        Optional<Product> expected = Optional.empty();

        // when
        Optional<Product> actual = productRepository.findById(uuid);

        // then
        assertEquals(expected, actual);

    }

    @Test
    void findAll() {
        // given
        Product product1 = productRepository.save(new Product(null, "Банан", "Желтый", BigDecimal.valueOf(4.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        Product product2 = productRepository.save(new Product(null, "Дыня", "Желтая", BigDecimal.valueOf(8.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        Product product3 = productRepository.save(new Product(null, "Апельсин", "Оранжевый", BigDecimal.valueOf(4.52), LocalDateTime.of(2023, 10, 29, 19, 1)));
        // when
        List<Product> actual = productRepository.findAll();
        // then
        assertThat(actual).hasSize(3);
        List<UUID> productUuids = actual.stream()
                .map(Product::getUuid)
                .toList();
        assertThat(productUuids).containsAll(Arrays.asList(product1.getUuid(), product2.getUuid(), product3.getUuid()));
    }

    @Test
    void save() {
        // given
        Product productToSave = ProductTestData.builder()
                .withUuid(null)
                .build().buildProduct();
        // when
        Product actual = productRepository.save(productToSave);
        // then
        assertNotEquals(null, actual.getUuid());
    }

    @Test
    void saveShouldTrowsIllegalArgumentException_whenProductIsNull() {

        assertThrows(IllegalArgumentException.class, () -> productRepository.save(null));
    }

    @Test
    void delete() {
        // given
        Product product1 = productRepository.save(new Product(null, "Банан", "Желтый", BigDecimal.valueOf(4.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        Product product2 = productRepository.save(new Product(null, "Банан", "Желтый", BigDecimal.valueOf(4.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        Product product3 = productRepository.save(new Product(null, "Банан", "Желтый", BigDecimal.valueOf(4.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        // when
        productRepository.delete(product3.getUuid());
        // then
        assertThat(productRepository.findAll()).hasSize(2);
        assertEquals(Optional.empty(), productRepository.findById(product3.getUuid()));
    }

    @Test
    void deleteShouldNotThrowException_whenUuidIsNotFound() {
        // given
        UUID uuid = UUID.fromString("6802b1e2-a572-45eb-b410-6666def1c46a");
        Product product1 = productRepository.save(new Product(null, "Банан", "Желтый", BigDecimal.valueOf(4.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        Product product2 = productRepository.save(new Product(null, "Банан", "Желтый", BigDecimal.valueOf(4.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        Product product3 = productRepository.save(new Product(null, "Банан", "Желтый", BigDecimal.valueOf(4.02), LocalDateTime.of(2023, 10, 29, 19, 1)));
        // when
        // then
        assertDoesNotThrow(() -> productRepository.delete(uuid));
    }
}