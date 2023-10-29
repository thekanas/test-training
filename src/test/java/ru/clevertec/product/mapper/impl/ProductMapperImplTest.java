package ru.clevertec.product.mapper.impl;

import org.junit.jupiter.api.Test;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.util.ProductTestData;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ProductMapperImplTest {

    private final ProductMapper mapper = new ProductMapperImpl();

    @Test
    void toProduct() {
        // given
        Product expected = ProductTestData.builder()
                .withUuid(null)
                .build().buildProduct();
        ProductDto productDto = ProductTestData.builder()
                .build().buildProductDto();

        // when
        Product actual = mapper.toProduct(productDto);

        // then
        assertEquals(actual, expected);
        assertThat(actual)
                .hasFieldOrPropertyWithValue(Product.Fields.uuid, null);
    }

    @Test
    void toInfoProductDto() {
        // given
        Product product = ProductTestData.builder()
                .build().buildProduct();
        InfoProductDto expected = ProductTestData.builder()
                .build().buildInfoProductDto();

        // when
        InfoProductDto actual = mapper.toInfoProductDto(product);

        // then
        assertEquals(actual, expected);
    }

    @Test
    void merge() {
        // given
        Product oldProduct = ProductTestData.builder()
                .build().buildProduct();
        Product expected = ProductTestData.builder()
                .withName("Булочка")
                .withDescription("с маком")
                .withPrice(BigDecimal.valueOf(3.50))
                .build().buildProduct();
        ProductDto productDto = ProductTestData.builder()
                .withName("Булочка")
                .withDescription("с маком")
                .withPrice(BigDecimal.valueOf(3.50))
                .build().buildProductDto();

        // when
        Product actual = mapper.merge(oldProduct, productDto);

        // then
        assertEquals(actual, expected);
    }

}