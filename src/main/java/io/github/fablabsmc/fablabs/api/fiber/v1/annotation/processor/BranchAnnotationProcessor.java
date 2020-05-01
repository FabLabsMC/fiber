package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting.Group;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;

/**
 * An annotation processor for fields representing config groups.
 *
 * <p>In effect, this is called for every field in a config POJO
 * class that is annotated with {@link Group}.
 *
 * <p>Annotations made for these processors should
 * specifically target {@link ElementType#FIELD}.
 *
 * @param <A> the type of annotations processed
 * @see AnnotatedSettings#registerGroupProcessor(Class, BranchAnnotationProcessor)
 */
@FunctionalInterface
public interface BranchAnnotationProcessor<A extends Annotation> extends ConfigAnnotationProcessor<A, Field, ConfigTreeBuilder> {
	/**
	 * Called for every field that has an annotation of type {@code A} and is annotated with {@link Group @Group}.
	 *
	 * @param annotation the annotation present on the {@code field}
	 * @param field      a field declared in {@code pojo}'s class
	 * @param pojo       the <em>plain old java object</em> being processed
	 * @param builder    the builder being configured
	 * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
	 */
	@Override
	void apply(A annotation, Field field, Object pojo, ConfigTreeBuilder builder);
}
