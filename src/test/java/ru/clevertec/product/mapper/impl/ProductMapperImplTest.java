package ru.clevertec.product.mapper.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.util.ProductTestData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ProductMapperImplTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @ParameterizedTest
    @MethodSource("getProductMapperArguments")
    void toProductTest(ProductDto productDto, Product expected) {
         // when
        Product actual = mapper.toProduct(productDto);

        // when, then
        assertEquals(expected, actual);
        assertThat(actual)
                .hasFieldOrPropertyWithValue(Product.Fields.uuid, null)
                .hasFieldOrPropertyWithValue(Product.Fields.created, null);
    }

    @ParameterizedTest
    @MethodSource("getInfoProductMapperArguments")
    void toInfoProductDtoTest(InfoProductDto expected, Product product) {
        // when
        InfoProductDto actual = mapper.toInfoProductDto(product);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void mergeTest() {
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
        assertEquals(expected, actual);
    }

    static Stream<Arguments> getProductMapperArguments() {
        return Stream.of(
                Arguments.of(
                        new ProductDto("Банан", "Желтый", BigDecimal.valueOf(4.02)),
                        new Product(null, "Банан", "Желтый", BigDecimal.valueOf(4.02), null)),
                Arguments.of(
                        new ProductDto("Дыня", "Желтая", BigDecimal.valueOf(4.52)),
                        new Product(null, "Дыня", "Желтая", BigDecimal.valueOf(4.52), null)),
                Arguments.of(
                        new ProductDto("Апельсин", "Оранжевый", BigDecimal.valueOf(0.01)),
                        new Product(null, "Апельсин", "Оранжевый", BigDecimal.valueOf(0.01), null))
        );
    }

    static Stream<Arguments> getInfoProductMapperArguments() {
        return Stream.of(
                Arguments.of(
                        new InfoProductDto(UUID.fromString("f8658043-17e8-4e48-8d2d-5f664b768398"), "Банан", "Желтый", BigDecimal.valueOf(4.02)),
                        new Product(UUID.fromString("f8658043-17e8-4e48-8d2d-5f664b768398"), "Банан", "Желтый", BigDecimal.valueOf(4.02), LocalDateTime.of(2023, 10, 29, 19, 1))),
                Arguments.of(
                        new InfoProductDto(UUID.fromString("31ec6ab3-58be-4356-b669-9ff066d402c5"),"Дыня", "Желтая", BigDecimal.valueOf(4.52)),
                        new Product(UUID.fromString("31ec6ab3-58be-4356-b669-9ff066d402c5"), "Дыня", "Желтая", BigDecimal.valueOf(4.52), LocalDateTime.of(2023, 10, 29, 20, 2))),
                Arguments.of(
                        new InfoProductDto(UUID.fromString("deda7d86-dd74-4b17-8e7b-75060c3d5cbb"),"Апельсин", "Оранжевый", BigDecimal.valueOf(0.01)),
                        new Product(UUID.fromString("deda7d86-dd74-4b17-8e7b-75060c3d5cbb"), "Апельсин", "Оранжевый", BigDecimal.valueOf(0.01), LocalDateTime.of(2023, 10, 29, 21, 3)))
        );
    }

}
