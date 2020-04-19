package me.zeroeightsix.fiber.annotation;

import me.zeroeightsix.fiber.NodeOperations;
import me.zeroeightsix.fiber.annotation.convention.NoNamingConvention;
import me.zeroeightsix.fiber.annotation.convention.SettingNamingConvention;
import me.zeroeightsix.fiber.annotation.exception.MalformedFieldException;
import me.zeroeightsix.fiber.annotation.magic.TypeMagic;
import me.zeroeightsix.fiber.builder.ConfigLeafBuilder;
import me.zeroeightsix.fiber.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.exception.FiberException;
import me.zeroeightsix.fiber.exception.FiberTypeProcessingException;
import me.zeroeightsix.fiber.exception.RuntimeFiberException;
import me.zeroeightsix.fiber.schema.*;
import me.zeroeightsix.fiber.tree.ConfigBranch;
import me.zeroeightsix.fiber.tree.ConfigTree;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotatedSettings {

    public static final AnnotatedSettings DEFAULT_SETTINGS = new AnnotatedSettings();

    private final Map<Class<?>, ParameterizedTypeProcessor<?>> registeredGenericTypes = new HashMap<>();
    private final Map<Class<?>, ConfigType<?, ?>> registeredTypes = new HashMap<>();
    private final Map<Class<? extends Annotation>, SettingAnnotationProcessor.Value<?>> valueSettingProcessors = new HashMap<>();
    private final Map<Class<? extends Annotation>, SettingAnnotationProcessor.Group<?>> groupSettingProcessors = new HashMap<>();
    private final Map<Class<? extends Annotation>, ConstraintAnnotationProcessor<?>> constraintProcessors = new HashMap<>();

    {
        this.registerTypeMapping(boolean.class, ConfigTypes.BOOLEAN);
        this.registerTypeMapping(Boolean.class, ConfigTypes.BOOLEAN);
        this.registerTypeMapping(byte.class, ConfigTypes.BYTE);
        this.registerTypeMapping(Byte.class, ConfigTypes.BYTE);
        this.registerTypeMapping(char.class, ConfigTypes.CHARACTER);
        this.registerTypeMapping(Character.class, ConfigTypes.CHARACTER);
        this.registerTypeMapping(short.class, ConfigTypes.SHORT);
        this.registerTypeMapping(Short.class, ConfigTypes.SHORT);
        this.registerTypeMapping(int.class, ConfigTypes.INTEGER);
        this.registerTypeMapping(Integer.class, ConfigTypes.INTEGER);
        this.registerTypeMapping(double.class, ConfigTypes.DOUBLE);
        this.registerTypeMapping(Double.class, ConfigTypes.DOUBLE);
        this.registerTypeMapping(float.class, ConfigTypes.FLOAT);
        this.registerTypeMapping(Float.class, ConfigTypes.FLOAT);
        this.registerTypeMapping(long.class, ConfigTypes.LONG);
        this.registerTypeMapping(Long.class, ConfigTypes.LONG);
        this.registerTypeMapping(String.class, ConfigTypes.STRING);
        this.registerTypeMapping(BigDecimal.class, ConfigTypes.UNBOUNDED_DECIMAL);
        this.registerTypeMapping(BigInteger.class, ConfigTypes.UNBOUNDED_INTEGER);
        this.registerTypeMapping(List.class, typeArguments -> ConfigTypes.makeList(typeArguments[0]));
        this.registerTypeMapping(Set.class, typeArguments -> ConfigTypes.makeSet(typeArguments[0]));
        this.registerGroupProcessor(Setting.Group.class, (annotation, field, pojo, node) -> {});
        this.registerConstraintProcessor(Setting.Constrain.Range.class, new ConstraintAnnotationProcessor<Setting.Constrain.Range>() {
            @Override
            public <T> DecimalConfigType<T> processDecimal(Setting.Constrain.Range annotation, AnnotatedElement annotated, Object pojo, DecimalConfigType<T> baseType) {
                DecimalConfigType<T> ret = baseType;
                if (annotation.min() > Double.NEGATIVE_INFINITY) {
                    ret = ret.withMinimum(ret.toActualType(BigDecimal.valueOf(annotation.min())));
                }
                if (annotation.max() < Double.POSITIVE_INFINITY) {
                    ret = ret.withMaximum(ret.toActualType(BigDecimal.valueOf(annotation.max())));
                }
                if (annotation.step() > Double.MIN_VALUE) {
                    ret = ret.withIncrement(ret.toActualType(BigDecimal.valueOf(annotation.step())));
                }
                return ret;
            }
        });
        this.registerConstraintProcessor(Setting.Constrain.BigRange.class, new ConstraintAnnotationProcessor<Setting.Constrain.BigRange>() {
            @Override
            public <T> DecimalConfigType<T> processDecimal(Setting.Constrain.BigRange annotation, AnnotatedElement annotated, Object pojo, DecimalConfigType<T> baseType) {
                DecimalConfigType<T> ret = baseType;
                if (!annotation.min().isEmpty()) {
                    ret = ret.withMinimum(ret.toActualType(new BigDecimal(annotation.min())));
                }
                if (!annotation.max().isEmpty()) {
                    ret = ret.withMaximum(ret.toActualType(new BigDecimal(annotation.max())));
                }
                if (!annotation.step().isEmpty()) {
                    ret = ret.withIncrement(ret.toActualType(new BigDecimal(annotation.step())));
                }
                return ret;
            }
        });
        this.registerConstraintProcessor(Setting.Constrain.MinLength.class, new ConstraintAnnotationProcessor<Setting.Constrain.MinLength>() {
            @Override
            public <T> StringConfigType<T> processString(Setting.Constrain.MinLength annotation, AnnotatedElement annotated, Object pojo, StringConfigType<T> baseType) {
                return baseType.withMinLength(annotation.value());
            }

            @Override
            public <T> ListConfigType<T> processList(Setting.Constrain.MinLength annotation, AnnotatedElement annotated, Object pojo, ListConfigType<T> baseType) {
                return baseType.withMinSize(annotation.value());
            }
        });
        this.registerConstraintProcessor(Setting.Constrain.MaxLength.class, new ConstraintAnnotationProcessor<Setting.Constrain.MaxLength>() {
            @Override
            public <T> StringConfigType<T> processString(Setting.Constrain.MaxLength annotation, AnnotatedElement annotated, Object pojo, StringConfigType<T> baseType) {
                return baseType.withMaxLength(annotation.value());
            }

            @Override
            public <T> ListConfigType<T> processList(Setting.Constrain.MaxLength annotation, AnnotatedElement annotated, Object pojo, ListConfigType<T> baseType) {
                return baseType.withMaxSize(annotation.value());
            }
        });
        this.registerConstraintProcessor(Setting.Constrain.Regex.class, new ConstraintAnnotationProcessor<Setting.Constrain.Regex>() {
            @Override
            public <T> StringConfigType<T> processString(Setting.Constrain.Regex annotation, AnnotatedElement annotated, Object pojo, StringConfigType<T> baseType) {
                return baseType.withPattern(annotation.value());
            }
        });
    }

    public <T> AnnotatedSettings registerTypeMapping(Class<T> clazz, ConfigType<T, ?> type) {
        if (clazz.isArray()) throw new IllegalArgumentException("Cannot register custom mappings for arrays");
        if (this.registeredTypes.containsKey(clazz)) {
            throw new IllegalStateException(clazz + " is already linked with " + this.registeredTypes.get(clazz));
        }
        this.registeredTypes.put(clazz, type);
        return this;
    }

    public <T> AnnotatedSettings registerTypeMapping(Class<? super T> clazz, ParameterizedTypeProcessor<T> processor) {
        if (clazz.isArray()) throw new IllegalArgumentException("Cannot register custom mappings for arrays");
        if (this.registeredGenericTypes.containsKey(clazz)) {
            throw new IllegalStateException(clazz + " is already linked with " + this.registeredGenericTypes.get(clazz));
        }
        this.registeredGenericTypes.put(clazz, processor);
        return this;
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
        if (this.valueSettingProcessors.containsKey(annotationType)) {
            throw new IllegalStateException("Cannot register multiple setting processors for the same annotation (" + annotationType + ")");
        }
        this.valueSettingProcessors.put(annotationType, processor);
        return this;
    }

    /**
     * Registers a group annotation processor, tasked with processing annotations on ancestor fields (config fields annotated with {@link Setting.Group}.
     *
     * @param annotationType a class representing the type of annotation to process
     * @param processor      a processor for this annotation
     * @param <A>            the type of annotation to process
     * @return {@code this}, for chaining
     */
    public <A extends Annotation> AnnotatedSettings registerGroupProcessor(Class<A> annotationType, SettingAnnotationProcessor.Group<A> processor) {
        if (this.groupSettingProcessors.containsKey(annotationType)) {
            throw new IllegalStateException("Cannot register multiple node processors for the same annotation (" + annotationType + ")");
        }
        this.groupSettingProcessors.put(annotationType, processor);
        return this;
    }

    /**
     * Registers a constraint annotation processor, tasked with processing annotations on config types.
     *
     * @param annotationType a class representing the type of annotation to process
     * @param processor      a processor for this annotation
     * @param <A>            the type of annotation to process
     * @return {@code this}, for chaining
     */
    public <A extends Annotation> AnnotatedSettings registerConstraintProcessor(Class<A> annotationType, ConstraintAnnotationProcessor<A> processor) {
        if (this.constraintProcessors.containsKey(annotationType)) {
            throw new IllegalStateException("Cannot register multiple processors for the same annotation (" + annotationType + ")");
        }
        this.constraintProcessors.put(annotationType, processor);
        return this;
    }

    public ConfigBranch makeTree(Object pojo) throws FiberException {
        ConfigTreeBuilder builder = ConfigTree.builder();
        this.applyToNode(builder, pojo);
        return builder.build();
    }

    public <P> void applyToNode(ConfigTree mergeTo, P pojo) throws FiberException {
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

        NodeOperations.moveChildren(this.constructNode(pojoClass, pojo, onlyAnnotated, convention), mergeTo);
    }

    private <P> ConfigTreeBuilder constructNode(Class<P> pojoClass, P pojo, boolean onlyAnnotated, SettingNamingConvention convention) throws FiberException {
        ConfigTreeBuilder node = ConfigTree.builder();

        List<Member> defaultEmpty = new ArrayList<>();
        Map<String, List<Member>> listenerMap = this.findListeners(pojoClass);

        for (Field field : pojoClass.getDeclaredFields()) {
            if (field.isSynthetic() || !isIncluded(field, onlyAnnotated)) continue;
            try {
                checkViolation(field);
                String name = findName(field, convention);
                if (field.isAnnotationPresent(Setting.Group.class)) {
                    this.fieldToNode(pojo, node, field, name);
                } else {
                    this.fieldToItem(node, field, pojo, name, listenerMap.getOrDefault(name, defaultEmpty));
                }
            } catch (FiberException e) {
                throw new FiberException("Failed to process field '" + Modifier.toString(field.getModifiers()) + " " + field.getType().getSimpleName() + " " + field.getName() + "' in " + pojoClass.getSimpleName(), e);
            }
        }

        return node;
    }

    private Map<String, List<Member>> findListeners(Class<?> pojoClass) {
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

    private static void checkViolation(Field field) throws FiberException {
        if (Modifier.isFinal(field.getModifiers())) throw new FiberException("Field '" + field.getName() + "' can not be final");
    }

    private static Optional<Setting> getSettingAnnotation(Field field) {
        return field.isAnnotationPresent(Setting.class) ? Optional.of(field.getAnnotation(Setting.class)) : Optional.empty();
    }

    private <P> void fieldToNode(P pojo, ConfigTreeBuilder node, Field field, String name) throws FiberException {
        ConfigTreeBuilder sub = node.fork(name);
        try {
            field.setAccessible(true);
            this.applyToNode(sub, field.get(pojo));
            this.applyAnnotationProcessors(pojo, field, sub, this.groupSettingProcessors);
            sub.build();
        } catch (IllegalAccessException e) {
            throw new FiberException("Couldn't fork and apply sub-node", e);
        }
    }

    private <T> void fieldToItem(ConfigTreeBuilder node, Field field, Object pojo, String name, List<Member> listeners) throws FiberException {
        @SuppressWarnings("unchecked") ConfigType<T, ?> type = (ConfigType<T, ?>) this.toConfigType(field.getAnnotatedType(), pojo);

        ConfigLeafBuilder<T, ?> builder = new ConfigLeafBuilder<>(node, name, type)
                .withComment(findComment(field))
                .withDefaultValue(this.findDefaultValue(field, pojo));

        for (Member listener : listeners) {
            BiConsumer<T, T> consumer = this.constructListener(listener, pojo, type.getActualType());
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

        this.applyAnnotationProcessors(pojo, field, builder, this.valueSettingProcessors);

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

    private ConfigType<?, ?> toConfigType(AnnotatedType annotatedType, Object pojo) throws FiberTypeProcessingException {
        Class<?> clazz = TypeMagic.classForType(annotatedType.getType());
        if (clazz == null) {
            throw new FiberTypeProcessingException("Unknown type " + annotatedType.getType().getTypeName());
        }
        ConfigType<?, ?> ret;
        if (annotatedType instanceof AnnotatedArrayType) {
            ConfigType<?, ?> componentType = this.toConfigType(((AnnotatedArrayType) annotatedType).getAnnotatedGenericComponentType(), pojo);
            ret = ConfigTypes.makeArray(componentType);
        } else if (this.registeredGenericTypes.containsKey(clazz)) {
            ParameterizedTypeProcessor<?> parameterizedTypeProcessor = this.registeredGenericTypes.get(clazz);
            if (!(annotatedType instanceof AnnotatedParameterizedType)) {
                throw new FiberTypeProcessingException("Expected type parameters for " + clazz);
            }
            AnnotatedType[] annotatedTypeArgs = ((AnnotatedParameterizedType) annotatedType).getAnnotatedActualTypeArguments();
            ConfigType<?, ?>[] typeArguments = new ConfigType[annotatedTypeArgs.length];
            for (int i = 0; i < annotatedTypeArgs.length; i++) {
                typeArguments[i] = this.toConfigType(annotatedTypeArgs[i], pojo);
            }
            ret = parameterizedTypeProcessor.process(typeArguments);
        } else {
            ret = this.registeredTypes.get(clazz);
        }
        if (ret == null) {
            Optional<Class<?>> closestParent = Stream.concat(this.registeredGenericTypes.keySet().stream(), this.registeredTypes.keySet().stream())
                    .filter(c -> c.isAssignableFrom(clazz))
                    .reduce((c1, c2) -> c1.isAssignableFrom(c2) ? c2 : c1);
            String closestParentSuggestion = closestParent.map(p -> "declaring the element as '" + p.getTypeName() + "', or ").orElse("");
            throw new FiberTypeProcessingException("Unknown config type " + annotatedType.getType().getTypeName() +
                    ". Consider marking as transient, or " + closestParentSuggestion + "registering a new Class -> ConfigType mapping.");
        }
        return this.constrain(ret, annotatedType, pojo);
    }

    private <T extends ConfigType<?, ?>> T constrain(T type, AnnotatedElement annotated, Object pojo) throws FiberTypeProcessingException {
        T ret = type;
        for (Annotation annotation : annotated.getAnnotations()) {
            @SuppressWarnings("unchecked") ConstraintAnnotationProcessor<Annotation> processor =
                    (ConstraintAnnotationProcessor<Annotation>) this.constraintProcessors.get(annotation.annotationType());
            if (processor != null) {
                try {
                    ret = this.constrain(processor, type, annotation, annotated, pojo);
                } catch (UnsupportedOperationException e) {
                    throw new FiberTypeProcessingException("Failed to constrain type " + type, e);
                }
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private <T extends ConfigType<?, ?>> T constrain(ConstraintAnnotationProcessor<Annotation> processor, T type, Annotation annotation, AnnotatedElement annotated, Object pojo) {
        switch (type.getKind()) {
            case DECIMAL:
                return (T) processor.processDecimal(annotation, annotated, pojo, (DecimalConfigType<?>) type);
            case LIST:
                return (T) processor.processList(annotation, annotated, pojo, (ListConfigType<?>) type);
            case STRING:
                return (T) processor.processString(annotation, annotated, pojo, (StringConfigType<?>) type);
        }
        return type;
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
            return this.constructListenerFromField((Field) listener, pojo, wantedType);
        } else if (listener instanceof Method) {
            return this.constructListenerFromMethod((Method) listener, pojo, wantedType);
        } else {
            throw new FiberException("Cannot create listener from " + listener + ": must be a field or method");
        }
    }

    private <T, P, A> BiConsumer<T,T> constructListenerFromMethod(Method method, P pojo, Class<A> wantedType) throws FiberException {
        int i = this.checkListenerMethod(method, wantedType);
        method.setAccessible(true);
        final boolean staticMethod = Modifier.isStatic(method.getModifiers());
        switch (i) {
            case 1:
                return (oldValue, newValue) -> {
                    try {
                        method.invoke(staticMethod ? null : pojo, newValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeFiberException("Failed to invoke listener " + method + " with argument " + newValue, e);
                    }
                };
            case 2:
                return (oldValue, newValue) -> {
                    try {
                        method.invoke(staticMethod ? null : pojo, oldValue, newValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeFiberException("Failed to invoke listener " + method + " with arguments " + oldValue + ", " + newValue, e);
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
        this.checkListenerField(field, wantedType);

        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        BiConsumer<T, T> consumer;
        try {
            @SuppressWarnings({ "unchecked", "unused" })
            BiConsumer<T, T> suppress = consumer = (BiConsumer<T, T>) field.get(pojo);
        } catch (IllegalAccessException e) {
            throw new FiberException("Could not construct listener", e);
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

    private static String findComment(Field field) {
        return getSettingAnnotation(field).map(Setting::comment).filter(s -> !s.isEmpty()).orElse(null);
    }

    private static String findName(Field field, SettingNamingConvention convention) {
        return Optional.ofNullable(
                field.isAnnotationPresent(Setting.Group.class) ?
                        field.getAnnotation(Setting.Group.class).name() :
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
