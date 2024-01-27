package ru.clevertec.product.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.util.ProductTestData;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProductDtoValidatorTest {

    private final ProductDtoValidator validator = ProductDtoValidator.getInstance();

    @Test
    void shouldPassValidation() {
        // given
        ProductDto productDto = ProductTestData.builder()
                .build().buildProductDto();

        // when
        ValidationResult actualResult = validator.validate(productDto);

        // then
        assertFalse(actualResult.hasErrors());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Бул", "Bulka", "12345", "Булkl"})
    void invalidName(String name) {
        // given
        ProductDto productDto = ProductTestData.builder()
                .withName(name)
                .build().buildProductDto();

        // when
        ValidationResult actualResult = validator.validate(productDto);

        // then
        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.name");
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"Бул каСма", "BulkaSMakom", "1234567891011", "Булка s makom"})
    void invalidDescription(String description) {
        // given
        ProductDto productDto = ProductTestData.builder()
                .withDescription(description)
                .build().buildProductDto();

        // when
        ValidationResult actualResult = validator.validate(productDto);

        // then
        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.description");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -111111111.1, -0.00000000000001})
    void invalidPrice(double price) {
        // given
        ProductDto productDto = ProductTestData.builder()
                .withPrice(BigDecimal.valueOf(price))
                .build().buildProductDto();

        // when
        ValidationResult actualResult = validator.validate(productDto);

        // then
        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.price");
    }

    @Test
    void invalidPriceIfNull() {
        // given
        ProductDto productDto = ProductTestData.builder()
                .withPrice(null)
                .build().buildProductDto();

        // when
        ValidationResult actualResult = validator.validate(productDto);

        // then
        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.price");
    }

    @Test
    void invalidNameDescriptionPrice() {
        // given
        ProductDto productDto = ProductTestData.builder()
                .withName("Бул")
                .withDescription("Батон")
                .withPrice(BigDecimal.valueOf(-1.0))
                .build().buildProductDto();

        // when
        ValidationResult actualResult = validator.validate(productDto);

        // then
        assertThat(actualResult.getErrors()).hasSize(3);
        List<String> errorCodes = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();
        assertThat(errorCodes).contains("invalid.name", "invalid.description", "invalid.price");
    }

}
