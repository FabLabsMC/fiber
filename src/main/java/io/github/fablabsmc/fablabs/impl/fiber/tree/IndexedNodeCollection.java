package io.github.fablabsmc.fablabs.impl.fiber.tree;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.DuplicateChildException;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigNode;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.NodeCollection;

public class IndexedNodeCollection extends AbstractCollection<ConfigNode> implements NodeCollection {
	private final Map<String, ConfigNode> items = new TreeMap<>();
	@Nullable
	private final ConfigBranch owner;

	public IndexedNodeCollection(@Nullable ConfigBranch owner) {
		this.owner = owner;
	}

	@Nonnull
	@Override
	public Iterator<ConfigNode> iterator() {
		return new Iterator<ConfigNode>() {
			@Nullable
			private ConfigNode last;
			private final Iterator<ConfigNode> backing = items.values().iterator();

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
				// order is important to avoid infinite recursion
				this.backing.remove();
				this.last.detach();
			}
		};
	}

	@Override
	public Spliterator<ConfigNode> spliterator() {
		return this.items.values().spliterator();
	}

	@Override
	public boolean add(ConfigNode item) throws DuplicateChildException {
		return add(item, false);
	}

	@Override
	public boolean add(ConfigNode item, boolean overwrite) throws DuplicateChildException {
		Objects.requireNonNull(item);

		if (overwrite) {
			this.removeByName(item.getName());
		} else if (this.items.containsKey(item.getName())) {
			throw new DuplicateChildException("Attempt to replace node " + item.getName());
		}

		this.items.put(item.getName(), item);
		item.attachTo(this.owner);
		return true;
	}

	@Override
	public boolean contains(@Nullable Object o) {
		if (o instanceof ConfigNode) {
			return Objects.equals(this.items.get(((ConfigNode) o).getName()), o);
		}

		return false;
	}

	@Override
	public boolean remove(@Nullable Object child) {
		if (child instanceof ConfigNode) {
			boolean removed = this.items.remove(((ConfigNode) child).getName(), child);

			if (removed) {
				((ConfigNode) child).detach();
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
		ConfigNode removed = this.items.remove(name);

		if (removed != null) {
			removed.detach();
		}

		return removed;
	}
}
