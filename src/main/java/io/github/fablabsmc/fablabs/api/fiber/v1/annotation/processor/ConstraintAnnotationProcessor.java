package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.BooleanConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.EnumConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ListConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.MapConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.NumberConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.RecordConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.StringConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;

/**
 * An annotation processor for constraints on config types.
 *
 * <p>Annotations made for this type of processor should
 * specifically target {@link ElementType#TYPE_USE}.
 *
 * @param <A> the type of annotations processed
 * @see AnnotatedSettings.Builder#registerConstraintProcessor(Class, ConstraintAnnotationProcessor)
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
	default <T> NumberConfigType<T> processDecimal(NumberConfigType<T> baseType, A annotation, AnnotatedElement annotated) {
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
	default <T> StringConfigType<T> processString(StringConfigType<T> baseType, A annotation, AnnotatedElement annotated) {
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
	default <T, E> ListConfigType<T, E> processList(ListConfigType<T, E> baseType, A annotation, AnnotatedElement annotated) {
		throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
	}

	default <T> EnumConfigType<T> processEnum(EnumConfigType<T> baseType, A annotation, AnnotatedElement annotated) {
		throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
	}

	default <R> RecordConfigType<R> processRecord(RecordConfigType<R> baseType, A annotation, AnnotatedElement annotated) {
		throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
	}

	default <T> BooleanConfigType<T> processBoolean(BooleanConfigType<T> baseType, A annotation, AnnotatedElement annotated) {
		throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
	}

	default <R, V> MapConfigType<R, V> processMap(MapConfigType<R, V> baseType, A annotation, AnnotatedElement annotated) {
		throw new UnsupportedOperationException("Invalid annotation " + annotation.annotationType() + " for type " + baseType);
	}
}
