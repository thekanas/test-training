package ru.clevertec.product.validator;

public interface Validator<T> {

    ValidationResult validate(T object);
}
