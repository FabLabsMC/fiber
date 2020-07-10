package io.github.fablabsmc.fablabs.impl.fiber.annotation;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.FiberId;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.IllegalTreeStateException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.RuntimeFiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigAttribute;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigLeaf;

/**
 * A config leaf backed by a field, {@linkplain ConfigType}, and deferred leaf.
 *
 * <p>It is used to fetch the backing field's values on each {@link #getValue()} call, to make sure a leaf and its corresponding POJO field are always synchronised.
 *
 * @param <R>
 * @param <S>
 */
public class BackedConfigLeaf<R, S> implements ConfigLeaf<S> {
	private final ConfigLeaf<S> backing;
	private final ConfigType<R, S, ?> type;
	private final Object pojo;
	private final Field backingField;
	private R cachedValue = null;
	private ConfigBranch parent;

	public BackedConfigLeaf(ConfigLeaf<S> backing, ConfigType<R, S, ?> type, Object pojo, Field backingField) {
		this.backing = backing;
		this.type = type;
		this.pojo = pojo;
		this.backingField = backingField;
	}

	@Override
	public String getName() {
		return backing.getName();
	}

	@Override
	public Map<FiberId, ConfigAttribute<?>> getAttributes() {
		return backing.getAttributes();
	}

	@Override
	public <R, A> Optional<R> getAttributeValue(FiberId id, ConfigType<R, A, ?> type) {
		return backing.getAttributeValue(id, type);
	}

	@Override
	public <A> Optional<A> getAttributeValue(FiberId id, SerializableType<A> expectedType) {
		return backing.getAttributeValue(id, expectedType);
	}

	@Override
	public <A> ConfigAttribute<A> getOrCreateAttribute(FiberId id, SerializableType<A> attributeType, @Nullable A defaultValue) {
		return backing.getOrCreateAttribute(id, attributeType, defaultValue);
	}

	@Nullable
	@Override
	public ConfigBranch getParent() {
		return parent;
	}

	@Override
	public void attachTo(ConfigBranch parent) throws IllegalTreeStateException {
		// Attaching/detaching requires careful inspection of whether or not the parent's items contains `this`.
		// In the case of deferred leaves, the leaf in the parent's items is the deferred leaf and not the backing leaf.
		// Thus, we can't defer attaching/detaching to the backing leaf, as it will think it's not part of the parent's items.
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
	public boolean setValue(@Nonnull S value) {
		if (this.backing.setValue(value)) {
			try {
				this.backingField.setAccessible(true);
				value = backing.getValue(); // Might've changed after a type check + correction, so we fetch again
				this.backingField.set(pojo, type.toRuntimeType(value));
			} catch (IllegalAccessException e) {
				throw new RuntimeFiberException("Failed to update field value", e);
			}

			return true;
		}

		return false;
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public S getValue() {
		try {
			backingField.setAccessible(true);
			R fieldValue = (R) backingField.get(pojo);

			if (!Objects.equals(fieldValue, cachedValue)) {
				this.backing.setValue(type.toSerializedType(fieldValue));
				cachedValue = fieldValue;
			}
		} catch (IllegalAccessException e) {
			// Because this exception might appear to happen 'at random' to the user, we wrap it to at least provide more information about what just happened
			throw new RuntimeFiberException("Couldn't fetch setting value from POJO", e);
		}

		return backing.getValue();
	}

	@Override
	public SerializableType<S> getConfigType() {
		return backing.getConfigType();
	}

	@Nonnull
	@Override
	public BiConsumer<S, S> getListener() {
		return backing.getListener();
	}

	@Override
	public void addChangeListener(BiConsumer<S, S> listener) {
		backing.addChangeListener(listener);
	}

	@Nullable
	@Override
	public S getDefaultValue() {
		return backing.getDefaultValue();
	}

	@Override
	public String getComment() {
		return backing.getComment();
	}

	@Override
	public boolean accepts(@Nonnull S rawValue) {
		return backing.accepts(rawValue);
	}
}
