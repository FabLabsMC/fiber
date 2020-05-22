package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigLeafBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;

/**
 * An annotation processor for config fields holding values.
 *
 * <p>Annotations made to be handled by these processors should
 * specifically target {@link ElementType#FIELD}.
 *
 * @param <A> the type of annotations processed
 * @see AnnotatedSettings.Builder#registerSettingProcessor(Class, LeafAnnotationProcessor)
 */
@FunctionalInterface
public interface LeafAnnotationProcessor<A extends Annotation> extends ConfigAnnotationProcessor<A, Field, ConfigLeafBuilder<?, ?>> {
	/**
	 * Called for every field that has an annotation of type {@code A}
	 * and is not annotated with {@link Setting.Group @Setting.Group} or {@link Setting#ignore()}.
	 *
	 * @param annotation the annotation present on the {@code field}
	 * @param field      a field declared in {@code pojo}'s class
	 * @param pojo       the <em>plain old java object</em> being processed
	 * @param builder    the builder being configured
	 * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
	 */
	@Override
	void apply(A annotation, Field field, Object pojo, ConfigLeafBuilder<?, ?> builder);
}
