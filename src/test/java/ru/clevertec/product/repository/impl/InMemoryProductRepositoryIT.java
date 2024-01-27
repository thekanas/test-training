package ru.clevertec.product.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.util.ProductTestData;
import ru.clevertec.product.util.TestConstant;

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
        Product product1 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());
        Product product2 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());
        Product expected = productRepository.save(ProductTestData.builder().build().buildBananaProduct());

        // when
        Product actual = productRepository.findById(expected.getUuid())
                .orElseThrow();

        // then
        assertEquals(expected, actual);
    }

    @Test
    void findByIdShouldReturnOptionalEmpty_whenUuidIsNotFound() {
        // given
        UUID uuidNotFound = TestConstant.UUID_NOT_FOUND;
        Optional<Product> expected = Optional.empty();

        // when
        Optional<Product> actual = productRepository.findById(uuidNotFound);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        // given
        Product product1 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());
        Product product2 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());
        Product product3 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());

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
    void saveShouldSetUuidAndDate_whenProductWithoutUuidAndIsSaved() {
        // given
        Product productToSave = ProductTestData.builder()
                .withUuid(null)
                .withCreated(null)
                .build().buildProduct();

        // when
        Product actual = productRepository.save(productToSave);

        // then
        assertNotEquals(null, actual.getUuid());
        assertNotEquals(null, actual.getCreated());
    }

    @Test
    void saveShouldTrowsIllegalArgumentException_whenProductIsNull() {

        assertThrows(IllegalArgumentException.class, () -> productRepository.save(null));
    }

    @Test
    void delete() {
        // given
        Product product1 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());
        Product product2 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());
        Product product3 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());

        // when
        productRepository.delete(product3.getUuid());

        // then
        assertThat(productRepository.findAll()).hasSize(2);
        assertEquals(Optional.empty(), productRepository.findById(product3.getUuid()));
    }

    @Test
    void deleteShouldNotThrowException_whenUuidIsNotFound() {
        // given
        UUID noFoundUuid = TestConstant.UUID_NOT_FOUND;
        Product product1 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());
        Product product2 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());
        Product product3 = productRepository.save(ProductTestData.builder().build().buildBananaProduct());

        // when, then
        assertDoesNotThrow(() -> productRepository.delete(noFoundUuid));
    }

}
