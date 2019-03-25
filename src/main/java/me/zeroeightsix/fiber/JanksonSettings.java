package me.zeroeightsix.fiber;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.impl.SyntaxError;
import me.zeroeightsix.fiber.builder.Converter;
import me.zeroeightsix.fiber.ir.ConfigNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class JanksonSettings {

	public static void deserialize(ConfigNode node, InputStream stream) throws IOException {
	    node.setConverterFunction(JanksonSettings::provideConverter);
		Jankson jankson = Jankson.builder().build();
		try {
			JsonElement element = jankson.load(stream);
			JanksonSettings.deserialize(node, element);
		} catch (SyntaxError syntaxError) {
			syntaxError.printStackTrace();
		}
	}

	private static void deserialize(ConfigNode node, JsonElement element) {
		if (!(element instanceof JsonObject)) {
			throw new IllegalStateException("Root of configuration must be a jankson object");
		}

		JsonObject object = (JsonObject) element;
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			String key = entry.getKey();
			JsonElement child = entry.getValue();

			if (child instanceof JsonObject) {
			    deserialize(node.sub(key), child);
			} else {
			    node.set(key, child);
			}
		}
	}

	public static void serialize(ConfigNode node, OutputStream stream, boolean compress) throws IOException {
		JsonObject object = serialize(node);
		stream.write(object.toJson(!compress, !compress).getBytes());
	}

	private static JsonObject serialize(ConfigNode node) {
		JsonObject object = new JsonObject();

		node.getCachedValuesImmutable().forEach((s, value) -> object.put(s, ((JsonElement) value)));
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

}
