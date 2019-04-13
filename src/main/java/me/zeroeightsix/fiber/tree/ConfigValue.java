package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ConfigValue<T> extends ConfigLeaf implements Property<T> {

    @Nullable
    private T value;
    @Nullable
    private T defaultValue;
    @Nonnull
    private final BiConsumer<T, T> consumer;
    @Nonnull
    private final List<Constraint> constraintList;

    private final Predicate<T> restriction;

    @Nonnull
    private final Class<T> type;

    public ConfigValue(@Nullable String name, @Nullable String comment, @Nullable T value, @Nullable T defaultValue, @Nonnull BiConsumer<T, T> consumer, @Nonnull List<Constraint> constraintList, @Nonnull Class<T> type, final boolean isFinal) {
        super(name, comment);
        this.value = value;
        this.defaultValue = defaultValue;
        this.consumer = consumer;
        this.constraintList = constraintList;
        this.type = type;
        if (isFinal) {
            restriction = t -> false;
        } else {
            restriction = t -> constraintList.stream().allMatch(constraint -> constraint.test(t));
        }
    }

    @Override
    @Nullable
    public T getValue() {
        return value;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public boolean setValue(@Nullable T value) {
        if (!restriction.test(value)) return false;
        T oldValue = this.value;
        this.value = value;
        this.consumer.accept(oldValue, value);
        return true;
    }

    @Nonnull
    public BiConsumer<T, T> getListener() {
        return consumer;
    }

    @Nullable
    public T getDefaultValue() {
        return defaultValue;
    }

    public static <T> ConfigValueBuilder<T> builder(Class<T> type) {
        return new ConfigValueBuilder<>(type);
    }

}
