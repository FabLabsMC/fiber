package me.zeroeightsix.fiber;

import blue.endless.jankson.*;
import blue.endless.jankson.impl.Marshaller;
import blue.endless.jankson.impl.SyntaxError;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.tree.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class JanksonSettings {

	public static void deserialize(Node node, InputStream stream) throws IOException, FiberException {
		Jankson jankson = Jankson.builder().build();
		JsonObject object;
		try {
			object = jankson.load(stream);
		} catch (SyntaxError syntaxError) {
			throw new FiberException("Configuration file was malformed", syntaxError);
		}
		JanksonSettings.deserialize(node, object);
	}

	private static void deserialize(Node node, JsonObject element) throws FiberException {
		JsonObject object = element;
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			String key = entry.getKey();
			JsonElement child = entry.getValue();

            // TODO: Fiber marshaller system
            TreeItem item = node.lookup(key);
            if (item != null) {
                if (item instanceof Property) {
                    Property property = (Property) item;
                    Class type = property.getType();
                    property.setValue(Marshaller.getFallback().marshall(type, child));
                } else {
                    throw new FiberException("Value read for non-property node: " + item.getName());
                }
            } else {
                JanksonTransparentNode transparentNode = new JanksonTransparentNode(key, child);
                node.add(transparentNode);
            }
		}
	}

	public static void serialize(Node node, OutputStream stream, boolean compress) throws IOException {
		JsonObject object = serialize(node);
		stream.write(object.toJson(!compress, !compress).getBytes());
	}

	private static JsonObject serialize(Node node) {
		JsonObject object = new JsonObject();

		node.getItems().forEach(treeItem -> {
			if (treeItem instanceof Node) {
				Node subNode = (Node) treeItem;
				object.put(subNode.getName(), serialize(subNode));
				return;
			}

			if (treeItem instanceof HasValue) {
				object.put(treeItem.getName(), serialize((HasValue) treeItem));
			}
		});

		return object;
	}

	private static JsonElement serialize(HasValue hasValue) {
		// TODO: Fiber marshaller system
		return Marshaller.getFallback().serialize(hasValue.getValue());
	}

	private static class JanksonTransparentNode implements Transparent {

	    private final String name;
	    private final JsonElement value;

        public JanksonTransparentNode(String name, JsonElement value) {
            this.name = name;
            this.value = value;
        }

        @Nullable
        @Override
        public <A> A marshal(Class<A> type) {
            // TODO: Fiber marshaller
            return Marshaller.getFallback().marshall(type, this.value);
        }

        @Override
        public String getName() {
            return name;
        }

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[name=" + getName() + "]";
		}
	}

}
