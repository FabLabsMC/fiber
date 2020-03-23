package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.builder.constraint.AbstractConstraintsBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

@FunctionalInterface
public interface SettingConstraintProcessor<A extends Annotation, T> {
    void apply(A annotation, AnnotatedElement annotated, Object pojo, AbstractConstraintsBuilder<?, ?, T, ?> constraints);
}
