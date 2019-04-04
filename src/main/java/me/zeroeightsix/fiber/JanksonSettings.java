package me.zeroeightsix.fiber;

import blue.endless.jankson.*;
import blue.endless.jankson.impl.SyntaxError;
import me.zeroeightsix.fiber.exceptions.FiberException;
import me.zeroeightsix.fiber.tree.Cache;
import me.zeroeightsix.fiber.tree.ConfigNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class JanksonSettings {

	public static void deserialize(ConfigNode node, InputStream stream) throws IOException, FiberException {
	    Cache cache = new Cache() {
			@Override
			public Object get(String name) {
				Object o = super.get(name);
				if (o == null || o instanceof JsonNull) return null;
				return JanksonSettings.provideConverter((Class) o.getClass()).deserialize(o);
			}
		};
	    node.addCache(cache);
		Jankson jankson = Jankson.builder().build();
		JsonElement element = null;
		try {
			element = jankson.load(stream);
		} catch (SyntaxError syntaxError) {
			throw new FiberException("Configuration file was malformed", syntaxError);
		}
		JanksonSettings.deserialize(node, element, cache);
	}

	private static void deserialize(ConfigNode node, JsonElement element, Cache cache) throws FiberException {
		if (!(element instanceof JsonObject)) {
			throw new FiberException("Root of configuration must be a jankson object");
		}

		JsonObject object = (JsonObject) element;
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			String key = entry.getKey();
			JsonElement child = entry.getValue();

			if (child instanceof JsonObject) {
			    deserialize(node.sub(key), child, cache);
			} else {
			    node.setOrCache(key, child, cache);
			}
		}
	}

	public static void serialize(ConfigNode node, OutputStream stream, boolean compress) throws IOException {
		JsonObject object = serialize(node);
		stream.write(object.toJson(!compress, !compress).getBytes());
	}

	private static JsonObject serialize(ConfigNode node) {
		JsonObject object = new JsonObject();

		node.getCachesImmutable().forEach(cache -> cache.getCachedNames().forEach(name -> {
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
		node.getSubSettingsImmutable().forEach((s, configNode) -> object.put(s, JanksonSettings.serialize(configNode)));
		return object;
	}

	private static <T> Converter<JsonElement, T> provideConverter(Class<T> type) {
		return new Converter<JsonElement, T>() {
			@Override
			public T deserialize(JsonElement data) {
				return (T) ((JsonPrimitive) data).getValue(); // this couldn't be more unsafe
			}

			@Override
			public JsonElement serialize(T object) {
				return new JsonPrimitive(object);
			}
		};
	}

    public interface Converter<F, T> {

        F serialize(T data);
        T deserialize(F object);

    }
}
