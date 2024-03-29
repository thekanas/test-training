package ru.clevertec.product.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.exception.ProductNotFoundException;
import ru.clevertec.product.exception.ValidationException;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.util.ProductTestData;
import ru.clevertec.product.util.TestConstant;
import ru.clevertec.product.validator.ProductDtoValidator;
import ru.clevertec.product.validator.ValidationResult;
import ru.clevertec.product.validator.Error;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper mapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDtoValidator productDtoValidator;

    @InjectMocks
    private ProductServiceImpl productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;


    @Test
    void getTest() {
        // given
        UUID uuid = ProductTestData.builder().build().getUuid();
        InfoProductDto expected = ProductTestData.builder()
                .build().buildInfoProductDto();
        Product product = ProductTestData.builder()
                .build().buildProduct();

        when(mapper.toInfoProductDto(product))
                .thenReturn(expected);
        when(productRepository.findById(uuid))
                .thenReturn(Optional.of(product));

        // when
        InfoProductDto actual = productService.get(uuid);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getShouldThrowProductNotFoundException_whenProductIsNotFound() {
        // given
        UUID uuid = TestConstant.UUID_NOT_FOUND;

        // when, then
        var exception = assertThrows(ProductNotFoundException.class, () -> productService.get(uuid));
        assertThat(exception.getMessage())
                .isEqualTo("Product with uuid: " + uuid + " not found");
    }

    @Test
    void getAllTest() {
        // given
        UUID uuid2 = UUID.fromString("c114c54f-9d37-4763-ab7e-db03caf9dad6");
        InfoProductDto infoProductDto1 = ProductTestData.builder()
                .build().buildInfoProductDto();
        InfoProductDto infoProductDto2 = ProductTestData.builder()
                .withUuid(uuid2)
                .build().buildInfoProductDto();

        Product product1 = ProductTestData.builder()
                .build().buildProduct();
        Product product2 = ProductTestData.builder()
                .withUuid(uuid2)
                .build().buildProduct();

        List<InfoProductDto> expected = List.of(infoProductDto1, infoProductDto2);
        List<Product> products = List.of(product1, product2);

        when(mapper.toInfoProductDto(product1))
                .thenReturn(infoProductDto1);
        when(mapper.toInfoProductDto(product2))
                .thenReturn(infoProductDto2);
        when(productRepository.findAll())
                .thenReturn(products);

        // when
        List<InfoProductDto> actual = productService.getAll();

        // then
        assertIterableEquals(expected, actual);
    }

    @Test
    void createShouldInvokeRepository_withoutProductUuid() {
        // given
        Product productToSave = ProductTestData.builder()
                .withUuid(null)
                .build().buildProduct();
        Product expected = ProductTestData.builder()
                .build().buildProduct();
        ProductDto productDto = ProductTestData.builder()
                .build().buildProductDto();

        when(productRepository.save(productToSave))
                .thenReturn(expected);
        when(mapper.toProduct(productDto))
                .thenReturn(productToSave);
        when(productDtoValidator.validate(productDto))
                .thenReturn(new ValidationResult());

        // when
        productService.create(productDto);

        // then
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue())
                .hasFieldOrPropertyWithValue(Product.Fields.uuid, null);
    }

    @Test
    void createShouldThrowException_whenDtoInvalid() {
        // given
        ProductDto productDto = ProductTestData.builder()
                .build().buildProductDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.name", "message"));

        when(productDtoValidator.validate(productDto))
                .thenReturn(validationResult);

        // when,then
        assertThrows(ValidationException.class, () -> productService.create(productDto));
    }

    @Test
    void updateTest() {
        // given
        UUID uuid = ProductTestData.builder().build().getUuid();
        Product oldProduct = ProductTestData.builder()
                .build().buildProduct();
        Product newProduct = ProductTestData.builder()
                .withName("Булочка")
                .withDescription("с маком")
                .withPrice(BigDecimal.valueOf(3.50))
                .build().buildProduct();
        ProductDto productDto = ProductTestData.builder()
                .withName("Булочка")
                .withDescription("с маком")
                .withPrice(BigDecimal.valueOf(3.50))
                .build().buildProductDto();

        when(productRepository.findById(uuid))
                .thenReturn(Optional.ofNullable(oldProduct));
        when(productRepository.save(newProduct))
                .thenReturn(newProduct);
        when(mapper.merge(oldProduct, productDto))
                .thenReturn(newProduct);
        when(productDtoValidator.validate(productDto))
                .thenReturn(new ValidationResult());

        // when
        productService.update(uuid, productDto);

        // then
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue())
                .isEqualTo(newProduct);
    }

    @Test
    void updateShouldThrowProductNotFoundException_whenProductIsNotFound() {
        // given
        UUID uuid = UUID.fromString("25486810-43dd-41e8-ab60-98aa2d200acb");
        ProductDto productDto = ProductTestData.builder()
                .withName("Булочка")
                .withDescription("с маком")
                .withPrice(BigDecimal.valueOf(3.50))
                .build().buildProductDto();

        when(productDtoValidator.validate(productDto))
                .thenReturn(new ValidationResult());

        // when, then
        var exception = assertThrows(ProductNotFoundException.class, () -> productService.update(uuid, productDto));
        assertThat(exception.getMessage())
                .isEqualTo("Product with uuid: 25486810-43dd-41e8-ab60-98aa2d200acb not found");
    }

    @Test
    void updateShouldThrowException_whenDtoInvalid() {
        // given
        UUID uuid = UUID.fromString("25486810-43dd-41e8-ab60-98aa2d200acb");
        ProductDto productDto = ProductTestData.builder()
                .build().buildProductDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.name", "message"));

        when(productDtoValidator.validate(productDto))
                .thenReturn(validationResult);

        // when,then
        assertThrows(ValidationException.class, () -> productService.update(uuid, productDto));
    }

    @Test
    void deleteTest() {
        // given
        UUID uuid = ProductTestData.builder().build().getUuid();

        // when
        productService.delete(uuid);

        // then
        verify(productRepository).delete(uuid);
    }

}
