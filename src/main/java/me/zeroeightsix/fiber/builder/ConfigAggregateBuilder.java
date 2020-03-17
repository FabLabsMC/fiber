package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.builder.constraint.AggregateConstraintsBuilder;

import javax.annotation.Nonnull;

public final class ConfigAggregateBuilder<E, T> extends ConfigValueBuilder<T, ConfigAggregateBuilder<E, T>> {
    @Nonnull
    private final Class<E> componentType;

    public ConfigAggregateBuilder(@Nonnull Class<T> type, @Nonnull Class<E> componentType) {
        super(type);
        this.componentType = componentType;
    }

    @Override
    public AggregateConstraintsBuilder<ConfigAggregateBuilder<E, T>, T, E> constraints() {
        return new AggregateConstraintsBuilder<>(this, constraintList, type, componentType);
    }

}
