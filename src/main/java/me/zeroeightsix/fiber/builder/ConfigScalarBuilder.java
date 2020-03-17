package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.builder.constraint.ConstraintsBuilder;

import javax.annotation.Nonnull;

public class ConfigScalarBuilder<T> extends ConfigValueBuilder<T, ConfigScalarBuilder<T>> {
    public ConfigScalarBuilder(@Nonnull Class<T> type) {
        super(type);
    }

    @Override
    public ConstraintsBuilder<ConfigScalarBuilder<T>, T> constraints() {
        return new ConstraintsBuilder<>(this, constraintList, type);
    }
}
