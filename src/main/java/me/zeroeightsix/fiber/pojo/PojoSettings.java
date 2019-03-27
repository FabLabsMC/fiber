package me.zeroeightsix.fiber.pojo;

import com.google.common.primitives.Primitives;
import me.zeroeightsix.fiber.ConfigOperations;
import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.ir.ConfigNode;
import me.zeroeightsix.fiber.pojo.conventions.NoNamingConvention;
import me.zeroeightsix.fiber.pojo.conventions.SettingNamingConvention;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class PojoSettings {

    public static void applyToIR(ConfigNode mergeTo, Object pojo) throws IllegalAccessException {
        ConfigNode node = parsePojo(pojo);
        ConfigOperations.mergeTo(node, mergeTo);
    }

    private static ConfigNode parsePojo(Object pojo) throws IllegalAccessException {
        ConfigNode node = new ConfigNode(null);
        boolean forceFinals = true;
        SettingNamingConvention namingConvention = new NoNamingConvention();
        Class pojoClass = pojo.getClass();

        if (pojoClass.isAnnotationPresent(Settings.class)) {
            Settings settingsAnnotation = (Settings) pojoClass.getAnnotation(Settings.class);
            if (settingsAnnotation.noForceFinals()) forceFinals = false;

            try {
                namingConvention = createNamingConvention(settingsAnnotation.namingConvention());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new IllegalAccessException("Naming convention must have an empty constructor");
            }
        }

        List<ConfigValueBuilder> builderList = buildFieldNodes(pojo, namingConvention, node, forceFinals);
        builderList.forEach(ConfigValueBuilder::build);
        return node;
    }

    private static List<ConfigValueBuilder> buildFieldNodes(Object pojo, SettingNamingConvention convention, ConfigNode node, boolean forceFinals) {
        return Arrays.stream(pojo.getClass().getDeclaredFields()).map(field -> {
            SettingProperties properties = getProperties(field);
            if (properties.ignored) return null;

            // Get angry if not final
            if (forceFinals && !properties.noForceFinal && !Modifier.isFinal(field.getModifiers())) {
                throw new IllegalStateException("Field " + field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " must be final");
            }

            // Get type
            Class type = field.getType();
            if (type.isPrimitive()) {
                type = Primitives.wrap(type); // We're dealing with boxed primitives
            }

            // Construct builder by type
            ConfigValueBuilder builder = node.builder(type)
                    .comment(properties.comment);

            // Set final if final
            if (properties.finalValue) {
                builder.setFinal();
            }

            // Get name
            String name = field.getName();
            String conventionName = convention.name(name);
            name = (conventionName == null || conventionName.isEmpty()) ? name : conventionName;
            builder.name(name);

            // Get value
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            try {
                Object value = field.get(pojo);
                builder.defaultValue(value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(isAccessible);

            return builder;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static String getComment(Comment annotation) {
        return annotation == null ? null : annotation.value();
    }

    private static String getComment(Field field) {
        return getComment(field.getAnnotation(Comment.class));
    }

    private static SettingNamingConvention createNamingConvention(Class<? extends SettingNamingConvention> namingConvention) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return namingConvention.getDeclaredConstructor().newInstance();
    }

    private static SettingProperties getProperties(Field field) {
        String comment = getComment(field);
        boolean ignored = field.isAnnotationPresent(Setting.Ignored.class);
        boolean noForceFinal = field.isAnnotationPresent(Setting.NoForceFinal.class);
        boolean finalValue = field.isAnnotationPresent(Setting.Final.class);
        Set<Constraint> constraints = new HashSet<>();

        return new SettingProperties(comment, ignored, noForceFinal, finalValue, constraints);
    }

    private static class SettingProperties {
        final String comment;
        final boolean ignored;
        final boolean noForceFinal;
        final boolean finalValue;
        final Set<Constraint> constraintSet;

        public SettingProperties(String comment, boolean ignored, boolean noForceFinal, boolean finalValue, Set<Constraint> constraintSet) {
            this.comment = comment;
            this.ignored = ignored;
            this.noForceFinal = noForceFinal;
            this.finalValue = finalValue;
            this.constraintSet = constraintSet;
        }
    }

}
