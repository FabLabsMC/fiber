package me.zeroeightsix.fiber.tree;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A query that can be run against any config tree to try and get a node.
 *
 * <p> A {@code ConfigQuery} follows a path in the tree, represented by a list of strings.
 * It can notably be used to retrieve nodes from various config trees with a similar structure.
 *
 * @param <T> the type of queried tree nodes
 */
public final class ConfigQuery<T extends TreeItem> {
    public static ConfigQuery<Node> node(String first, String... more) {
        return new ConfigQuery<>(Node.class, null, first, more);
    }

    public static <V> ConfigQuery<ConfigValue<V>> property(Class<V> valueType, String first, String... more) {
        return new ConfigQuery<>(ConfigValue.class, valueType, first, more);
    }

    private final List<String> path;
    private final Class<? super T> nodeType;
    @Nullable private final Class<?> valueType;

    private ConfigQuery(Class<? super T> nodeType, @Nullable Class<?> valueType, String first, String[] path) {
        this.nodeType = nodeType;
        this.valueType = valueType;
        this.path = new ArrayList<>();
        this.path.add(first);
        this.path.addAll(Arrays.asList(path));
    }

    public Optional<T> run(NodeLike tree) {
        NodeLike subTree = tree;
        List<String> path = this.path;
        int len = path.size();
        for (int i = 0; i < len; i++) {
            TreeItem node = subTree.lookup(path.get(i));
            if (i == len - 1) {
                if (isValidResult(node)) {
                    @SuppressWarnings("unchecked") Optional<T> result = (Optional<T>) Optional.of(node);
                    return result;
                }
            } else if (node instanceof NodeLike) {
                subTree = (NodeLike) node;
            } else {
                break;
            }
        }
        return Optional.empty();
    }

    private boolean isValidResult(TreeItem node) {
        return nodeType.isInstance(node) && (valueType == null || valueType.isAssignableFrom(((ConfigValue<?>)node).getType()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(nodeType.getSimpleName());
        if (valueType != null) {
            sb.append('<').append(valueType.getSimpleName()).append('>');
        }
        return sb.append("@'").append(String.join(".", path)).append('\'').toString();
    }
}
