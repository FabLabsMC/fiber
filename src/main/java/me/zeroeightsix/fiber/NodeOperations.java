package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
import me.zeroeightsix.fiber.tree.*;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeOperations {

    /**
     * Merges two {@code ConfigNode} objects.
     *
     * <p> The first parameter {@code from} remains unchanged, but {@code to} will be mutated and receive all of {@code from}'s children.
     *
     * <p> If both nodes have one or more children with the same name, the child from {@code from} takes priority.
     *
     * @param from  The {@code ConfigNode} that will be read from, but not mutated.
     * @param to    The mutated {@link ConfigNode} that will inherit <code>from</code>'s values and nodes.
     */
    public static void mergeTo(NodeLike from, ConfigNodeBuilder to) {
        Map<String, TreeItem> map = Stream.concat(
                to.getItems().stream(),
                from.getItems().stream()
        ).collect(Collectors.toMap(TreeItem::getName, item -> item, (t1, t2) -> t1));
        to.getItems().clear();
        to.getItems().addAll(map.values());
    }

    /**
     * Merges a leaf node ({@code ConfigValue}) into a {@code ConfigNode}.
     *
     * @param value The leaf node to be inherited
     * @param to    The mutated {@link ConfigNode} that will inherit <code>value</code>
     */
    public static void mergeTo(ConfigValue<?> value, ConfigNodeBuilder to) {
        to.remove(value.getName());
        to.getItems().add(value);
    }

    public static <T> void mergeTo(Property<T> from, Property<T> to) {
        to.setValue(from.getValue());
    }
}
