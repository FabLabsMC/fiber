package io.github.fablabsmc.fablabs.impl.fiber.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import io.github.fablabsmc.fablabs.api.fiber.v1.NodeOperations;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.MemberCollector;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.SettingProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention.NoNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.convention.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.BranchAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConfigAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.LeafAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ParameterizedTypeProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigLeafBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberTypeProcessingException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.MalformedFieldException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ProcessingMemberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.RuntimeFiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ListConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.NumberConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.StringConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.magic.TypeMagic;

public final class AnnotatedSettingsImpl implements AnnotatedSettings {
	private final Map<Class<?>, ParameterizedTypeProcessor<?>> registeredGenericTypes = new HashMap<>();
	private final Map<Class<?>, ConfigType<?, ?, ?>> registeredTypes = new HashMap<>();
	private final Map<Class<? extends Annotation>, LeafAnnotationProcessor<?>> valueSettingProcessors = new HashMap<>();
	private final Map<Class<? extends Annotation>, BranchAnnotationProcessor<?>> groupSettingProcessors = new HashMap<>();
	private final Map<Class<? extends Annotation>, ConstraintAnnotationProcessor<?>> constraintProcessors = new HashMap<>();
	private final MemberCollector memberCollector;

	public AnnotatedSettingsImpl(MemberCollector memberCollector) {
		this.memberCollector = memberCollector;
	}

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
		this.registerGroupProcessor(Setting.Group.class, (annotation, field, pojo, node) -> {
		});
		this.registerConstraintProcessor(Setting.Constrain.Range.class, new ConstraintAnnotationProcessor<Setting.Constrain.Range>() {
			@Override
			public <T> NumberConfigType<T> processDecimal(NumberConfigType<T> baseType, Setting.Constrain.Range annotation, AnnotatedElement annotated) {
				DecimalSerializableType serType = baseType.getSerializedType();
				BigDecimal min = serType.getMaximum();
				BigDecimal max = serType.getMaximum();
				BigDecimal inc = serType.getIncrement();

				if (annotation.min() > Double.NEGATIVE_INFINITY) {
					min = BigDecimal.valueOf(annotation.min());
				}

				if (annotation.max() < Double.POSITIVE_INFINITY) {
					max = BigDecimal.valueOf(annotation.max());
				}

				if (annotation.step() > Double.MIN_VALUE) {
					inc = BigDecimal.valueOf(annotation.step());
				}

				return baseType.withType(new DecimalSerializableType(min, max, inc));
			}
		});
		this.registerConstraintProcessor(Setting.Constrain.BigRange.class, new ConstraintAnnotationProcessor<Setting.Constrain.BigRange>() {
			@Override
			public <T> NumberConfigType<T> processDecimal(NumberConfigType<T> baseType, Setting.Constrain.BigRange annotation, AnnotatedElement annotated) {
				DecimalSerializableType serType = baseType.getSerializedType();
				BigDecimal min = serType.getMaximum();
				BigDecimal max = serType.getMaximum();
				BigDecimal inc = serType.getIncrement();

				if (!annotation.min().isEmpty()) {
					min = new BigDecimal(annotation.min());
				}

				if (!annotation.max().isEmpty()) {
					max = new BigDecimal(annotation.max());
				}

				if (!annotation.step().isEmpty()) {
					inc = new BigDecimal(annotation.step());
				}

				return baseType.withType(new DecimalSerializableType(min, max, inc));
			}
		});
		this.registerConstraintProcessor(Setting.Constrain.MinLength.class, new ConstraintAnnotationProcessor<Setting.Constrain.MinLength>() {
			@Override
			public <T> StringConfigType<T> processString(StringConfigType<T> baseType, Setting.Constrain.MinLength annotation, AnnotatedElement annotated) {
				return baseType.withMinLength(annotation.value());
			}

			@Override
			public <T, E> ListConfigType<T, E> processList(ListConfigType<T, E> baseType, Setting.Constrain.MinLength annotation, AnnotatedElement annotated) {
				return baseType.withMinSize(annotation.value());
			}
		});
		this.registerConstraintProcessor(Setting.Constrain.MaxLength.class, new ConstraintAnnotationProcessor<Setting.Constrain.MaxLength>() {
			@Override
			public <T> StringConfigType<T> processString(StringConfigType<T> baseType, Setting.Constrain.MaxLength annotation, AnnotatedElement annotated) {
				return baseType.withMaxLength(annotation.value());
			}

			@Override
			public <T, E> ListConfigType<T, E> processList(ListConfigType<T, E> baseType, Setting.Constrain.MaxLength annotation, AnnotatedElement annotated) {
				return baseType.withMaxSize(annotation.value());
			}
		});
		this.registerConstraintProcessor(Setting.Constrain.Regex.class, new ConstraintAnnotationProcessor<Setting.Constrain.Regex>() {
			@Override
			public <T> StringConfigType<T> processString(StringConfigType<T> baseType, Setting.Constrain.Regex annotation, AnnotatedElement annotated) {
				return baseType.withPattern(annotation.value());
			}
		});
	}

	@Override
	public <T> AnnotatedSettings registerTypeMapping(Class<? super T> clazz, ConfigType<T, ?, ?> type) {
		if (clazz.isArray()) throw new IllegalArgumentException("Cannot register custom mappings for arrays");

		if (this.registeredTypes.containsKey(clazz)) {
			throw new IllegalStateException(clazz + " is already linked with " + this.registeredTypes.get(clazz));
		}

		this.registeredTypes.put(clazz, type);
		return this;
	}

	@Override
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
	 * @param <A>            the type of annotation to process
	 * @param annotationType a class representing the type of annotation to process
	 * @param processor      a processor for this annotation
	 * @return {@code this}, for chaining
	 */
	@Override
	public <A extends Annotation> AnnotatedSettings registerSettingProcessor(Class<A> annotationType, LeafAnnotationProcessor<A> processor) {
		checkAnnotationValidity(annotationType);

		if (this.valueSettingProcessors.containsKey(annotationType)) {
			throw new IllegalStateException("Cannot register multiple setting processors for the same annotation (" + annotationType + ")");
		}

		this.valueSettingProcessors.put(annotationType, processor);
		return this;
	}

	private static void checkAnnotationValidity(Class<? extends Annotation> annotationType) {
		if (!annotationType.isAnnotationPresent(Retention.class) || annotationType.getAnnotation(Retention.class).value() != RetentionPolicy.RUNTIME) {
			throw new IllegalArgumentException("Annotation type " + annotationType + " does not have RUNTIME retention");
		}
	}

	/**
	 * Registers a group annotation processor, tasked with processing annotations on ancestor fields (config fields annotated with {@link Setting.Group}.
	 *
	 * @param <A>            the type of annotation to process
	 * @param annotationType a class representing the type of annotation to process
	 * @param processor      a processor for this annotation
	 * @return {@code this}, for chaining
	 */
	@Override
	public <A extends Annotation> AnnotatedSettings registerGroupProcessor(Class<A> annotationType, BranchAnnotationProcessor<A> processor) {
		checkAnnotationValidity(annotationType);

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
	@Override
	public <A extends Annotation> AnnotatedSettings registerConstraintProcessor(Class<A> annotationType, ConstraintAnnotationProcessor<A> processor) {
		checkAnnotationValidity(annotationType);

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
		@SuppressWarnings("unchecked") Class<P> pojoClass = (Class<P>) pojo.getClass();
		SettingNamingConvention convention;

		if (pojoClass.isAnnotationPresent(Settings.class)) {
			Settings settingsAnnotation = pojoClass.getAnnotation(Settings.class);
			convention = createConvention(settingsAnnotation.namingConvention());
		} else { // Assume defaults
			convention = new NoNamingConvention();
		}

		ConfigTreeBuilder builder = ConfigTree.builder();
		PojoProcessorImpl processor = this.new PojoProcessorImpl(pojo, convention, builder);
		this.memberCollector.collect(pojo, pojoClass, processor);
		NodeOperations.moveChildren(builder, mergeTo);
	}

	private class PojoProcessorImpl implements SettingProcessor {
		private final Object pojo;
		private final SettingNamingConvention convention;
		private final Map<String, List<Member>> listenerMap = new HashMap<>();
		private final ConfigTreeBuilder builder;

		PojoProcessorImpl(Object pojo, SettingNamingConvention convention, ConfigTreeBuilder builder) {
			this.pojo = pojo;
			this.convention = convention;
			this.builder = builder;
		}

		@Override
		public void processSetting(Object pojo, Field setting) throws ProcessingMemberException {
			try {
				checkViolation(setting);
				String name = findName(setting, PojoProcessorImpl.this.convention);
				ConfigType<?, ?, ?> type = this.toConfigType(setting.getAnnotatedType());
				this.buildLeaf(setting, name, type);
			} catch (FiberException e) {
				throw new ProcessingMemberException("Failed to process setting '" + Modifier.toString(setting.getModifiers()) + " " + setting.getType().getSimpleName() + " " + setting.getName() + "' in " + setting.getDeclaringClass().getSimpleName(), e, setting);
			}
		}

		@Override
		public void processGroup(Field group, Object pojo) throws ProcessingMemberException {
			try {
				checkViolation(group);
				String name = findName(group, this.convention);
				ConfigTreeBuilder sub = this.builder.fork(name);
				group.setAccessible(true);
				AnnotatedSettingsImpl.this.applyToNode(sub, group.get(this.pojo));
				this.applyAnnotationProcessors(group, this.pojo, sub, AnnotatedSettingsImpl.this.groupSettingProcessors);
				sub.build();
			} catch (FiberException | IllegalAccessException e) {
				throw new ProcessingMemberException("Failed to process group '" + Modifier.toString(group.getModifiers()) + " " + group.getType().getSimpleName() + " " + group.getName() + "' in " + group.getDeclaringClass().getSimpleName(), e, group);
			}
		}

		@Override
		public void processListenerMethod(Object pojo, Method method, String name) {
			this.listenerMap.computeIfAbsent(name, v -> new ArrayList<>()).add(method);
		}

		@Override
		public void processListenerField(Object pojo, Field field, String name) {
			this.listenerMap.computeIfAbsent(name, v -> new ArrayList<>()).add(field);
		}

		private <R, S> void buildLeaf(Field setting, String name, ConfigType<R, S, ?> type) throws FiberException {
			List<Member> listeners = this.listenerMap.getOrDefault(name, Collections.emptyList());
			ConfigLeafBuilder<S, R> leaf = this.builder
					.beginValue(name, type, this.findDefaultValue(setting, this.pojo))
					.withComment(findComment(setting))
					.withListener((t, newValue) -> {
						try {
							setting.setAccessible(true);
							setting.set(this.pojo, newValue);
						} catch (IllegalAccessException e) {
							throw new RuntimeFiberException("Failed to update field value", e);
						}
					});

			for (Member listener : listeners) {
				BiConsumer<R, R> consumer = this.constructListener(listener, this.pojo, type.getRuntimeType());
				if (consumer == null) continue;
				leaf.withListener(consumer);
			}

			this.applyAnnotationProcessors(setting, this.pojo, leaf, AnnotatedSettingsImpl.this.valueSettingProcessors);
			leaf.build();
		}

		private <C> void applyAnnotationProcessors(Field field, Object pojo, C sub, Map<Class<? extends Annotation>, ? extends ConfigAnnotationProcessor<?, Field, C>> settingProcessors) {
			for (Annotation annotation : field.getAnnotations()) {
				@SuppressWarnings("unchecked") ConfigAnnotationProcessor<Annotation, Field, C> processor = (ConfigAnnotationProcessor<Annotation, Field, C>) settingProcessors.get(annotation.annotationType());

				if (processor != null) {
					processor.apply(annotation, field, pojo, sub);
				}
			}
		}

		@Nonnull
		private ConfigType<?, ?, ?> toConfigType(AnnotatedType annotatedType) throws FiberTypeProcessingException {
			Class<?> clazz = TypeMagic.classForType(annotatedType.getType());

			if (clazz == null) {
				throw new FiberTypeProcessingException("Unknown type " + annotatedType.getType().getTypeName());
			}

			@Nonnull ConfigType<?, ?, ?> ret;

			if (annotatedType instanceof AnnotatedArrayType) {
				ConfigType<?, ?, ?> componentType = this.toConfigType(((AnnotatedArrayType) annotatedType).getAnnotatedGenericComponentType());
				Class<?> componentClass = clazz.getComponentType();
				assert componentClass != null;
				ret = this.makeArrayConfigType(componentClass, componentType);
			} else if (AnnotatedSettingsImpl.this.registeredGenericTypes.containsKey(clazz)) {
				ParameterizedTypeProcessor<?> parameterizedTypeProcessor = AnnotatedSettingsImpl.this.registeredGenericTypes.get(clazz);

				if (!(annotatedType instanceof AnnotatedParameterizedType)) {
					throw new FiberTypeProcessingException("Expected type parameters for " + clazz);
				}

				AnnotatedType[] annotatedTypeArgs = ((AnnotatedParameterizedType) annotatedType).getAnnotatedActualTypeArguments();
				ConfigType<?, ?, ?>[] typeArguments = new ConfigType[annotatedTypeArgs.length];

				for (int i = 0; i < annotatedTypeArgs.length; i++) {
					typeArguments[i] = this.toConfigType(annotatedTypeArgs[i]);
				}

				ret = parameterizedTypeProcessor.process(typeArguments);
			} else if (AnnotatedSettingsImpl.this.registeredTypes.containsKey(clazz)) {
				ret = AnnotatedSettingsImpl.this.registeredTypes.get(clazz);
			} else if (clazz.isEnum()) {
				ret = ConfigTypes.makeEnum(clazz.asSubclass(Enum.class));
			} else {
				Optional<Class<?>> closestParent = Stream.concat(AnnotatedSettingsImpl.this.registeredGenericTypes.keySet().stream(), AnnotatedSettingsImpl.this.registeredTypes.keySet().stream())
						.filter(c -> c.isAssignableFrom(clazz))
						.reduce((c1, c2) -> c1.isAssignableFrom(c2) ? c2 : c1);
				String closestParentSuggestion = closestParent.map(p -> "declaring the element as '" + p.getTypeName() + "', or ").orElse("");
				throw new FiberTypeProcessingException("Unknown config type " + annotatedType.getType().getTypeName()
						+ ". Consider marking as transient, or " + closestParentSuggestion + "registering a new Class -> ConfigType mapping.");
			}

			assert ret != null;
			return this.constrain(ret, annotatedType);
		}

		@SuppressWarnings("unchecked")
		private ConfigType<?, ?, ?> makeArrayConfigType(Class<?> componentClass, ConfigType<?, ?, ?> componentType) {
			assert TypeMagic.wrapPrimitive(componentClass) == TypeMagic.wrapPrimitive(componentType.getRuntimeType()) : "Class=" + componentClass + ", ConfigType=" + componentType;

			if (componentClass == boolean.class) {
				return ConfigTypes.makeBooleanArray((ConfigType<Boolean, ?, ?>) componentType);
			} else if (componentClass == byte.class) {
				return ConfigTypes.makeByteArray((ConfigType<Byte, ?, ?>) componentType);
			} else if (componentClass == short.class) {
				return ConfigTypes.makeShortArray((ConfigType<Short, ?, ?>) componentType);
			} else if (componentClass == int.class) {
				return ConfigTypes.makeIntArray((ConfigType<Integer, ?, ?>) componentType);
			} else if (componentClass == long.class) {
				return ConfigTypes.makeLongArray((ConfigType<Long, ?, ?>) componentType);
			} else if (componentClass == float.class) {
				return ConfigTypes.makeFloatArray((ConfigType<Float, ?, ?>) componentType);
			} else if (componentClass == double.class) {
				return ConfigTypes.makeDoubleArray((ConfigType<Double, ?, ?>) componentType);
			} else if (componentClass == char.class) {
				return ConfigTypes.makeCharArray((ConfigType<Character, ?, ?>) componentType);
			} else {
				assert !componentClass.isPrimitive() : "Primitive component type: " + componentClass;
				return ConfigTypes.makeArray(componentType);
			}
		}

		private <T extends ConfigType<?, ?, ?>> T constrain(T type, AnnotatedElement annotated) throws FiberTypeProcessingException {
			T ret = type;

			for (Annotation annotation : annotated.getAnnotations()) {
				@SuppressWarnings("unchecked") ConstraintAnnotationProcessor<Annotation> processor =
						(ConstraintAnnotationProcessor<Annotation>) AnnotatedSettingsImpl.this.constraintProcessors.get(annotation.annotationType());
				if (processor != null) {
					try {
						ret = this.constrain(ret, processor, annotation, annotated);
					} catch (UnsupportedOperationException e) {
						throw new FiberTypeProcessingException("Failed to constrain type " + type, e);
					}
				}
			}

			return ret;
		}

		@SuppressWarnings("unchecked")
		private <T extends ConfigType<?, ?, ?>> T constrain(T type, ConstraintAnnotationProcessor<Annotation> processor, Annotation annotation, AnnotatedElement annotated) {
			return (T) type.constrain(processor, annotation, annotated);
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

		private <T> BiConsumer<T, T> constructListener(Member listener, Object pojo, Class<T> wantedType) throws FiberException {
			BiConsumer<T, T> result;

			if (listener instanceof Field) {
				result = this.constructListenerFromField((Field) listener, pojo, wantedType);
			} else if (listener instanceof Method) {
				result = this.constructListenerFromMethod((Method) listener, pojo, wantedType);
			} else {
				throw new FiberException("Cannot create listener from " + listener + ": must be a field or method");
			}

			// note: we assume that a value coming from the IR is valid
			return result;
		}

		private <T, A> BiConsumer<T, T> constructListenerFromMethod(Method method, Object pojo, Class<A> wantedType) throws FiberException {
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
			if (!method.getReturnType().equals(void.class)) {
				throw new FiberException("Listener method must return void");
			}

			int paramCount = method.getParameterCount();

			if ((paramCount != 1 && paramCount != 2) || !method.getParameterTypes()[0].equals(wantedType)) {
				throw new FiberException("Listener method must have exactly two parameters of type that it listens for");
			}

			return paramCount;
		}

		private <T, A> BiConsumer<T, T> constructListenerFromField(Field field, Object pojo, Class<A> wantedType) throws FiberException {
			this.checkListenerField(field, wantedType);
			field.setAccessible(true);

			try {
				@SuppressWarnings("unchecked") BiConsumer<T, T> consumer = (BiConsumer<T, T>) field.get(pojo);
				return consumer;
			} catch (IllegalAccessException e) {
				throw new FiberException("Could not construct listener", e);
			}
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
	}

	private static void checkViolation(Field field) throws FiberException {
		if (Modifier.isFinal(field.getModifiers())) {
			throw new FiberException("Field '" + field.getName() + "' can not be final");
		}
	}

	private static Optional<Setting> getSettingAnnotation(Field field) {
		return field.isAnnotationPresent(Setting.class) ? Optional.of(field.getAnnotation(Setting.class)) : Optional.empty();
	}

	private static String findComment(Field field) {
		return getSettingAnnotation(field).map(Setting::comment).filter(s -> !s.isEmpty()).orElse(null);
	}

	private static String findName(Field field, SettingNamingConvention convention) {
		return Optional.ofNullable(
				field.isAnnotationPresent(Setting.Group.class)
						? field.getAnnotation(Setting.Group.class).name()
						: getSettingAnnotation(field).map(Setting::name).orElse(null))
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
