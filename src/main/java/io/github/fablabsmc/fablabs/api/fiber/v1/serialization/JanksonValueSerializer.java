package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.BooleanSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.EnumSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.ListSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.MapSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.RecordSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.StringSerializableType;

/**
 * {@link ValueSerializer} for Jankson.
 */
public class JanksonValueSerializer implements ValueSerializer<JsonElement, JsonObject> {
	private final boolean minify;
	private final Jankson jankson;

	public JanksonValueSerializer(boolean minify) {
		this(minify, Jankson.builder().build());
	}

	public JanksonValueSerializer(boolean minify, Jankson jankson) {
		this.minify = minify;
		this.jankson = jankson;
	}

	@Override
	public JsonElement serializeBoolean(boolean value, BooleanSerializableType type) {
		return value ? JsonPrimitive.TRUE : JsonPrimitive.FALSE;
	}

	@Override
	public boolean deserializeBoolean(JsonElement elem, BooleanSerializableType type) throws ValueDeserializationException {
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
	public JsonElement serializeNumber(BigDecimal value, DecimalSerializableType type) {
		return new JsonPrimitive(value);
	}

	@Override
	public BigDecimal deserializeNumber(JsonElement elem, DecimalSerializableType type) throws ValueDeserializationException {
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
	public JsonElement serializeString(String value, StringSerializableType type) {
		return new JsonPrimitive(value);
	}

	@Override
	public String deserializeString(JsonElement elem, StringSerializableType type) throws ValueDeserializationException {
		if (elem instanceof JsonPrimitive) {
			return ((JsonPrimitive) elem).asString();
		}

		throw new ValueDeserializationException(elem, String.class, "JsonElement of wrong type");
	}

	@Override
	public JsonElement serializeEnum(String value, EnumSerializableType type) {
		return new JsonPrimitive(value);
	}

	@Override
	public String deserializeEnum(JsonElement elem, EnumSerializableType type) throws ValueDeserializationException {
		if (elem instanceof JsonPrimitive) {
			return ((JsonPrimitive) elem).asString();
		}

		throw new ValueDeserializationException(elem, String.class, "JsonElement of wrong type");
	}

	@Override
	public <E> JsonElement serializeList(List<E> value, ListSerializableType<E> type) {
		JsonArray arr = new JsonArray();

		for (E e : value) {
			arr.add(type.getElementType().serializeValue(e, this));
		}

		return arr;
	}

	@Override
	public <E> List<E> deserializeList(JsonElement elem, ListSerializableType<E> type) throws ValueDeserializationException {
		if (elem instanceof JsonArray) {
			JsonArray arr = ((JsonArray) elem);
			List<E> ls = new ArrayList<>(arr.size());

			for (JsonElement e : arr) {
				ls.add(type.getElementType().deserializeValue(e, this));
			}

			return ls;
		}

		throw new ValueDeserializationException(elem, List.class, "JsonElement of wrong type");
	}

	@Override
	public <V> JsonElement serializeMap(Map<String, V> value, MapSerializableType<V> type) {
		JsonObject obj = new JsonObject();

		for (Map.Entry<String, V> entry : value.entrySet()) {
			obj.put(entry.getKey(), type.getValueType().serializeValue(entry.getValue(), this));
		}

		return obj;
	}

	@Override
	public <V> Map<String, V> deserializeMap(JsonElement elem, MapSerializableType<V> type) throws ValueDeserializationException {
		if (elem instanceof JsonObject) {
			JsonObject obj = ((JsonObject) elem);
			Map<String, V> map = new LinkedHashMap<>(obj.size());

			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				map.put(entry.getKey(), type.getValueType().deserializeValue(entry.getValue(), this));
			}

			return map;
		}

		throw new ValueDeserializationException(elem, Map.class, "JsonElement of wrong type");
	}

	@Override
	public JsonElement serializeRecord(Map<String, Object> value, RecordSerializableType type) {
		JsonObject obj = new JsonObject();
		Map<String, SerializableType<?>> fields = type.getFields();

		for (Map.Entry<String, Object> entry : value.entrySet()) {
			obj.put(entry.getKey(), this.serializeRecordField(entry.getValue(), fields.get(entry.getKey())));
		}

		return obj;
	}

	@SuppressWarnings("unchecked")
	private <T> JsonElement serializeRecordField(Object value, SerializableType<T> type) {
		return type.serializeValue((T) type.getErasedPlatformType().cast(value), this);
	}

	@Override
	public Map<String, Object> deserializeRecord(JsonElement elem, RecordSerializableType type) throws ValueDeserializationException {
		if (elem instanceof JsonObject) {
			JsonObject obj = ((JsonObject) elem);
			Map<String, Object> map = new LinkedHashMap<>(obj.size());
			Map<String, SerializableType<?>> fields = type.getFields();

			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				map.put(entry.getKey(), fields.get(entry.getKey()).deserializeValue(elem, this));
			}

			return map;
		}

		throw new ValueDeserializationException(elem, Map.class, "JsonElement of wrong type");
	}

	@Override
	public JsonElement serializeTarget(JsonObject value) {
		return value;
	}

	@Override
	public JsonObject deserializeTarget(JsonElement elem) throws ValueDeserializationException {
		if (elem instanceof JsonObject) {
			return (JsonObject) elem;
		}

		throw new ValueDeserializationException(elem, JsonObject.class, "JsonElement of wrong type");
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
		out.write(target.toJson(!minify, !minify).getBytes(StandardCharsets.UTF_8));
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
