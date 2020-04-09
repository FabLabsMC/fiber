package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.NodeOperations;
import me.zeroeightsix.fiber.annotation.convention.NoNamingConvention;
import me.zeroeightsix.fiber.annotation.convention.SettingNamingConvention;
import me.zeroeightsix.fiber.annotation.exception.MalformedFieldException;
import me.zeroeightsix.fiber.annotation.magic.TypeMagic;
import me.zeroeightsix.fiber.builder.ConfigAggregateBuilder;
import me.zeroeightsix.fiber.builder.ConfigNodeBuilder;
import me.zeroeightsix.fiber.builder.ConfigValueBuilder;
import me.zeroeightsix.fiber.builder.constraint.AbstractConstraintsBuilder;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.tree.ConfigNode;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotatedSettings {

    public static final AnnotatedSettings DEFAULT_SETTINGS = new AnnotatedSettings();

    private final Map<Class<? extends Annotation>, SettingAnnotationProcessor.Value<?>> valueSettingProcessors = new HashMap<>();
    private final Map<Class<? extends Annotation>, SettingAnnotationProcessor.Group<?>> groupSettingProcessors = new HashMap<>();
    private final Map<Class<? extends Annotation>, ConstraintProcessorEntry> constraintProcessors = new HashMap<>();

    {
        registerGroupProcessor(Setting.Node.class, (annotation, field, pojo, node) -> {});
        registerConstraintProcessor(Setting.Constrain.Range.class, Number.class, (annotation, annotated, pojo, constraints) -> {
            if (annotation.min() > Double.NEGATIVE_INFINITY) {
                constraints.atLeast(annotation.min());
            }
            if (annotation.max() < Double.POSITIVE_INFINITY) {
                constraints.atMost(annotation.max());
            }
        });
        registerConstraintProcessor(Setting.Constrain.BigRange.class, Number.class, (annotation, annotated, pojo, constraints) -> {
            if (!annotation.min().isEmpty()) {
                constraints.atLeast(new BigDecimal(annotation.min()));
            }
            if (!annotation.max().isEmpty()) {
                constraints.atMost(new BigDecimal(annotation.max()));
            }
        });
        registerConstraintProcessor(Setting.Constrain.MinLength.class, Object.class,
                (annotation, annotated, pojo, constraints) -> constraints.minLength(annotation.value()));
        registerConstraintProcessor(Setting.Constrain.MaxLength.class, Object.class,
                (annotation, annotated, pojo, constraints) -> constraints.maxLength(annotation.value()));
        registerConstraintProcessor(Setting.Constrain.Regex.class, CharSequence.class,
                (annotation, annotated, pojo, constraints) -> constraints.regex(annotation.value()));
    }

    /**
     * Registers a setting annotation processor, tasked with processing annotations on config fields.
     *
     * @param annotationType a class representing the type of annotation to process
     * @param processor      a processor for this annotation
     * @param <A>            the type of annotation to process
     * @return {@code this}, for chaining
     */
    public <A extends Annotation> AnnotatedSettings registerSettingProcessor(Class<A> annotationType, SettingAnnotationProcessor.Value<A> processor) {
        if (valueSettingProcessors.containsKey(annotationType)) {
            throw new IllegalStateException("Cannot register multiple setting processors for the same annotation (" + annotationType + ")");
        }
        valueSettingProcessors.put(annotationType, processor);
        return this;
    }

    /**
     * Registers a group annotation processor, tasked with processing annotations on ancestor fields (config fields annotated with {@link Setting.Node}.
     *
     * @param annotationType a class representing the type of annotation to process
     * @param processor      a processor for this annotation
     * @param <A>            the type of annotation to process
     * @return {@code this}, for chaining
     */
    public <A extends Annotation> AnnotatedSettings registerGroupProcessor(Class<A> annotationType, SettingAnnotationProcessor.Group<A> processor) {
        if (groupSettingProcessors.containsKey(annotationType)) {
            throw new IllegalStateException("Cannot register multiple node processors for the same annotation (" + annotationType + ")");
        }
        groupSettingProcessors.put(annotationType, processor);
        return this;
    }

    /**
     * Registers a constraint annotation processor, tasked with processing annotations on config types.
     *
     * @param annotationType a class representing the type of annotation to process
     * @param valueType      a class representing the type of values to process
     * @param processor      a processor for this annotation
     * @param <A>            the type of annotation to process
     * @param <T>            the type of values to process
     * @return {@code this}, for chaining
     */
    public <A extends Annotation, T> AnnotatedSettings registerConstraintProcessor(Class<A> annotationType, Class<T> valueType, ConstraintAnnotationProcessor<A, ? super T> processor) {
        if (constraintProcessors.containsKey(annotationType)) {
            throw new IllegalStateException("Cannot register multiple processors for the same annotation (" + annotationType + ")");
        }
        constraintProcessors.put(annotationType, new ConstraintProcessorEntry(processor, valueType));
        return this;
    }

    public ConfigNode asNode(Object pojo) throws FiberException {
        ConfigNodeBuilder builder = new ConfigNodeBuilder();
        applyToNode(builder, pojo);
        return builder.build();
    }

    public <P> void applyToNode(ConfigNodeBuilder mergeTo, P pojo) throws FiberException {
        @SuppressWarnings("unchecked")
        Class<P> pojoClass = (Class<P>) pojo.getClass();

        boolean onlyAnnotated;
        SettingNamingConvention convention;

        if (pojoClass.isAnnotationPresent(Settings.class)) {
            Settings settingsAnnotation = pojoClass.getAnnotation(Settings.class);
            onlyAnnotated = settingsAnnotation.onlyAnnotated();
            convention = createConvention(settingsAnnotation.namingConvention());
        } else { // Assume defaults
            onlyAnnotated = false;
            convention = new NoNamingConvention();
        }

        NodeOperations.mergeTo(constructNode(pojoClass, pojo, onlyAnnotated, convention), mergeTo);
    }

    private <P> ConfigNodeBuilder constructNode(Class<P> pojoClass, P pojo, boolean onlyAnnotated, SettingNamingConvention convention) throws FiberException {
        ConfigNodeBuilder node = new ConfigNodeBuilder();

        List<Member> defaultEmpty = new ArrayList<>();
        Map<String, List<Member>> listenerMap = findListeners(pojoClass);

        for (Field field : pojoClass.getDeclaredFields()) {
            if (field.isSynthetic() || !isIncluded(field, onlyAnnotated)) continue;
            checkViolation(field);
            String name = findName(field, convention);
            if (field.isAnnotationPresent(Setting.Node.class)) {
                fieldToNode(pojo, node, field, name);
            } else {
                fieldToItem(node, field, pojo, name, listenerMap.getOrDefault(name, defaultEmpty));
            }
        }

        return node;
    }

    private Map<String, List<Member>> findListeners(Class<?> pojoClass) {
        return Stream.concat(Arrays.stream(pojoClass.getDeclaredFields()), Arrays.stream(pojoClass.getDeclaredMethods()))
                .filter(accessibleObject -> accessibleObject.isAnnotationPresent(Listener.class))
                .collect(Collectors.groupingBy(accessibleObject -> ((AccessibleObject) accessibleObject).getAnnotation(Listener.class).value()));
    }

    private boolean isIncluded(Field field, boolean onlyAnnotated) {
        if (isIgnored(field)) return false;
        return !onlyAnnotated || field.isAnnotationPresent(Setting.class);
    }

    private boolean isIgnored(Field field) {
        return getSettingAnnotation(field).map(Setting::ignore).orElse(false) || Modifier.isTransient(field.getModifiers());
    }

    private void checkViolation(Field field) throws FiberException {
        if (Modifier.isFinal(field.getModifiers())) throw new FiberException("Field '" + field.getName() + "' can not be final");
    }

    private Optional<Setting> getSettingAnnotation(Field field) {
        return field.isAnnotationPresent(Setting.class) ? Optional.of(field.getAnnotation(Setting.class)) : Optional.empty();
    }

    private <P> void fieldToNode(P pojo, ConfigNodeBuilder node, Field field, String name) throws FiberException {
        ConfigNodeBuilder sub = node.fork(name);
        try {
            field.setAccessible(true);
            applyToNode(sub, field.get(pojo));
            applyAnnotationProcessors(pojo, field, sub, this.groupSettingProcessors);
            sub.build();
        } catch (IllegalAccessException e) {
            throw new FiberException("Couldn't fork and apply sub-node", e);
        }
    }

    private <T> void fieldToItem(ConfigNodeBuilder node, Field field, Object pojo, String name, List<Member> listeners) throws FiberException {
        Class<T> type = getSettingTypeFromField(field);

        ConfigValueBuilder<T> builder = createConfigValueBuilder(node, name, type, field, pojo)
                .withComment(findComment(field))
                .withDefaultValue(findDefaultValue(field, pojo))
                .withFinality(getSettingAnnotation(field).map(Setting::constant).orElse(false));

        constrain(builder.beginConstraints(), field.getAnnotatedType(), pojo).finishConstraints();

        for (Member listener : listeners) {
            BiConsumer<T, T> consumer = constructListener(listener, pojo, type);
            if (consumer == null) continue;
            builder.withListener(consumer);
        }

        builder.withListener((t, newValue) -> {
            try {
                field.setAccessible(true);
                field.set(pojo, newValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        applyAnnotationProcessors(pojo, field, builder, this.valueSettingProcessors);

        builder.build();
    }

    private <C> void applyAnnotationProcessors(Object pojo, Field field, C sub, Map<Class<? extends Annotation>, ? extends SettingAnnotationProcessor<?, C>> settingProcessors) {
        for (Annotation annotation : field.getAnnotations()) {
            @SuppressWarnings("unchecked") SettingAnnotationProcessor<Annotation, C> processor = (SettingAnnotationProcessor<Annotation, C>) settingProcessors.get(annotation.annotationType());
            if (processor != null) {
                processor.apply(annotation, field, pojo, sub);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Nonnull
    private <T, E> ConfigValueBuilder<T> createConfigValueBuilder(ConfigNodeBuilder parent, String name, Class<T> type, Field field, Object pojo) {
        AnnotatedType annotatedType = field.getAnnotatedType();
        if (ConfigAggregateBuilder.isAggregate(type)) {
            if (Collection.class.isAssignableFrom(type)) {
                if (annotatedType instanceof AnnotatedParameterizedType) {
                    AnnotatedType[] typeArgs = ((AnnotatedParameterizedType) annotatedType).getAnnotatedActualTypeArguments();
                    if (typeArgs.length == 1) { // assume that the only type parameter is the Collection type parameter
                        AnnotatedType typeArg = typeArgs[0];
                        Class<E> componentType = (Class<E>) TypeMagic.classForType(typeArg.getType());
                        if (componentType != null) {
                            // coerce to a collection class and configure as such
                            ConfigAggregateBuilder<T, E> aggregate = ConfigAggregateBuilder.create(parent, name, (Class) type, componentType);
                            // element constraints are on the type argument (eg. List<@Regex String>), so we setup constraints from it
                            constrain(aggregate.beginConstraints().component(), typeArg, pojo).finishComponent().finishConstraints();
                            return aggregate;
                        }
                    }
                    return ConfigAggregateBuilder.create(parent, name, (Class) type, null);
                }
            } else {
                assert type.isArray();
                if (annotatedType instanceof AnnotatedArrayType) {
                    // coerce to an array class
                    Class<E[]> arrayType = (Class<E[]>) type;
                    ConfigAggregateBuilder<T, E> aggregate = (ConfigAggregateBuilder<T, E>) ConfigAggregateBuilder.create(parent, name, arrayType);
                    // take the component constraint information from the special annotated type
                    constrain(aggregate.beginConstraints().component(), ((AnnotatedArrayType) annotatedType).getAnnotatedGenericComponentType(), pojo).finishComponent().finishConstraints();
                    return aggregate;
                }
            }
        }
        return new ConfigValueBuilder<>(parent, name, type);
    }

    @SuppressWarnings("unchecked")
    private <T, B extends AbstractConstraintsBuilder<?, T, ?>> B constrain(B constraints, AnnotatedElement annotated, Object pojo) {
        for (Annotation annotation : annotated.getAnnotations()) {
            ConstraintProcessorEntry entry = this.constraintProcessors.get(annotation.annotationType());
            if (entry != null) {
                if (entry.acceptedType.isAssignableFrom(constraints.getType())) {
                    entry.processor.apply(annotation, annotated, pojo, constraints);
                } else {
                    throw new RuntimeFiberException(annotation + " does not support " +
                            (annotated instanceof AnnotatedType ? TypeMagic.classForType(((AnnotatedType) annotated).getType()) :
                                    annotated instanceof Field ? ((Field) annotated).getType().getName() : annotated) +
                            ". Should be assignable to " + entry.acceptedType.getName() + ".");
                }
            }
        }
        return constraints;
    }

    @SuppressWarnings("unchecked")
    private <T> T findDefaultValue(Field field, Object pojo) throws FiberException {
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

    private <T, P, A> BiConsumer<T,T> constructListener(Member listener, P pojo, Class<A> wantedType) throws FiberException {
        if (listener instanceof Field) {
            return constructListenerFromField((Field) listener, pojo, wantedType);
        } else if (listener instanceof Method) {
            return constructListenerFromMethod((Method) listener, pojo, wantedType);
        } else {
            throw new FiberException("Cannot create listener from " + listener + ": must be a field or method");
        }
    }

    private <T, P, A> BiConsumer<T,T> constructListenerFromMethod(Method method, P pojo, Class<A> wantedType) throws FiberException {
        int i = checkListenerMethod(method, wantedType);
        method.setAccessible(true);
        final boolean staticMethod = Modifier.isStatic(method.getModifiers());
        switch (i) {
            case 1:
                return (oldValue, newValue) -> {
                    try {
                        method.invoke(staticMethod ? null : pojo, newValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                };
            case 2:
                return (oldValue, newValue) -> {
                    try {
                        method.invoke(staticMethod ? null : pojo, oldValue, newValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                };
            default:
                throw new FiberException("Listener failed due to an invalid number of arguments.");
        }
    }

    private <A> int checkListenerMethod(Method method, Class<A> wantedType) throws FiberException {
        if (!method.getReturnType().equals(void.class)) throw new FiberException("Listener method must return void");
        int paramCount = method.getParameterCount();
        if ((paramCount != 1 && paramCount != 2) || !method.getParameterTypes()[0].equals(wantedType)) throw new FiberException("Listener method must have exactly two parameters of type that it listens for");
        return paramCount;
    }

    private <T, P, A> BiConsumer<T,T> constructListenerFromField(Field field, P pojo, Class<A> wantedType) throws FiberException {
        checkListenerField(field, wantedType);

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

    private <A> void checkListenerField(Field field, Class<A> wantedType) throws MalformedFieldException {
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

    private <T> Class<T> getSettingTypeFromField(Field field) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) field.getType();
        return wrapPrimitive(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrapPrimitive(Class<T> type) {
        if (type.equals(boolean.class)) return (Class<T>) Boolean.class;
        if (type.equals(byte.class)) return (Class<T>) Byte.class;
        if (type.equals(char.class)) return (Class<T>) Character.class;
        if (type.equals(short.class)) return (Class<T>) Short.class;
        if (type.equals(int.class)) return (Class<T>) Integer.class;
        if (type.equals(double.class)) return (Class<T>) Double.class;
        if (type.equals(float.class)) return (Class<T>) Float.class;
        if (type.equals(long.class)) return (Class<T>) Long.class;
        return type;
    }

    private String findComment(Field field) {
        return getSettingAnnotation(field).map(Setting::comment).filter(s -> !s.isEmpty()).orElse(null);
    }

    private String findName(Field field, SettingNamingConvention convention) {
        return Optional.ofNullable(
                field.isAnnotationPresent(Setting.Node.class) ?
                        field.getAnnotation(Setting.Node.class).name() :
                        getSettingAnnotation(field).map(Setting::name).orElse(null))
                .filter(s -> !s.isEmpty())
                .orElse(convention.name(field.getName()));
    }

    private SettingNamingConvention createConvention(Class<? extends SettingNamingConvention> namingConvention) throws FiberException {
        try {
            return namingConvention.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FiberException("Could not initialise naming convention", e);
        }
    }

    private static class ConstraintProcessorEntry {
        @SuppressWarnings("rawtypes")
        private final ConstraintAnnotationProcessor processor;
        private final Class<?> acceptedType;

        ConstraintProcessorEntry(ConstraintAnnotationProcessor<?, ?> processor, Class<?> acceptedType) {
            this.processor = processor;
            this.acceptedType = acceptedType;
        }
    }
}
