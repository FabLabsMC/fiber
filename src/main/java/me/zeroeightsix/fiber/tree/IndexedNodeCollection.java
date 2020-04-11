package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exception.DuplicateChildException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class IndexedNodeCollection extends AbstractCollection<ConfigNode> implements NodeCollection {
    private final Map<String, ConfigNodeImpl> items = new TreeMap<>();
    @Nullable private final ConfigBranch owner;

    public IndexedNodeCollection(@Nullable ConfigBranch owner) {
        this.owner = owner;
    }

    public IndexedNodeCollection(@Nullable ConfigBranch owner, Collection<ConfigNode> items) {
        this(owner);
        this.addAll(items);
    }

    @Nonnull
    @Override
    public Iterator<ConfigNode> iterator() {
    return new Iterator<ConfigNode>() {
            @Nullable private ConfigNodeImpl last;
            private Iterator<ConfigNodeImpl> backing = items.values().iterator();

            @Override
            public boolean hasNext() {
                return backing.hasNext();
            }

            @Override
            public ConfigNode next() {
                this.last = this.backing.next();
                return last;
            }

            @Override
            public void remove() {
                if (this.last == null) throw new IllegalStateException();
                this.backing.remove();
                this.last.detach();
            }
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Spliterator</*out*/ ConfigNode> spliterator() {
        // The Spliterator interface is read-only, so we consider its element type covariant
        return (Spliterator/*<out ConfigNode>*/) this.items.values().spliterator();
    }

    @Override
    public boolean add(ConfigNode item) throws DuplicateChildException {
        return add(item, false);
    }

    @Override
    public boolean add(ConfigNode item, boolean overwrite) throws DuplicateChildException {
        Objects.requireNonNull(item);
        if (!(item instanceof ConfigNodeImpl)) {
            throw new IllegalArgumentException("Invalid config node implementation " + item);
        }
        if (overwrite) {
            this.removeByName(item.getName());
        } else if (this.items.containsKey(item.getName())) {
            throw new DuplicateChildException("Attempt to replace node " + item.getName());
        }
        this.items.put(item.getName(), (ConfigNodeImpl) item);
        ((ConfigNodeImpl) item).setParent(this.owner);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof ConfigNodeImpl) {
            boolean removed = this.items.remove(((ConfigNode) o).getName(), o);
            if (removed) {
                ((ConfigNodeImpl) o).detach();
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public ConfigNode getByName(String name) {
        return this.items.get(name);
    }

    @Override
    @Nullable
    public ConfigNode removeByName(String name) {
        ConfigNodeImpl removed = this.items.remove(name);
        if (removed != null) {
            removed.detach();
        }
        return removed;
    }
}
