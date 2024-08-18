package ru.inno.course.player.ext;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class PointsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(20, 20),
                Arguments.of(222, 222),
                Arguments.of(-50, -50),
                Arguments.of(12345, 654321),
                Arguments.of(0, 0),
                Arguments.of(-9999, -9999)
        );

    }
}
