package io.github.fablabsmc.fablabs.api.fiber.v1.tree;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.DuplicateChildException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.IllegalTreeStateException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.FiberId;

/**
 * The building block of a tree: every node implement this interface.
 */
public interface ConfigNode {
	/**
	 * Returns this node's name.
	 *
	 * @return this node's name
	 */
	String getName();

	/**
	 * Returns the map describing the attributes of this node.
	 *
	 * <p>Attributes store metadata pertaining to the node itself, rather than its value.
	 * The returned map can be mutated by third parties to supplement the default node metadata.
	 * Examples of attributes include translation keys or rendering information.
	 *
	 * <p>As the returned data structure is shared by every attribute source,
	 * attributes should be grouped by namespace.
	 *
	 * @return this node's configurable attributes
	 */
	Map<FiberId, ConfigAttribute<?>> getAttributes();

	/**
	 * Retrieves the value of the attribute with the given id and converts it to a runtime type.
	 *
	 * @param id   the attribute's id
	 * @param type a {@code ConfigType} describing the type of values expected and
	 *             how to convert them to the desired runtime type
	 * @param <R>  the runtime type to which the attribute value will be converted
	 * @param <A>  the type of values expected from the attribute
	 * @return an {@code Optional} describing the value of the attribute,
	 * or an empty {@code Optional} if the attribute does not exist
	 * @throws ClassCastException if the attribute exists but has a type that is not assignable to {@code type}
	 */
	<R, A> Optional<R> getAttributeValue(FiberId id, ConfigType<R, A, ?> type);

	/**
	 * Retrieves the value of the attribute with the given id.
	 *
	 * @param id           the attribute's id
	 * @param expectedType a {@code SerializableType} describing the type of values expected
	 * @param <A>          the type of values expected from the attribute
	 * @return an {@code Optional} describing the value of the attribute,
	 * or an empty {@code Optional} if the attribute does not exist
	 * @throws ClassCastException if the attribute exists but has a type that is not assignable to {@code expectedType}
	 */
	<A> Optional<A> getAttributeValue(FiberId id, SerializableType<A> expectedType);

	/**
	 * Retrieves the attribute with the given id. If it does not exist, one is created with the given type and default value.
	 *
	 * @param <A>           the type of value stored by the attribute
	 * @param id            the id of the desired attribute
	 * @param attributeType the type of values held by the attribute
	 * @param defaultValue  the default value, used if the attribute does not exist
	 * @return the current (existing or computed) attribute associated with the given id
	 * @see #getAttributes()
	 */
	<A> ConfigAttribute<A> getOrCreateAttribute(FiberId id, SerializableType<A> attributeType, @Nullable A defaultValue);

	/**
	 * Returns this node's parent, if any.
	 *
	 * @return this node's parent, or {@code null} if it is not part of a config tree
	 */
	@Nullable
	ConfigBranch getParent();

	/**
	 * Attaches this node to an existing branch.
	 *
	 * <p>After this method has returned normally, this node will be part
	 * of the branch's {@linkplain ConfigBranch#getItems() children}, and this
	 * node's parent will be set to {@code parent}.
	 *
	 * <p>If {@code parent} is {@code null}, this method does not mutate any state.
	 * It will however still throw {@code IllegalTreeStateException} if this node
	 * is not in a suitable state to be attached to another parent. To detach the node
	 * from its current parent, use {@link #detach()}.
	 *
	 * @param parent the new parent branch for this node
	 * @throws DuplicateChildException   if the new parent already has a child of the same name
	 * @throws IllegalTreeStateException if this node is already attached to another branch
	 * @see NodeCollection#add(ConfigNode, boolean)
	 */
	void attachTo(ConfigBranch parent) throws DuplicateChildException, IllegalTreeStateException;

	/**
	 * Detaches this node from its parent branch, if any.
	 *
	 * <p>After this method has returned, this node will be removed from
	 * the current parent's {@linkplain ConfigBranch#getItems() children}, and this
	 * node's parent will be set to {@code null}.
	 *
	 * <p>This method has no effect is this node has no parent.
	 *
	 * @see NodeCollection#remove(Object)
	 */
	void detach();
}
