package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
import me.zeroeightsix.fiber.builder.ConfigValueBuilder;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;

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
public interface SettingAnnotationProcessor<A extends Annotation, C> {
    /**
     * Called for every field that has an annotation of type {@code A}.
     *
     * @param annotation the annotation present on the {@code field}
     * @param field a field declared in {@code pojo}'s class
     * @param pojo the <em>plain old java object</em> being processed
     * @param setting the builder being configured
     * @see AnnotatedSettings#applyToNode(ConfigNodeBuilder, Object)
     */
    void apply(A annotation, Field field, Object pojo, C setting);

    /**
     * An annotation processor for config fields holding values.
     *
     * <p> In effect, this is called for every field in a config POJO
     * class that is not annotated with {@link Setting.Node} or {@link Setting#ignore()}.
     *
     * <p> Annotations made handled by these processors should
     * specifically target {@link ElementType#FIELD}.
     *
     * @param <A> the type of annotations processed
     * @see AnnotatedSettings#registerSettingProcessor(Class, Value) 
     */
    @FunctionalInterface
    interface Value<A extends Annotation> extends SettingAnnotationProcessor<A, ConfigValueBuilder<?>> {
        @Override
        void apply(A annotation, Field field, Object pojo, ConfigValueBuilder<?> builder);
    }

    /**
     * An annotation processor for config fields representing config groups.
     *
     * <p> In effect, this is called for every field in a config POJO
     * class that is annotated with {@link Setting.Node}.
     *
     * <p> Annotations made for these processors should
     * specifically target {@link ElementType#FIELD}.
     *
     * @param <A> the type of annotations processed
     * @see AnnotatedSettings#registerGroupProcessor(Class, Group)
     */
    @FunctionalInterface
    interface Group<A extends Annotation> extends SettingAnnotationProcessor<A, ConfigNodeBuilder> {
        @Override
        void apply(A annotation, Field field, Object pojo, ConfigNodeBuilder node);
    }
}
