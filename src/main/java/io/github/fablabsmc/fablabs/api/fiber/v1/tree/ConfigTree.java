package io.github.fablabsmc.fablabs.api.fiber.v1.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.NodeOperations;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;

public interface ConfigTree {
	/**
	 * @return a new builder for a root config node
	 */
	static ConfigTreeBuilder builder() {
		return builder(null, null);
	}

	/**
	 * Creates a new non-root {@link ConfigTreeBuilder} with a name.
	 *
	 * <p>The built subtree will be attached to {@code parent}, with the given {@code name}.
	 * To generate a whole tree from the root, use {@link ConfigTree#builder()} instead.
	 *
	 * @param parent the parent of the builder to create
	 * @param name   the name of the {@link ConfigTree} created by the builder
	 * @return a {@code ConfigTreeBuilder} for an intermediary config branch
	 * @see ConfigTree#builder()
	 */
	static ConfigTreeBuilder builder(@Nullable ConfigTree parent, @Nullable String name) {
		return new ConfigTreeBuilder(parent, name);
	}

	/**
	 * Returns a collection of this node's children.
	 *
	 * <p>The returned collection is guaranteed to have no two nodes with the same name.
	 * Any changes made to it will be reflected by this tree.
	 *
	 * @return the set of children
	 * @see NodeOperations
	 */
	@Nonnull
	NodeCollection getItems();

	/**
	 * Tries to find a child in this node by name. If a child is found, it will be returned.
	 *
	 * @param name The name of the child to look for.
	 * @return the child if found, otherwise {@code null}.
	 */
	@Nullable
	ConfigNode lookup(String name);

	/**
	 * Tries to find a child branch in this node by name. If a child is found, and it is
	 * a branch node, it is returned.
	 *
	 * @param name The name of the child to look for.
	 * @return The child branch if found, otherwise null.
	 */
	@Nullable
	ConfigBranch lookupBranch(String name);

	/**
	 * Tries to find a child leaf in this node by name.
	 * If a child with the right type is found, it will be returned.
	 *
	 * @param name the name of the leaf to look for
	 * @param type a {@link SerializableType} object representing the type of values held by the leaf
	 * @param <T>  the type of values held by the leaf
	 * @return the leaf if found, otherwise {@code null}
	 */
	@Nullable
	// should we throw an exception on wrong type instead ?
	<T> ConfigLeaf<T> lookupLeaf(String name, SerializableType<T> type);

	/**
	 * Tries to find a child leaf in this node by name.
	 * If a child with the right type is found, the mirror will be bound to it.
	 *
	 * @param name   the name of the leaf to mirror
	 * @param mirror the mirror to bind to the leaf
	 * @return {@code true} if the operation succeeded
	 */
	boolean lookupAndBind(String name, PropertyMirror<?> mirror);
}
