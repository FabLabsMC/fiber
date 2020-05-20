package io.github.fablabsmc.fablabs.impl.fiber.constraint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.RecordSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.TypeCheckResult;

public class RecordConstraintChecker extends ConstraintChecker<Map<String, Object>, RecordSerializableType> {
	private static final RecordConstraintChecker INSTANCE = new RecordConstraintChecker();

	public static RecordConstraintChecker instance() {
		return INSTANCE;
	}

	private RecordConstraintChecker() {
	}

	@Override
	public TypeCheckResult<Map<String, Object>> test(RecordSerializableType cfg, Map<String, Object> value) {
		// if value does not have enough fields -> unrecoverable
		if (!value.keySet().containsAll(cfg.getFields().keySet())) {
			return TypeCheckResult.unrecoverable();
		}

		// if value has extra fields -> failed
		boolean successful = cfg.getFields().keySet().containsAll(value.keySet());
		// keep track of a corrected value map
		Map<String, Object> corrected = new LinkedHashMap<>(value.size());

		for (Map.Entry<String, SerializableType<?>> field : cfg.getFields().entrySet()) {
			Object child = value.get(field.getKey());
			SerializableType<?> fieldType = field.getValue();
			TypeCheckResult<?> result = this.testChild(fieldType, child);
			Optional<?> correctedFieldValue = result.getCorrectedValue();

			if (!result.hasPassed()) {
				successful = false;
			}

			if (correctedFieldValue.isPresent()) {
				corrected.put(field.getKey(), correctedFieldValue.get());
			} else {
				return TypeCheckResult.unrecoverable();
			}
		}

		return successful ? TypeCheckResult.successful(value) : TypeCheckResult.failed(corrected);
	}

	@SuppressWarnings("unchecked")
	private <T> TypeCheckResult<T> testChild(SerializableType<T> type, Object value) {
		// value has already been validated, so this is always valid
		// type.test also calls type.cast inside it, so double casting serves no purpose
		return type.test((T) value);
	}

	@Override
	public boolean comprehends(RecordSerializableType cfg, RecordSerializableType cfg2) {
		for (Map.Entry<String, SerializableType<?>> entry : cfg.getFields().entrySet()) {
			SerializableType<?> other = cfg2.getFields().get(entry.getKey());

			if (!entry.getValue().equals(other)) {
				return false;
			}
		}

		return true;
	}
}
