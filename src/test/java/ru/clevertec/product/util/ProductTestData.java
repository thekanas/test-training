package ru.clevertec.product.util;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

@Data
@Builder(setterPrefix = "with", toBuilder = true)
@Accessors(chain = true)
public class ProductTestData {

    @Builder.Default
    private UUID uuid = UUID.fromString("25486810-43dd-41e8-ab60-98aa2d200acb");

    @Builder.Default
    private String name = "Батон";

    @Builder.Default
    private String description = "Белый хлеб";

    @Builder.Default
    private BigDecimal price = BigDecimal.valueOf(2.20);

    @Builder.Default
    private LocalDateTime created = LocalDateTime.of(2023, Month.OCTOBER, 29, 12, 0);

    public Product buildProduct() {
        return new Product(uuid, name, description, price, created);
    }

    public ProductDto buildProductDto() {
        return new ProductDto(name, description, price);
    }

    public InfoProductDto buildInfoProductDto() {
        return new InfoProductDto(uuid, name, description, price);
    }
}