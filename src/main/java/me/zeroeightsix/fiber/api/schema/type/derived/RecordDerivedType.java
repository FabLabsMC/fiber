package me.zeroeightsix.fiber.api.schema.type.derived;

import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.schema.type.RecordConfigType;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public final class RecordDerivedType<R, E> extends DerivedType<R, ConfigBranch, RecordConfigType> {

    @SuppressWarnings("unchecked")
    public RecordDerivedType(RecordConfigType serializedType, Class<? super R> runtimeType, Function<ConfigBranch, R> f, Function<R, ConfigBranch> f0) {
        super(serializedType, (Class<R>) runtimeType, f, f0);
    }

    @Override
    public <U> RecordDerivedType<U, E> derive(Class<? super U> runtimeType, Function<R, U> g, Function<U, R> g0) {
        return new RecordDerivedType<>(this.getSerializedType(), runtimeType, s -> g.apply(this.f.apply(s)), u -> this.f0.apply(g0.apply(u)));
    }

    @Override
    public RecordDerivedType<R, E> withType(RecordConfigType newSpec) {
        this.checkTypeNarrowing(newSpec);
        return new RecordDerivedType<>(newSpec, this.getRuntimeType(), this.f, this.f0);
    }

    @Override
    public RecordDerivedType<R, E> constrain(ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
        return processor.processRecord(this, annotation, annotated);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "(" +
                "[" +
                String.join(", ", this.getSerializedType().getFields().keySet()) +
                "]" +
                " : " + this.getRuntimeType().getSimpleName() +
                ")";
    }
}
