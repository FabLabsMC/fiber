package me.zeroeightsix.fiber.api.annotation.processor;

import me.zeroeightsix.fiber.api.annotation.AnnotatedSettings;
import me.zeroeightsix.fiber.api.tree.ConfigTree;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;

/**
 * An annotation processor for config fields.
 *
 * <p> Annotations made for this type of processor should
 * specifically target {@link ElementType#FIELD}.
 *
 * @param <A> the type of annotations processed
 * @param <C> the type of builders configured
 * @see AnnotatedSettings
 */
public interface ConfigAnnotationProcessor<A extends Annotation, E extends AnnotatedElement, C> {
    /**
     * Called for every field that has an annotation of type {@code A}.
     *
     * @param annotation the annotation present on the {@code field}
     * @param field a field declared in {@code pojo}'s class
     * @param pojo the <em>plain old java object</em> being processed
     * @param setting the builder being configured
     * @see AnnotatedSettings#applyToNode(ConfigTree, Object)
     */
    void apply(A annotation, E field, Object pojo, C setting);

}
