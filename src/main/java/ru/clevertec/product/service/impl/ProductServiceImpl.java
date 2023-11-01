package ru.clevertec.product.service.impl;

import lombok.RequiredArgsConstructor;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.exception.ProductNotFoundException;
import ru.clevertec.product.exception.ValidationException;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.service.ProductService;
import ru.clevertec.product.validator.ProductDtoValidator;
import ru.clevertec.product.validator.ValidationResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper mapper;
    private final ProductRepository productRepository;
    private final ProductDtoValidator productDtoValidator;

    @Override
    public InfoProductDto get(UUID uuid) {
        Optional<Product> product = productRepository.findById(uuid);
        if(product.isEmpty()) {
            throw new ProductNotFoundException(uuid);
        }
        return mapper.toInfoProductDto(product.get());
    }

    @Override
    public List<InfoProductDto> getAll() {
        return productRepository.findAll().stream()
                .map(mapper::toInfoProductDto)
                .collect(Collectors.toList());
    }

    @Override
    public UUID create(ProductDto productDto) {
        ValidationResult validationResult = productDtoValidator.validate(productDto);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getErrors());
        }
        return productRepository.save(mapper.toProduct(productDto)).getUuid();
    }

    @Override
    public void update(UUID uuid, ProductDto productDto) {
        ValidationResult validationResult = productDtoValidator.validate(productDto);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getErrors());
        }

        Optional<Product> oldProduct = productRepository.findById(uuid);
        if(oldProduct.isEmpty()) {
            throw new ProductNotFoundException(uuid);
        }

        Product updateProduct = mapper.merge(oldProduct.get(), productDto);

        productRepository.save(updateProduct);
    }

    @Override
    public void delete(UUID uuid) {
        productRepository.delete(uuid);
    }
}
