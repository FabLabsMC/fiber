package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.NodeOperations;
import me.zeroeightsix.fiber.annotation.convention.NoNamingConvention;
import me.zeroeightsix.fiber.annotation.convention.SettingNamingConvention;
import me.zeroeightsix.fiber.annotation.exception.MalformedFieldException;
import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.builder.constraint.ConstraintsBuilder;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.tree.ConfigNode;
import me.zeroeightsix.fiber.tree.Node;
import me.zeroeightsix.fiber.tree.TreeItem;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotatedSettings {

    public static <P> void applyToNode(Node mergeTo, P pojo) throws FiberException {
        @SuppressWarnings("unchecked")
        Class<P> pojoClass = (Class<P>) pojo.getClass();

        boolean noForceFinals;
        boolean onlyAnnotated;
        SettingNamingConvention convention;

        if (pojoClass.isAnnotationPresent(Settings.class)) {
            Settings settingsAnnotation = pojoClass.getAnnotation(Settings.class);
            noForceFinals = settingsAnnotation.noForceFinals();
            onlyAnnotated = settingsAnnotation.onlyAnnotated();
            convention = createConvention(settingsAnnotation.namingConvention());
        } else { // Assume defaults
            noForceFinals = onlyAnnotated = false;
            convention = new NoNamingConvention();
        }

        NodeOperations.mergeTo(constructNode(pojoClass, pojo, noForceFinals, onlyAnnotated, convention), mergeTo);
    }

    private static <P> Node constructNode(Class<P> pojoClass, P pojo, boolean noForceFinals, boolean onlyAnnotated, SettingNamingConvention convention) throws FiberException {
        ConfigNode node = new ConfigNode();

        List<Member> defaultEmpty = new ArrayList<>();
        Map<String, List<Member>> listenerMap = findListeners(pojoClass);

        for (Field field : pojoClass.getDeclaredFields()) {
            if (field.isSynthetic() || !isIncluded(field, onlyAnnotated)) continue;
            checkViolation(field, noForceFinals);
            String name = findName(field, convention);
            if (field.isAnnotationPresent(Setting.Node.class)) {
                Node sub = node.fork(name);
                try {
                    AnnotatedSettings.applyToNode(sub, field.get(pojo));
                } catch (IllegalAccessException e) {
                    throw new FiberException("Couldn't fork and apply sub-node", e);
                }
            } else {
                node.add(fieldToItem(field, pojo, name, listenerMap.getOrDefault(name, defaultEmpty)));
            }
        }

        return node;
    }

    private static Map<String, List<Member>> findListeners(Class<?> pojoClass) {
        return Stream.concat(Arrays.stream(pojoClass.getDeclaredFields()), Arrays.stream(pojoClass.getDeclaredMethods()))
                .filter(accessibleObject -> accessibleObject.isAnnotationPresent(Listener.class))
                .collect(Collectors.groupingBy(accessibleObject -> ((AccessibleObject) accessibleObject).getAnnotation(Listener.class).value()));
    }

    private static boolean isIncluded(Field field, boolean onlyAnnotated) {
        if (isIgnored(field)) return false;
        return !onlyAnnotated || field.isAnnotationPresent(Setting.class);
    }

    private static boolean isIgnored(Field field) {
        return getSettingAnnotation(field).map(Setting::ignore).orElse(false) || Modifier.isTransient(field.getModifiers());
    }

    private static void checkViolation(Field field, boolean noForceFinals) throws FiberException {
        if (!noForceFinals && !Modifier.isFinal(field.getModifiers()) && !getSettingAnnotation(field).map(Setting::noForceFinal).orElse(false)) throw new FiberException("Field '" + field.getName() + "' must be final");
    }

    private static Optional<Setting> getSettingAnnotation(Field field) {
        return field.isAnnotationPresent(Setting.class) ? Optional.of(field.getAnnotation(Setting.class)) : Optional.empty();
    }

    private static <T, P> TreeItem fieldToItem(Field field, P pojo, String name, List<Member> listeners) throws FiberException {
        Class<T> type = getSettingTypeFromField(field);

        ConfigValueBuilder<T> builder = new ConfigValueBuilder<>(type)
                .withName(name)
                .withComment(findComment(field))
                .withDefaultValue(findDefaultValue(field, pojo))
                .setFinal(getSettingAnnotation(field).map(Setting::constant).orElse(false));

        constrain(builder.constraints(), field);

        for (Member listener : listeners) {
            BiConsumer<T, T> consumer = constructListener(listener, pojo, type);
            if (consumer == null) continue;
            builder.withListener(consumer);
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static <T> void constrain(ConstraintsBuilder<T> constraints, Field field) {
        if (field.isAnnotationPresent(Setting.Constrain.Min.class)) constraints.minNumerical((T) Double.valueOf(field.getAnnotation(Setting.Constrain.Min.class).value()));
        if (field.isAnnotationPresent(Setting.Constrain.Max.class)) constraints.maxNumerical((T) Double.valueOf(field.getAnnotation(Setting.Constrain.Max.class).value()));
        constraints.finish();
    }

    @SuppressWarnings("unchecked")
    private static <T, P> T findDefaultValue(Field field, P pojo) throws FiberException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        T value;
        try {
            value = (T) field.get(pojo);
        } catch (IllegalAccessException e) {
            throw new FiberException("Couldn't get value for field '" + field.getName() + "'", e);
        }
        field.setAccessible(accessible);
        return value;
    }

    private static <T, P, A> BiConsumer<T,T> constructListener(Member listener, P pojo, Class<A> wantedType) throws FiberException {
        if (listener instanceof Field) {
            return constructListenerFromField((Field) listener, pojo, wantedType);
        } else if (listener instanceof Method) {
            return constructListenerFromMethod((Method) listener, pojo, wantedType);
        } else {
            throw new FiberException("Cannot create listener from " + listener + ": must be a field or method");
        }
    }

    private static <T, P, A> BiConsumer<T,T> constructListenerFromMethod(Method method, P pojo, Class<A> wantedType) throws FiberException {
        checkListenerMethod(method, wantedType);
        method.setAccessible(true);
        final boolean staticMethod = Modifier.isStatic(method.getModifiers());
        return (oldValue, newValue) -> {
            try {
                method.invoke(staticMethod ? null : pojo, oldValue, newValue);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        };
    }

    private static <A> void checkListenerMethod(Method method, Class<A> wantedType) throws FiberException {
        if (!method.getReturnType().equals(void.class)) throw new FiberException("Listener method must return void");
        if (method.getParameterCount() != 2 || !method.getParameterTypes()[0].equals(wantedType)) throw new FiberException("Listener method must have exactly two parameters of type that it listens for");
    }

    private static <T, P, A> BiConsumer<T,T> constructListenerFromField(Field field, P pojo, Class<A> wantedType) throws FiberException {
        checkListenerField(field, wantedType);

        @SuppressWarnings("unchecked")
        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        BiConsumer<T, T> consumer;
        try {
            @SuppressWarnings({ "unchecked", "unused" })
            BiConsumer<T, T> suppress = consumer = (BiConsumer<T, T>) field.get(pojo);
        } catch (IllegalAccessException e) {
            throw new FiberException("Couldn't construct listener", e);
        }
        field.setAccessible(isAccessible);

        return consumer;
    }

    private static <A> void checkListenerField(Field field, Class<A> wantedType) throws MalformedFieldException {
        if (!field.getType().equals(BiConsumer.class)) {
            throw new MalformedFieldException("Field " + field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " must be a BiConsumer");
        }

        ParameterizedType genericTypes = (ParameterizedType) field.getGenericType();
        if (genericTypes.getActualTypeArguments().length != 2) {
            throw new MalformedFieldException("Listener " + field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " must have 2 generic types");
        } else if (genericTypes.getActualTypeArguments()[0] != genericTypes.getActualTypeArguments()[1]) {
            throw new MalformedFieldException("Listener " + field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " must have 2 identical generic types");
        } else if (!genericTypes.getActualTypeArguments()[0].equals(wantedType)) {
            throw new MalformedFieldException("Listener " + field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " must have the same generic type as the field it's listening for");
        }
    }

    private static <T> Class<T> getSettingTypeFromField(Field field) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) field.getType();
        if (type.isPrimitive()) return wrapPrimitive(type);
        return type;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> wrapPrimitive(Class<T> type) {
        if (type.equals(boolean.class)) return (Class<T>) Boolean.class;
        if (type.equals(byte.class)) return (Class<T>) Byte.class;
        if (type.equals(char.class)) return (Class<T>) Character.class;
        if (type.equals(short.class)) return (Class<T>) Short.class;
        if (type.equals(int.class)) return (Class<T>) Integer.class;
        if (type.equals(double.class)) return (Class<T>) Double.class;
        if (type.equals(float.class)) return (Class<T>) Float.class;
        if (type.equals(long.class)) return (Class<T>) Long.class;
        return null;
    }

    private static String findComment(Field field) {
        return getSettingAnnotation(field).map(Setting::comment).filter(s -> !s.isEmpty()).orElse(null);
    }

    private static String findName(Field field, SettingNamingConvention convention) {
        return Optional.ofNullable(
                field.isAnnotationPresent(Setting.Node.class) ?
                        field.getAnnotation(Setting.Node.class).name() :
                        getSettingAnnotation(field).map(Setting::name).orElse(null))
                .filter(s -> !s.isEmpty())
                .orElse(convention.name(field.getName()));
    }

    private static SettingNamingConvention createConvention(Class<? extends SettingNamingConvention> namingConvention) throws FiberException {
        try {
            return namingConvention.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FiberException("Could not initialise naming convention", e);
        }
    }

}
