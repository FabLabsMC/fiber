package io.github.fablabsmc.fablabs.impl.fiber.tree;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigLeaf;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.Property;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;

public final class PropertyMirrorImpl<R, S> implements PropertyMirror<R> {
	protected Property<S> delegate;
	protected ConfigType<R, S, ?> mirroredType;
	@Nullable
	private S lastSerializedValue;
	@Nullable
	private R cachedValue;

	public PropertyMirrorImpl(ConfigType<R, S, ?> mirroredType) {
		this.mirroredType = mirroredType;
	}

	/**
	 * Sets a property to mirror.
	 *
	 * <p>After calling this method with a valid delegate,
	 * every property method will redirect to {@code delegate}.
	 *
	 * @param delegate a property to mirror
	 */
	@Override
	public void mirror(Property<?> delegate) {
		if (!this.mirroredType.getSerializedType().getErasedPlatformType().equals(delegate.getType())) {
			throw new IllegalArgumentException("Unsupported delegate type " + delegate.getType() + ", should be " + this.mirroredType.getSerializedType().getErasedPlatformType());
		}

		@SuppressWarnings("unchecked") Property<S> d = (Property<S>) delegate;
		this.delegate = d;

		if (d instanceof ConfigLeaf) {
			// passive invalidation
			((ConfigLeaf<S>) d).addChangeListener((old, cur) -> this.cachedValue = null);
			this.lastSerializedValue = null;
		} else {
			// active invalidation, less efficient
			this.lastSerializedValue = d.getValue();
		}
	}

	@Override
	public Property<?> getMirrored() {
		return this.delegate;
	}

	@Override
	public boolean setValue(@Nonnull R value) {
		if (this.delegate == null) throw new IllegalStateException("No delegate property set for this mirror");
		return this.delegate.setValue(this.mirroredType.toPlatformType(value));
	}

	@Override
	public boolean accepts(@Nonnull R value) {
		if (this.delegate == null) throw new IllegalStateException("No delegate property set for this mirror");
		return this.delegate.accepts(this.mirroredType.toPlatformType(value));
	}

	@Nonnull
	@Override
	public R getValue() {
		if (this.delegate == null) throw new IllegalStateException("No delegate property set for this mirror");

		if (this.cachedValue == null || this.lastSerializedValue != null) {
			S serializedValue = this.delegate.getValue();

			if (cachedValue == null || !Objects.equals(this.lastSerializedValue, serializedValue)) {
				this.cachedValue = this.mirroredType.toRuntimeType(serializedValue);
				this.lastSerializedValue = serializedValue;
			}
		}

		return this.cachedValue;
	}

	@Override
	public Class<R> getType() {
		return this.mirroredType.getRuntimeType();
	}

	@Override
	public ConfigType<R, S, ?> getMirroredType() {
		return this.mirroredType;
	}
}
