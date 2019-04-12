package me.zeroeightsix.fiber;

import blue.endless.jankson.*;
import blue.endless.jankson.impl.Marshaller;
import blue.endless.jankson.impl.SyntaxError;
import me.zeroeightsix.fiber.exceptions.FiberException;
import me.zeroeightsix.fiber.tree.ConfigNode;
import me.zeroeightsix.fiber.tree.HasValue;
import me.zeroeightsix.fiber.tree.Node;
import me.zeroeightsix.fiber.tree.Property;

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

	private static void deserialize(Node node, JsonObject element) {
		JsonObject object = element;
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			String key = entry.getKey();
			JsonElement child = entry.getValue();

			if (child instanceof JsonObject) {
				ConfigNode fork = new ConfigNode(key, object.getComment(key));
				node.getItems().add(fork);
				deserialize(fork, (JsonObject) child);
			} else {
				// TODO
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
				object.put(treeItem.getName(), Marshaller.getFallback().serialize(((HasValue) treeItem).getValue()));
			}
		});

/*		node.getCachesImmutable().forEach(cache -> cache.getCachedNames().forEach(name -> {
			Object o = cache.get(name);
			JsonElement element;
			if (o == null) {
				element = JsonNull.INSTANCE;
			} else {
				element = (JsonElement) JanksonSettings.provideConverter((Class) o.getClass()).serialize(o);
			}
			object.put(name, element);
		}));

		node.getSettingsImmutable().forEach((s, setting) -> {
			object.put(s, (JsonElement) provideConverter(setting.getType()).serialize(setting.getValue()));
			if (setting.hasComment())
				object.setComment(s, setting.getComment());
		});
		node.getSubSettingsImmutable().forEach((s, configNode) -> object.put(s, JanksonSettings.serialize(configNode)));*/

		return object;
	}

	private static JsonElement serialize(Property property) {
		return Marshaller.getFallback().serialize(property.getValue());
	}

}
