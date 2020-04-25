package me.zeroeightsix.fiber.api.annotation;

import me.zeroeightsix.fiber.api.annotation.processor.BranchAnnotationProcessor;
import me.zeroeightsix.fiber.api.annotation.processor.ConstraintAnnotationProcessor;
import me.zeroeightsix.fiber.api.annotation.processor.LeafAnnotationProcessor;
import me.zeroeightsix.fiber.api.annotation.processor.ParameterizedTypeProcessor;
import me.zeroeightsix.fiber.api.exception.FiberException;
import me.zeroeightsix.fiber.api.schema.type.derived.DerivedType;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigTree;
import me.zeroeightsix.fiber.impl.annotation.AnnotatedSettingsImpl;

import java.lang.annotation.Annotation;

public interface AnnotatedSettings {
    AnnotatedSettings DEFAULT_SETTINGS = create();

    static AnnotatedSettings create() {
        return new AnnotatedSettingsImpl();
    }

    /* tree building methods */

    ConfigBranch makeTree(Object pojo) throws FiberException;

    <P> void applyToNode(ConfigTree mergeTo, P pojo) throws FiberException;

    /* configuration methods */

    <T> AnnotatedSettings registerTypeMapping(Class<? super T> clazz, DerivedType<T, ?, ?> type);

    <T> AnnotatedSettings registerTypeMapping(Class<? super T> clazz, ParameterizedTypeProcessor<T> processor);

    /**
     * Registers a setting annotation processor, tasked with processing annotations on config fields.
     *
     * @param <A>            the type of annotation to process
     * @param annotationType a class representing the type of annotation to process
     * @param processor      a processor for this annotation
     * @return {@code this}, for chaining
     */
    <A extends Annotation> AnnotatedSettings registerSettingProcessor(Class<A> annotationType, LeafAnnotationProcessor<A> processor);

    /**
     * Registers a group annotation processor, tasked with processing annotations on ancestor fields (config fields annotated with {@link Setting.Group}.
     *
     * @param <A>            the type of annotation to process
     * @param annotationType a class representing the type of annotation to process
     * @param processor      a processor for this annotation
     * @return {@code this}, for chaining
     */
    <A extends Annotation> AnnotatedSettings registerGroupProcessor(Class<A> annotationType, BranchAnnotationProcessor<A> processor);

    /**
     * Registers a constraint annotation processor, tasked with processing annotations on config types.
     *
     * @param annotationType a class representing the type of annotation to process
     * @param processor      a processor for this annotation
     * @param <A>            the type of annotation to process
     * @return {@code this}, for chaining
     */
    <A extends Annotation> AnnotatedSettings registerConstraintProcessor(Class<A> annotationType, ConstraintAnnotationProcessor<A> processor);

}
