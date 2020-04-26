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
        if (values.size() < this.cfg.getMinSize()) {
            return TypeCheckResult.unrecoverable();
        }

        boolean valid = true;
        int maxSize = this.cfg.getMaxSize();
        Map<String, V> corrected = new LinkedHashMap<>();

        int size = 0;
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
                Optional<V> correctedValue = testResult.getCorrectedValue();
                if (correctedValue.isPresent()) {
                    corrected.put(entry.getKey(), correctedValue.get());
                    size++;
                }
                // if not present, just skip it
            }
            if (size >= maxSize) {
                valid = false;
                break;
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
