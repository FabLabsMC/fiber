package me.zeroeightsix.fiber.tree;

import me.zeroeightsix.fiber.exception.DuplicateChildException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class IndexedNodeCollection extends AbstractCollection<ConfigNode> implements NodeCollection {
    private final Map<String, ConfigNodeImpl> items = new TreeMap<>();
    @Nullable private final ConfigGroup owner;

    public IndexedNodeCollection(@Nullable ConfigGroup owner) {
        this.owner = owner;
    }

    public IndexedNodeCollection(@Nullable ConfigGroup owner, Collection<ConfigNode> items) {
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

    @Override
    public boolean add(ConfigNode item) throws DuplicateChildException {
        Objects.requireNonNull(item);
        if (!(item instanceof ConfigNodeImpl)) {
            throw new IllegalArgumentException("Invalid config node implementation " + item);
        }
        if (this.items.containsKey(item.getName())) {
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
    public ConfigNodeImpl getByName(String name) {
        return this.items.get(name);
    }

    @Override
    @Nullable
    public ConfigNodeImpl removeByName(String name) {
        ConfigNodeImpl removed = this.items.remove(name);
        if (removed != null) {
            removed.detach();
        }
        return removed;
    }
}
