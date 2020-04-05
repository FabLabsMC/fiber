package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
import me.zeroeightsix.fiber.builder.ConfigValueBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface SettingAnnotationProcessor<A extends Annotation, C> {
    void apply(A annotation, Field field, Object pojo, C setting);

    @FunctionalInterface
    interface Value<A extends Annotation> extends SettingAnnotationProcessor<A, ConfigValueBuilder<?>> {
        @Override
        void apply(A annotation, Field field, Object pojo, ConfigValueBuilder<?> builder);
    }

    @FunctionalInterface
    interface Node<A extends Annotation> extends SettingAnnotationProcessor<A, ConfigNodeBuilder> {
        @Override
        void apply(A annotation, Field field, Object pojo, ConfigNodeBuilder node);
    }
}
