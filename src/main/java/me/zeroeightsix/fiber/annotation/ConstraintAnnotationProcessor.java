package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.builder.constraint.AbstractConstraintsBuilder;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;

/**
 * An annotation processor for constraints on config types.
 *
 * <p> Annotations made for this type of processor should
 * specifically target {@link ElementType#TYPE_USE}.
 *
 * @param <A> the type of annotations processed
 * @param <T> the type of values processed
 * @see AnnotatedSettings#registerConstraintProcessor(Class, Class, ConstraintAnnotationProcessor)
 */
@FunctionalInterface
public interface ConstraintAnnotationProcessor<A extends Annotation, T> {
    /**
     * Called for every type use site (field or generics) that has an annotation of type {@code A}.
     *
     * @param annotation the annotation present on the {@code annotated} element
     * @param annotated an annotated type use site declared in {@code pojo}'s class
     * @param pojo the <em>plain old java object</em> being processed
     * @param constraints the constraints builder being configured
     * @see AnnotatedSettings#applyToNode(ConfigTreeBuilder, Object)
     */
    void apply(A annotation, AnnotatedElement annotated, Object pojo, AbstractConstraintsBuilder<?, ?, T> constraints);
}
