package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Pattern;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.BooleanSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.EnumSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.ListSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.MapSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.RecordSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.StringSerializableType;

/**
 * A {@link TypeSerializer} for the JSON serialization form.
 *
 * @see <a href="https://www.json.org/json-en.html">json.org</a>
 */
public class JsonTypeSerializer implements TypeSerializer<JsonObject> {
	@Override
	public void serialize(BooleanSerializableType type, JsonObject json) {
		json.put("type", new JsonPrimitive("boolean"));
	}

	@Override
	public void serialize(DecimalSerializableType type, JsonObject json) {
		json.put("type", new JsonPrimitive("number"));
		BigDecimal min = type.getMinimum();

		if (min != null) {
			json.put("min", new JsonPrimitive(min));
		}

		BigDecimal maximum = type.getMaximum();

		if (maximum != null) {
			json.put("max", new JsonPrimitive(maximum));
		}

		BigDecimal increment = type.getIncrement();

		if (increment != null) {
			json.put("increment", new JsonPrimitive(increment));
		}
	}

	@Override
	public void serialize(EnumSerializableType type, JsonObject json) {
		json.put("type", new JsonPrimitive("enum"));
		JsonArray values = new JsonArray();

		for (String value : type.getValidValues()) {
			values.add(new JsonPrimitive(value));
		}

		json.put("values", values);
	}

	@Override
	public void serialize(ListSerializableType<?> type, JsonObject json) {
		json.put("type", new JsonPrimitive("list"));
		JsonObject elementType = new JsonObject();
		type.getElementType().serialize(this, elementType);
		json.put("elementType", elementType);
		json.put("unique", new JsonPrimitive(type.hasUniqueElements()));
		json.put("minSize", new JsonPrimitive(type.getMinSize()));
		json.put("maxSize", new JsonPrimitive(type.getMaxSize()));
	}

	@Override
	public void serialize(MapSerializableType<?> type, JsonObject json) {
		json.put("type", new JsonPrimitive("map"));
		JsonObject valueType = new JsonObject();
		type.getValueType().serialize(this, valueType);
		json.put("valueType", valueType);
		json.put("minSize", new JsonPrimitive(type.getMinSize()));
		json.put("maxSize", new JsonPrimitive(type.getMaxSize()));
	}

	@Override
	public void serialize(RecordSerializableType type, JsonObject json) {
		json.put("type", new JsonPrimitive("record"));
		JsonArray fields = new JsonArray();

		for (Map.Entry<String, SerializableType<?>> entry : type.getFields().entrySet()) {
			JsonObject field = new JsonObject();
			field.put("name", new JsonPrimitive(entry.getKey()));
			JsonObject fieldType = new JsonObject();
			entry.getValue().serialize(this, fieldType);
			field.put("type", fieldType);
			fields.add(field);
		}

		json.put("fields", fields);
	}

	@Override
	public void serialize(StringSerializableType type, JsonObject json) {
		json.put("type", new JsonPrimitive("string"));
		json.put("minLength", new JsonPrimitive(type.getMinLength()));
		json.put("maxLength", new JsonPrimitive(type.getMaxLength()));
		Pattern pattern = type.getPattern();

		if (pattern != null) {
			json.put("pattern", new JsonPrimitive(pattern.toString()));
		}
	}
}
