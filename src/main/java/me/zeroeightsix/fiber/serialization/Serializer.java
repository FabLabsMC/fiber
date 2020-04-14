package me.zeroeightsix.fiber.serialization;

import me.zeroeightsix.fiber.FiberId;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.tree.ConfigTree;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A {@code Serializer} serializes and deserializes data from a certain format into and from {@code Node}s
 *
 * @param <T> the type of serialized objects processed by this serializer
 * @see ConfigTree
 * @see JanksonSerializer
 */
public interface Serializer<T> {

    FiberId getIdentifier();

    /**
     * Deserializes data from an {@code InputStream} into a config tree.
     *
     * <p> If the serialized tree has elements not present in the actual node,
     * they will be collected as a separate data structure and returned.
     *
     * @param tree the node to deserialize
     * @param in the serialized data for the node
     * @return the unprocessed elements, in the same format as the input data
     */
    T deserialize(ConfigTree tree, InputStream in) throws FiberException, IOException;

    /**
     * Deserializes an intermediate element into a config tree.
     *
     * <p> If the serialized tree has elements not present in the actual node,
     * they will be collected as a separate data structure and returned.
     *
     * @param tree the node to deserialize
     * @param element the serialized data for the node
     * @return the unprocessed elements, in the same format as the input data
     */
    T deserialize(ConfigTree tree, T element) throws FiberException;

    /**
     * Serializes a config tree and writes it to an {@code OutputStream}.
     *
     * @param tree the node to serialize
     * @param out the stream to which the serialized data is to be written
     */
    default void serialize(ConfigTree tree, OutputStream out) throws FiberException, IOException {
        serialize(tree, null, out);
    }

    /**
     * Serializes a config tree and writes it to an {@code OutputStream}.
     *
     * <p> The caller may pass in additional data to be appended to the serialized
     * tree. In case of conflict between the serialized tree and the additional data
     * (eg. map representation with conflicting keys), the conflicting data from the
     * serialized tree gets overwritten.
     *
     * <p> The {@code additionalData} may be used with the leftovers from
     * {@link #deserialize(ConfigTree, InputStream)} to inject back unrecognized nodes.
     *
     * @param tree the node to serialize
     * @param additionalData data to append to the serialized tree
     * @param out the stream to which the serialized data is to be written
     */
    void serialize(ConfigTree tree, @Nullable T additionalData, OutputStream out) throws FiberException, IOException;

    /**
     * Serializes a config tree.
     *
     * @param tree the node to serialize
     * @return the serialized data for the node
     */
    T serialize(ConfigTree tree) throws FiberException;

}
