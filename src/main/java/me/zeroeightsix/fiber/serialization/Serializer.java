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
 * @param <T> the type of intermediary objects processed by this serializer
 * @see Node
 * @see JanksonSerializer
 */
public interface Serializer<T> {

    Identifier getIdentifier();

    void deserialize(Node node, InputStream stream) throws FiberException, IOException;

    void deserialize(Node node, T element) throws FiberException;

    void serialize(Node node, OutputStream stream) throws FiberException, IOException;

    T serialize(Node node);

}
