package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.builder.constraint.ConstraintsBuilder;
import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.exceptions.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.ConfigValue;
import me.zeroeightsix.fiber.tree.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ConfigValueBuilder<T> {

    @Nonnull
    private final Class<T> type;
    @Nullable
    private String name;
    @Nullable
    private String comment = null;
    @Nullable
    private T defaultValue = null;
    private boolean isFinal = false;
    private BiConsumer<T, T> consumer = (t, t2) -> {};
    private List<Constraint> constraintList = new ArrayList<>();

    // Special snowflake that doesn't really belong in a builder.
    // Used to easily register nodes to another node.
    private Node parentNode = null;

    public ConfigValueBuilder(@Nonnull Class<T> type) {
        this.type = type;
    }

    public ConfigValueBuilder<T> withName(String name) {
        this.name = name;
        return this;
    }

    public ConfigValueBuilder<T> withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public ConfigValueBuilder<T> withListener(BiConsumer<T, T> consumer) {
        final BiConsumer<T, T> prevConsumer = this.consumer; // to avoid confusion
        this.consumer = (t, t2) -> {
            prevConsumer.accept(t, t2);
            consumer.accept(t, t2); // The newest consumer is called last -> listeners are called in the order they are added
        };
        return this;
    }

    public ConfigValueBuilder<T> withDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ConfigValueBuilder<T> setFinal() {
        this.isFinal = true;
        return this;
    }

    /**
     * Sets the node that the built {@link ConfigValue} will be registered to.
     * @param node  The node this {@link ConfigValue} will be registered to.
     * @return      The builder
     */
    public ConfigValueBuilder<T> withParent(Node node) {
        parentNode = node;
        return this;
    }

    public ConstraintsBuilder<T> constraints() {
        return new ConstraintsBuilder<>(constraintList, type, this);
    }

    public ConfigValue<T> build() {
        ConfigValue<T> built = new ConfigValue<>(name, comment, defaultValue, defaultValue, consumer, constraintList, type, isFinal);

        if (parentNode != null) {
            // We don't know what kind of evil collection we're about to add a node to.
            // Though, we don't really want to throw an exception on this method because no developer likes try-catching every setting they build.
            // Let's tread with caution.
            try {
                parentNode.add(built);
            } catch (Exception e) {
                throw new RuntimeFiberException("Failed to register leaf to node", e);
            }
        }

        return built;
    }
}
