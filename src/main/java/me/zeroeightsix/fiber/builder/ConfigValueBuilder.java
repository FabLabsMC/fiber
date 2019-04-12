package me.zeroeightsix.fiber.builder;

import me.zeroeightsix.fiber.exceptions.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.ConfigValue;
import me.zeroeightsix.fiber.tree.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigValueBuilder<T> {

    @Nonnull
    private final Class<T> type;
    @Nullable
    private String name;
    @Nullable
    private String comment = null;

    // Special snowflake that doesn't really belong in a builder.
    // Used to easily register nodes to another node.
    private Node parentNode = null;

    public ConfigValueBuilder(@Nonnull Class<T> type) {
        this.type = type;
    }

    public ConfigValueBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public ConfigValueBuilder<T> comment(String comment) {
        this.comment = comment;
        return this;
    }

    public ConfigValueBuilder<T> registerTo(Node node) {
        parentNode = node;
        return this;
    }

    public ConfigValue<T> build() {
        ConfigValue<T> built = new ConfigValue<>(name, comment);

        if (parentNode != null) {
            // We don't know what kind of evil collection we're about to add a node to.
            // Let's tread with caution.
            try {
                parentNode.getItems().add(built);
            } catch (Exception e) {
                throw new RuntimeFiberException("Failed to register leaf to node, exception thrown (" + e.getMessage() + ")", e);
            }
        }

        return built;
    }

}
