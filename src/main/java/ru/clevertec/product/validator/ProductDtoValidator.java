package ru.clevertec.product.validator;

import lombok.NoArgsConstructor;
import ru.clevertec.product.data.ProductDto;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ProductDtoValidator implements Validator<ProductDto> {

    private static final ProductDtoValidator INSTANCE = new ProductDtoValidator();

    public static ProductDtoValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public ValidationResult validate(ProductDto productDto) {

        var validationResult = new ValidationResult();

        if (productDto.name() == null || productDto.name().isEmpty()) {
            validationResult.add(Error.of("invalid.name", "Name cannot be null or empty"));
        }
        else if (!productDto.name().matches("[а-яёА-ЯЁ\\s]{5,11}")) {
            validationResult.add(Error.of("invalid.name", "Name must contain 5-10 characters (Russian or spaces)"));
        }

        if (productDto.price() == null || productDto.price().compareTo(BigDecimal.ZERO) <= 0) {
            validationResult.add(Error.of("invalid.price", "Price cannot be null and must be positive"));
        }

        if (productDto.description() != null && !productDto.description().matches("[а-яёА-ЯЁ\\s]{10,31}")) {
            validationResult.add(Error.of("invalid.description", "Description should be 10-30 characters (Russian and spaces)"));
        }

        return validationResult;
    }
}
