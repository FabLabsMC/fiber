package me.zeroeightsix.fiber.impl.constraint;

import me.zeroeightsix.fiber.api.schema.type.MapSerializableType;
import me.zeroeightsix.fiber.api.schema.type.TypeCheckResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MapTypeChecker<V> extends Constraint<Map<String, V>, MapSerializableType<V>> {

    public MapTypeChecker(MapSerializableType<V> cfg) {
        super(cfg);
    }

    @Override
    public TypeCheckResult<Map<String, V>> test(Map<String, V> values) {
        boolean valid = true;
        int maxSize = this.cfg.getMaxSize();
        Map<String, V> corrected = new LinkedHashMap<>();

        for (Map.Entry<String, V> entry : values.entrySet()) {
            if (corrected.size() >= maxSize) {
                valid = false;
                break;
            }
            TypeCheckResult<String> keyTestResult = this.cfg.getKeyType().test(entry.getKey());
            TypeCheckResult<V> valueTestResult = this.cfg.getValueType().test(entry.getValue());
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
        if (corrected.size() < this.cfg.getMinSize()) {
            return TypeCheckResult.unrecoverable();
        } else if (!valid) {
            return TypeCheckResult.failed(corrected);
        } else {
            return TypeCheckResult.successful(values);
        }
    }

    @Override
    public boolean comprehends(Constraint<?, ?> constraint) {
        if (!(constraint instanceof MapTypeChecker)) return false;
        MapTypeChecker<?> that = (MapTypeChecker<?>) constraint;
        if (this.cfg.getMinSize() > that.cfg.getMinSize()) {
            return false;
        }
        if (this.cfg.getMaxSize() < that.cfg.getMaxSize()) {
            return false;
        }
        return this.cfg.getValueType().isAssignableFrom(that.cfg.getValueType());
    }
}
