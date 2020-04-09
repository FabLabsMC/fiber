package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exception.FiberQueryException;

import javax.annotation.Nonnull;
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
public final class ConfigQuery<T extends ConfigNode> {

    /**
     * Creates a {@code ConfigQuery} for a subtree with a specific path.
     *
     * <p> Each part of the path must correspond to a single node name.
     * The first part matches a direct child node of the root supplied to
     * the {@link #search(NodeLike)} and {@link #run(NodeLike)} methods.
     * Each additional name matches a node such that the <em>n</em>th name
     * matches a node at depth <em>n</em>, starting from the supplied tree.
     *
     * @param first the first name in the config path
     * @param more  additional node names forming the config path
     * @return a config query for subtrees of existing trees
     */
    public static ConfigQuery<ConfigGroup> subtree(String first, String... more) {
        return new ConfigQuery<>(ConfigGroup.class, null, first, more);
    }

    /**
     * Creates a {@code ConfigQuery} for a property with a specific path and value type.
     *
     * <p> Each part of the path must correspond to a single node name.
     * The first part matches a direct child node of the root supplied to
     * the {@link #search(NodeLike)} and {@link #run(NodeLike)} methods.
     * Each additional name matches a node such that the <em>n</em>th name
     * matches a node at depth <em>n</em>, starting from the supplied tree.
     *
     * <p> The returned query will only match a leaf with a {@linkplain Property#getType() property type}
     * that is identical to the given {@code propertyType}.
     *
     * @param propertyType a class object representing the type of values held by queried properties
     * @param first the first name in the config path
     * @param more  additional node names forming the config path
     * @return a config query for subtrees of existing trees
     */
    public static <V> ConfigQuery<ConfigLeaf<V>> property(Class<? super V> propertyType, String first, String... more) {
        return new ConfigQuery<>(ConfigLeaf.class, propertyType, first, more);
    }

    private final List<String> path;
    private final Class<? super T> nodeType;
    @Nullable
    private final Class<?> valueType;

    private ConfigQuery(Class<? super T> nodeType, @Nullable Class<?> valueType, String first, String[] path) {
        this.nodeType = nodeType;
        this.valueType = valueType;
        this.path = new ArrayList<>();
        this.path.add(first);
        this.path.addAll(Arrays.asList(path));
    }

    /**
     * Searches a config tree for a node satisfying this query.
     * If none is found, {@code Optional.empty()} is returned.
     *
     * @param cfg the config tree to search in
     * @return an {@code Optional} describing the queried node,
     * or {@code Optional.empty()}.
     * @see #run(NodeLike)
     */
    public Optional<T> search(NodeLike cfg) {
        try {
            return Optional.of(this.run(cfg));
        } catch (FiberQueryException e) {
            return Optional.empty();
        }
    }

    /**
     * Runs this query on a config tree.
     *
     * <p> If this query's parameters do not match the config's structure,
     * a {@link FiberQueryException} carrying error details is thrown.
     * The exception's information can be used for further handling of the erroring config.
     *
     * @param cfg the config tree to run the query on
     * @return the queried node, with the right path and type
     * @throws FiberQueryException if this query's parameters do not match the config's structure
     * @see FiberQueryException.MissingChild
     * @see FiberQueryException.WrongType
     * @see #search(NodeLike)
     */
    @Nonnull
    public T run(NodeLike cfg) throws FiberQueryException {
        List<String> path = this.path;
        NodeLike subtree = cfg;
        int lastIndex = path.size() - 1;
        for (int i = 0; i < lastIndex; i++) {
            subtree = this.lookupChild(subtree, path.get(i), ConfigGroup.class, null);
        }
        @SuppressWarnings("unchecked") T result =
                (T) this.lookupChild(subtree, path.get(lastIndex), this.nodeType, this.valueType);
        return result;
    }

    private <N> N lookupChild(NodeLike tree, String name, Class<N> nodeType, @Nullable Class<?> valueType) throws FiberQueryException {
        ConfigNode node = tree.lookup(name);
        if (nodeType.isInstance(node) && (valueType == null || valueType == ((ConfigLeaf<?>) node).getType())) {
            return nodeType.cast(node);
        } else if (node != null) {
            throw new FiberQueryException.WrongType(tree, node, nodeType, valueType);
        } else {
            throw new FiberQueryException.MissingChild(name, tree);
        }
    }

    /**
     * Returns a string representation of this query.
     *
     * <p> The string representation consists of the expected node type, followed
     * by the expected value type, if any, followed by a representation of this
     * query's path where individual node names are joined by dots.
     *
     * @return a string representation of this query
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(nodeType.getSimpleName());
        if (valueType != null) {
            sb.append('<').append(valueType.getSimpleName()).append('>');
        }
        return sb.append("@'").append(String.join(".", path)).append('\'').toString();
    }
}
