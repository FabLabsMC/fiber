package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.builder.ConfigValueBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@FunctionalInterface
public interface SettingAnnotationProcessor<A extends Annotation, C> {
    void apply(A annotation, Field field, Object pojo, C setting);

    interface Value<A extends Annotation> extends SettingAnnotationProcessor<A, ConfigValueBuilder<?, ?>> {
        @Override
        void apply(A annotation, Field field, Object pojo, ConfigValueBuilder<?, ?> builder);
    }

    interface Node<A extends Annotation> extends SettingAnnotationProcessor<A, me.zeroeightsix.fiber.tree.Node> {
        @Override
        void apply(A annotation, Field field, Object pojo, me.zeroeightsix.fiber.tree.Node node);
    }
}
