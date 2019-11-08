package com.scottlogic.deg.common.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidationResult
{
    public final boolean isSuccess;
    public final List<String> errors;

    private ValidationResult(boolean isSuccess, List<String> errors)
    {
        this.isSuccess = isSuccess;
        this.errors = errors;
    }

    public static ValidationResult success(){
        return new ValidationResult(true, new ArrayList<>());
    }

    public static ValidationResult failure(String... errors)
    {
        return new ValidationResult(false, Arrays.stream(errors).collect(Collectors.toList()));
    }

    public static ValidationResult failure(List<String> errors)
    {
        return new ValidationResult(false, errors);
    }

    public static ValidationResult combine(List<ValidationResult> validationResults)
    {
        boolean isValid = validationResults.stream()
            .allMatch(validationResult -> validationResult.isSuccess);
        List<String> errors = validationResults.stream()
            .flatMap(validationResult -> validationResult.errors.stream())
            .collect(Collectors.toList());
        return new ValidationResult(isValid, errors);
    }

    public static ValidationResult combine(ValidationResult... validationResults)
    {
        boolean isValid = Arrays.stream(validationResults)
            .allMatch(validationResult -> validationResult.isSuccess);
        List<String> errors = Arrays.stream(validationResults)
            .flatMap(validationResult -> validationResult.errors.stream())
            .collect(Collectors.toList());
        return new ValidationResult(isValid, errors);
    }

    public static ValidationResult combine(Stream<ValidationResult> validationResults)
    {
        List<Boolean> isValid = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        validationResults.forEach(validationResult ->
        {
            errors.addAll(validationResult.errors);
            isValid.add(validationResult.isSuccess);
        });
        return new ValidationResult(isValid.stream().allMatch(o -> o), errors);
    }
}

