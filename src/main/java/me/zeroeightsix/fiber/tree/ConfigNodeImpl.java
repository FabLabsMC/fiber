package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.FiberId;
import me.zeroeightsix.fiber.exception.IllegalTreeStateException;
import me.zeroeightsix.fiber.schema.ConfigType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * A commentable node.
 *
 * @see ConfigNode
 * @see ConfigBranchImpl
 * @see ConfigLeafImpl
 */
public abstract class ConfigNodeImpl implements ConfigNode, Commentable {

    private final Map<FiberId, ConfigAttribute<?>> attributes;
    @Nonnull
    private final String name;
    @Nullable
    private final String comment;
    @Nullable
    private ConfigBranch parent;

    /**
     * Creates a new {@code ConfigLeaf}.
     *
     * @param name    the name for this leaf
     * @param comment the comment for this leaf
     */
    public ConfigNodeImpl(@Nonnull String name, @Nullable String comment) {
        this.attributes = new TreeMap<>(Comparator.comparing(FiberId::toString));
        this.name = name;
        this.comment = comment;
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    @Nullable
    public String getComment() {
        return comment;
    }

    @Nullable
    @Override
    public ConfigBranch getParent() {
        return this.parent;
    }

    @Override
    public Map<FiberId, ConfigAttribute<?>> getAttributes() {
        return this.attributes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> ConfigAttribute<A> getOrCreateAttribute(FiberId id, ConfigType<A, A> attributeType, @Nullable A defaultValue) {
        ConfigAttribute<?> attr = getAttributes().computeIfAbsent(id, i -> new ConfigAttributeImpl<>(attributeType, defaultValue));
        checkAttributeType(attributeType, attr);
        return (ConfigAttribute<A>) attr;
    }

    @Override
    public <A> Optional<A> getAttributeValue(FiberId id, ConfigType<A, A> expectedType) {
        ConfigAttribute<?> attr = this.attributes.get(id);
        if (attr != null) {
            checkAttributeType(expectedType, attr);
            return Optional.ofNullable(expectedType.getActualType().cast(attr.getValue()));
        }
        return Optional.empty();
    }

    private static <A> void checkAttributeType(ConfigType<A, A> expectedType, ConfigAttribute<?> attr) {
        if (!expectedType.equals(attr.getConfigType())) {
            throw new ClassCastException("Attempt to retrieve a value of type " + expectedType + " from attribute with type " + attr.getConfigType());
        }
    }

    @Override
    public void detach() {
        // Note: infinite recursion between ConfigNode#detach() and NodeCollection#remove() could occur here,
        // but the latter performs the actual collection removal before detaching
        if (this.parent != null) {
            // here, we also need to avoid triggering a ConcurrentModificationException
            // thankfully, remove does not cause a CME if it's a no-op
            this.parent.getItems().remove(this);
        }
        this.parent = null;
    }

    @Override
    public void attachTo(ConfigBranch parent) {
        if (this.parent != null && this.parent != parent) {
            throw new IllegalTreeStateException(this + " needs to be detached before changing the parent");
        }
        // this node may have already been added by the collection
        if (parent != null && !parent.getItems().contains(this)) {
            parent.getItems().add(this);
        }
        this.parent = parent;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + getName() + ", comment=" + getComment() + "]";
    }
}
