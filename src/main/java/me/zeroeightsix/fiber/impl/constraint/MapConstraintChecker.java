package me.zeroeightsix.fiber.impl.constraint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import me.zeroeightsix.fiber.api.schema.type.MapSerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;

public class MapConstraintChecker<V> extends ConstraintChecker<Map<String, V>, MapSerializableType<V>> {
	private static final MapConstraintChecker<?> INSTANCE = new MapConstraintChecker<>();

	public static <V> MapConstraintChecker<V> instance() {
		@SuppressWarnings("unchecked") MapConstraintChecker<V> t = (MapConstraintChecker<V>) INSTANCE;
		return t;
	}

	private MapConstraintChecker() {
	}

	@Override
	public TypeCheckResult<Map<String, V>> test(MapSerializableType<V> cfg, Map<String, V> values) {
		boolean valid = true;
		int maxSize = cfg.getMaxSize();
		Map<String, V> corrected = new LinkedHashMap<>();

		for (Map.Entry<String, V> entry : values.entrySet()) {
			if (corrected.size() >= maxSize) {
				valid = false;
				break;
			}

			TypeCheckResult<String> keyTestResult = cfg.getKeyType().test(entry.getKey());
			TypeCheckResult<V> valueTestResult = cfg.getValueType().test(entry.getValue());

			if (keyTestResult.hasPassed() && valueTestResult.hasPassed()) {
				corrected.put(entry.getKey(), entry.getValue());
			} else {
				valid = false;
				Optional<String> correctedKey = keyTestResult.getCorrectedValue();
				Optional<V> correctedValue = valueTestResult.getCorrectedValue();

				if (correctedKey.isPresent() && correctedValue.isPresent()) {
					corrected.put(correctedKey.get(), correctedValue.get());
				}

				// if key or value missing, just skip the entry
			}
		}

		if (corrected.size() < cfg.getMinSize()) {
			return TypeCheckResult.unrecoverable();
		} else if (!valid) {
			return TypeCheckResult.failed(corrected);
		} else {
			return TypeCheckResult.successful(values);
		}
	}

	@Override
	public boolean comprehends(MapSerializableType<V> cfg, MapSerializableType<V> cfg2) {
		if (cfg.getMinSize() > cfg2.getMinSize()) {
			return false;
		}

		if (cfg.getMaxSize() < cfg2.getMaxSize()) {
			return false;
		}

		return cfg.getValueType().isAssignableFrom(cfg2.getValueType());
	}
}
