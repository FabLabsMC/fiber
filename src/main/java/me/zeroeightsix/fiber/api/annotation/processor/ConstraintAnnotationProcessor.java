package me.zeroeightsix.fiber.api.annotation.processor;

import me.zeroeightsix.fiber.api.annotation.AnnotatedSettings;
import me.zeroeightsix.fiber.api.schema.type.derived.*;
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
     * @param <T>        the type of values processed
     * @param baseType   the type being constrained
     * @param annotation the annotation present on the {@code annotated} element
     * @param annotated  an annotated type use site declared in {@code pojo}'s class
     * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
     */
    default <T> NumberDerivedType<T> processDecimal(NumberDerivedType<T> baseType, A annotation, AnnotatedElement annotated) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType().getSimpleName() + " for type " + baseType);
    }

    /**
     * Called for every type use site (field or generics) that has an annotation of type {@code A}.
     *
     * @param <T>        the type of values processed
     * @param baseType   the type being constrained
     * @param annotation the annotation present on the {@code annotated} element
     * @param annotated  an annotated type use site declared in {@code pojo}'s class
     * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
     */
    default <T> StringDerivedType<T> processString(StringDerivedType<T> baseType, A annotation, AnnotatedElement annotated) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
    }

    /**
     * Called for every type use site (field or generics) that has an annotation of type {@code A}.
     *
     * @param <T>        the type of values processed
     * @param baseType   the type being constrained
     * @param annotation the annotation present on the {@code annotated} element
     * @param annotated  an annotated type use site declared in {@code pojo}'s class
     * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
     */
    default <T, E> ListDerivedType<T, E> processList(ListDerivedType<T, E> baseType, A annotation, AnnotatedElement annotated) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
    }

    default <T> EnumDerivedType<T> processEnum(EnumDerivedType<T> baseType, A annotation, AnnotatedElement annotated) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
    }

    default <R, E> RecordDerivedType<R, E> processRecord(RecordDerivedType<R, E> baseType, A annotation, AnnotatedElement annotated) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
    }

    default <T> BooleanDerivedType<T> processBoolean(BooleanDerivedType<T> baseType, A annotation, AnnotatedElement annotated) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
    }

    default <R, V> MapDerivedType<R,V> processMap(MapDerivedType<R, V> baseType, A annotation, AnnotatedElement annotated) {
        throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
    }
}
