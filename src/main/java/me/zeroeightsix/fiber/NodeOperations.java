package me.zeroeightsix.fiber;

import me.zeroeightsix.fiber.tree.*;

import java.util.Map;
import java.util.stream.Collectors;

public class NodeOperations {

    /**
     * Merges two {@code ConfigNode} objects.
     *
     * @param from  The {@code ConfigNode} that will be read from, but not mutated.
     * @param to    The mutated {@link ConfigNode} that will inherit <code>from</code>'s values and nodes.
     */
    public static void mergeTo(Node from, Node to) {
        Map<String, TreeItem> map = to.getItems().stream().collect(Collectors.toMap(TreeItem::getName, item -> item));
        from.getItems().forEach(item -> {
            String name = item.getName();
            map.put(name, item);
        });
        to.getItems().clear();
        to.getItems().addAll(map.values());
    }

    /**
     * Merges a leaf node ({@code ConfigValue}) into a {@code ConfigNode}.
     *
     * @param value The leaf node to be inherited
     * @param to    The mutated {@link ConfigNode} that will inherit <code>value</code>
     */
    public static void mergeTo(ConfigValue<?> value, Node to) {
        to.remove(value.getName());
        to.getItems().add(value);
    }

    public static <T> void mergeTo(Property<T> from, Property<T> to) {
        to.setValue(from.getValue());
    }
}
