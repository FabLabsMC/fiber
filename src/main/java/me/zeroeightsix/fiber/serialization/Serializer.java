package me.zeroeightsix.fiber.serialization;

import me.zeroeightsix.fiber.Identifier;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.tree.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A {@code Serializer} serializes and deserializes data from a certain format into and from {@code Node}s
 *
 * @param <T> the type of serialized objects processed by this serializer
 * @see Node
 * @see JanksonSerializer
 */
public interface Serializer<T> {

    Identifier getIdentifier();

    void deserialize(Node node, InputStream stream) throws FiberException, IOException;

    /**
     * Deserializes an intermediate element into a config tree.
     *
     * <p> If the serialized tree has elements not present in the actual node,
     * they will be collected as a separate data structure and returned.
     *
     * @param node the node to deserialize
     * @param element the serialized data for the node
     * @return the unprocessed elements, in the same format as the input data
     */
    T deserialize(Node node, T element) throws FiberException;

    void serialize(Node node, OutputStream stream) throws FiberException, IOException;

    T serialize(Node node) throws FiberException;

}
