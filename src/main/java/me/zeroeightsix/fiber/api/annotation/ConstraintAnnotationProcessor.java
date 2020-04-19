package me.zeroeightsix.fiber.api.annotation;

import me.zeroeightsix.fiber.api.schema.DecimalConfigType;
import me.zeroeightsix.fiber.api.schema.ListConfigType;
import me.zeroeightsix.fiber.api.schema.StringConfigType;
import me.zeroeightsix.fiber.api.tree.ConfigTree;

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
 * @see AnnotatedSettings#registerConstraintProcessor(Class, ConstraintAnnotationProcessor)
 */
public interface ConstraintAnnotationProcessor<A extends Annotation> {
    /**
     * Called for every type use site (field or generics) that has an annotation of type {@code A}.
     *
     * @param <T> the type of values processed
     * @param annotation the annotation present on the {@code annotated} element
     * @param annotated an annotated type use site declared in {@code pojo}'s class
     * @param pojo the <em>plain old java object</em> being processed
     * @param baseType the type being constrained
     * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
     */
    default <T> DecimalConfigType<T> processDecimal(A annotation, AnnotatedElement annotated, Object pojo, DecimalConfigType<T> baseType) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType().getSimpleName() + " for type " + baseType);
    }

    /**
     * Called for every type use site (field or generics) that has an annotation of type {@code A}.
     *
     * @param <T> the type of values processed
     * @param annotation the annotation present on the {@code annotated} element
     * @param annotated an annotated type use site declared in {@code pojo}'s class
     * @param pojo the <em>plain old java object</em> being processed
     * @param baseType the type being constrained
     * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
     */
    default <T> StringConfigType<T> processString(A annotation, AnnotatedElement annotated, Object pojo, StringConfigType<T> baseType) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
    }

    /**
     * Called for every type use site (field or generics) that has an annotation of type {@code A}.
     *
     * @param annotation the annotation present on the {@code annotated} element
     * @param annotated an annotated type use site declared in {@code pojo}'s class
     * @param pojo the <em>plain old java object</em> being processed
     * @param baseType the type being constrained
     * @param <T> the type of values processed
     * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
     */
    default <T> ListConfigType<T> processList(A annotation, AnnotatedElement annotated, Object pojo, ListConfigType<T> baseType) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
    }
}
