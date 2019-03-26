package me.zeroeightsix.fiber.pojo;

import com.google.common.primitives.Primitives;
import me.zeroeightsix.fiber.ConfigOperations;
import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.ir.ConfigNode;
import me.zeroeightsix.fiber.pojo.conventions.NoNamingConvention;
import me.zeroeightsix.fiber.pojo.conventions.SettingNamingConvention;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
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

        List<ConfigValueBuilder> builderList = buildFieldNodes(pojo, namingConvention, node);
        builderList.forEach(ConfigValueBuilder::build);
        return node;
    }

    private static List<ConfigValueBuilder> buildFieldNodes(Object pojo, SettingNamingConvention convention, ConfigNode node) {
        return Arrays.stream(pojo.getClass().getDeclaredFields()).map(field -> {
            // TODO: Process field annotations
            String name = field.getName();
            String conventionName = convention.name(name);
            name = (conventionName == null || conventionName.isEmpty()) ? name : conventionName;

            Class type = field.getType();
            if (type.isPrimitive()) {
                type = Primitives.wrap(type); // We're dealing with boxed primitives
            }

            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);

            Object value = null;
            try {
                value = field.get(pojo).getClass();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            field.setAccessible(isAccessible);

            return node.builder(type)
                    .name(name)
                    .defaultValue(value);
        }).collect(Collectors.toList());
    }

    private static SettingNamingConvention createNamingConvention(Class<? extends SettingNamingConvention> namingConvention) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return namingConvention.getDeclaredConstructor().newInstance();
    }

}
