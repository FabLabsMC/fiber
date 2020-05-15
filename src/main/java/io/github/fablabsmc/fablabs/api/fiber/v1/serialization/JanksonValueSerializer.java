package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;

/**
 * {@link ValueSerializer} for Jankson.
 */
public class JanksonValueSerializer implements ValueSerializer<JsonElement, JsonObject> {
	private final Jankson jankson;

	public JanksonValueSerializer() {
		this(Jankson.builder().build());
	}

	public JanksonValueSerializer(Jankson jankson) {
		this.jankson = jankson;
	}

	@Override
	public JsonElement serializeBoolean(boolean value) {
		return value ? JsonPrimitive.TRUE : JsonPrimitive.FALSE;
	}

	@Override
	public boolean deserializeBoolean(JsonElement elem) throws ValueDeserializationException {
		if (elem instanceof JsonPrimitive) {
			Object value = ((JsonPrimitive) elem).getValue();

			if (value instanceof Boolean) {
				return (boolean) value;
			}

			throw new ValueDeserializationException(value, boolean.class, "JsonPrimitive not a boolean instance");
		}

		throw new ValueDeserializationException(elem, boolean.class, "JsonElement of wrong type");
	}

	@Override
	public JsonElement serializeNumber(BigDecimal value) {
		return new JsonPrimitive(value);
	}

	@Override
	public BigDecimal deserializeNumber(JsonElement elem) throws ValueDeserializationException {
		if (elem instanceof JsonPrimitive) {
			String value = ((JsonPrimitive) elem).asString();

			try {
				return new BigDecimal(value);
			} catch (NumberFormatException e) {
				throw new ValueDeserializationException(value, BigDecimal.class, "JsonPrimitive string not a valid BigDecimal");
			}
		}

		throw new ValueDeserializationException(elem, BigDecimal.class, "JsonElement of wrong type");
	}

	@Override
	public JsonElement serializeString(String value) {
		return new JsonPrimitive(value);
	}

	@Override
	public String deserializeString(JsonElement elem) throws ValueDeserializationException {
		if (elem instanceof JsonPrimitive) {
			return ((JsonPrimitive) elem).asString();
		}

		throw new ValueDeserializationException(elem, String.class, "JsonElement of wrong type");
	}

	@Override
	public JsonElement serializeList(List<JsonElement> value) {
		JsonArray arr = new JsonArray();
		arr.addAll(value);
		return arr;
	}

	@Override
	public List<JsonElement> deserializeList(JsonElement elem) throws ValueDeserializationException {
		if (elem instanceof JsonArray) {
			return ((JsonArray) elem);
		}

		throw new ValueDeserializationException(elem, List.class, "JsonElement of wrong type");
	}

	@Override
	public JsonElement serializeMap(Map<String, JsonElement> value) {
		JsonObject obj = new JsonObject();

		for (Map.Entry<String, JsonElement> entry : value.entrySet()) {
			obj.put(entry.getKey(), entry.getValue());
		}

		return obj;
	}

	@Override
	public Map<String, JsonElement> deserializeMap(JsonElement elem) throws ValueDeserializationException {
		if (elem instanceof JsonObject) {
			return ((JsonObject) elem);
		}

		throw new ValueDeserializationException(elem, Map.class, "JsonElement of wrong type");
	}

	@Override
	public void putElement(String name, JsonElement elem, JsonObject target) {
		target.put(name, elem);
	}

	@Override
	public Optional<JsonElement> getElement(String name, JsonObject target) {
		return Optional.ofNullable(target.get(name));
	}

	@Override
	public void writeTarget(JsonObject target, OutputStream out) throws IOException {
		out.write(target.toJson(true, true).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public JsonObject readTarget(InputStream in) throws ValueDeserializationException, IOException {
		try {
			return jankson.load(in);
		} catch (SyntaxError e) {
			throw new ValueDeserializationException(null, JsonObject.class, "Syntax error deserializing JSON", e);
		}
	}

	@Override
	public JsonObject newTarget() {
		return new JsonObject();
	}
}
