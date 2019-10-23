package com.scottlogic.deg.custom.builder;

import com.scottlogic.deg.custom.CustomGenerator;
import com.scottlogic.deg.custom.CustomGeneratorFieldType;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BuiltCustomGenerator<T> implements CustomGenerator<T> {

    private final CustomGeneratorFieldType fieldType;
    private final String name;
    private final Function<T, Boolean> matchingFunction;
    private final Supplier<Stream<T>> randomGenerator;
    private final Supplier<Stream<T>>  negatedRandomGenerator;
    private final Supplier<Stream<T>> sequentialGenerator;
    private final Supplier<Stream<T>> negatedSequentialGenerator;

    public BuiltCustomGenerator(CustomGeneratorFieldType fieldType,
                                String name,
                                Function<T, Boolean> matchingFunction,
                                Supplier<Stream<T>> randomGenerator,
                                Supplier<Stream<T>>  negatedRandomGenerator,
                                Supplier<Stream<T>> sequentialGenerator,
                                Supplier<Stream<T>> negatedSequentialGenerator) {
        this.fieldType = fieldType;
        this.name = name;
        this.matchingFunction = matchingFunction;
        this.randomGenerator = randomGenerator;
        this.negatedRandomGenerator = negatedRandomGenerator;
        this.sequentialGenerator = sequentialGenerator;
        this.negatedSequentialGenerator = negatedSequentialGenerator;
    }

    @Override
    public String generatorName() {
        return name;
    }

    @Override
    public CustomGeneratorFieldType fieldType() {
        return fieldType;
    }

    @Override
    public Stream<T> generateRandom() {
        return randomGenerator.get();
    }

    @Override
    public Stream<T> generateNegatedRandom() {
        return negatedRandomGenerator.get();
    }

    @Override
    public Stream<T> generateSequential() {
        return sequentialGenerator.get();
    }

    @Override
    public Stream<T> generateNegatedSequential() {
        return negatedSequentialGenerator.get();
    }

    @Override
    public boolean setMatchingFunction(T value) {
        return matchingFunction.apply(value);
    }
}