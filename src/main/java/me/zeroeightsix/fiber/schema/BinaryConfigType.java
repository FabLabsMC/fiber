package me.zeroeightsix.fiber.schema;

import me.zeroeightsix.fiber.constraint.Constraint;
import me.zeroeightsix.fiber.constraint.ConstraintType;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public final class BinaryConfigType<T> extends ConfigType<T, Boolean> {
    public static final BinaryConfigType<Boolean> BOOLEAN = new BinaryConfigType<>(Boolean.class, Function.identity(), Function.identity(), Collections.emptyMap());

    private BinaryConfigType(Class<T> actualType, Function<T, Boolean> f0, Function<Boolean, T> f, Map<ConstraintType, Constraint<? super Boolean>> typeConstraints) {
        super(actualType, Boolean.class, f0, f, typeConstraints);
    }

    @Override
    public Kind getKind() {
        return Kind.BOOLEAN;
    }

    @Override
    public <T1> BinaryConfigType<T1> derive(Class<? super T1> actualType, Function<T1, T> f0, Function<T, T1> f) {
        @SuppressWarnings("unchecked") Class<T1> c = (Class<T1>) actualType;
        return new BinaryConfigType<>(c, v -> this.toRawType(f0.apply(v)), v -> f.apply(this.toActualType(v)), this.indexedConstraints);
    }
}
