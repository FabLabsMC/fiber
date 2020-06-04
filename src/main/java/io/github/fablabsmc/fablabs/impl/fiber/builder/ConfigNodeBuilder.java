package io.github.fablabsmc.fablabs.impl.fiber.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.FiberId;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigAttribute;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigNode;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;

public abstract class ConfigNodeBuilder {
	@Nullable
	protected ConfigTree parent;
	@Nullable
	protected String name;
	@Nullable
	protected String comment = null;
	protected Map<FiberId, ConfigAttribute<?>> attributes;

	public ConfigNodeBuilder(@Nullable ConfigTree parent, @Nullable String name) {
		if (parent != null && name == null) throw new IllegalArgumentException("A child node needs a name");
		this.parent = parent;
		this.name = name;
		this.attributes = new HashMap<>();
	}

	/**
	 * Sets the {@code ConfigNode}'s name.
	 *
	 * @param name the name
	 * @return {@code this} builder
	 * @see ConfigTree#lookupLeaf
	 */
	public ConfigNodeBuilder withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Sets the {@code ConfigNode}'s comment.
	 *
	 * <p>If {@code null}, or if this method is never called, the {@code ConfigNode} will not have a comment.
	 * An empty comment (non null, but only consisting of whitespace) will be serialised.
	 *
	 * @param comment the comment
	 * @return {@code this} builder
	 */
	public ConfigNodeBuilder withComment(String comment) {
		this.comment = comment;
		return this;
	}

	/**
	 * Adds a {@link ConfigAttribute} to the built {@code ConfigNode}.
	 *
	 * @param id           the id of the attribute
	 * @param type         the class object representing the type of values stored in the attribute
	 * @param defaultValue the attribute's default value
	 * @param <A>          the type of values stored in the attribute
	 * @return {@code this}, for chaining
	 * @see ConfigNode#getAttributes()
	 */
	public <A> ConfigNodeBuilder withAttribute(FiberId id, SerializableType<A> type, A defaultValue) {
		return this.withAttribute(ConfigAttribute.create(id, type, defaultValue));
	}

	/**
	 * Adds a collection of {@link ConfigAttribute} to the built {@code ConfigNode}.
	 *
	 * @param attributes A collection of attributes.
	 * @return This builder.
	 */
	public ConfigNodeBuilder withAttributes(Collection<ConfigAttribute<?>> attributes) {
		for (ConfigAttribute<?> attribute : attributes) {
			this.withAttribute(attribute);
		}

		return this;
	}

	/**
	 * Adds a single {@link ConfigAttribute} to the built {@code ConfigNode}.
	 *
	 * @param attribute The attribute.
	 * @return This builder.
	 */
	public ConfigNodeBuilder withAttribute(ConfigAttribute<?> attribute) {
		this.attributes.put(attribute.getIdentifier(), attribute);
		return this;
	}

	/**
	 * Builds and returns a new {@code ConfigNode} with the parent and name stored in this builder.
	 */
	public abstract ConfigNode build();
}
