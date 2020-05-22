package io.github.fablabsmc.fablabs.impl.fiber.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect.MemberCollector;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.BranchAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ConstraintAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.LeafAnnotationProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.processor.ParameterizedTypeProcessor;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.DecimalSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ListConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.NumberConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.StringConfigType;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.collect.MemberCollectorImpl;
import io.github.fablabsmc.fablabs.impl.fiber.annotation.collect.MemberCollectorRecursiveImpl;

public final class AnnotatedSettingsBuilderImpl implements AnnotatedSettings.Builder {
	private final Map<Class<?>, ParameterizedTypeProcessor<?>> registeredGenericTypes = new LinkedHashMap<>();
	private final Map<Class<?>, ConfigType<?, ?, ?>> registeredTypes = new LinkedHashMap<>();
	private final Map<Class<? extends Annotation>, LeafAnnotationProcessor<?>> valueSettingProcessors = new LinkedHashMap<>();
	private final Map<Class<? extends Annotation>, BranchAnnotationProcessor<?>> groupSettingProcessors = new LinkedHashMap<>();
	private final Map<Class<? extends Annotation>, ConstraintAnnotationProcessor<?>> constraintProcessors = new LinkedHashMap<>();
	private SettingNamingConvention convention = SettingNamingConvention.NONE;
	@Nullable
	private MemberCollector collector;
	private boolean collectRecursively;
	private boolean annotatedOnly;

	@Override
	public AnnotatedSettings.Builder apply(Consumer<AnnotatedSettings.Builder> configuration) {
		configuration.accept(this);
		return this;
	}

	@Override
	public AnnotatedSettings.Builder collectMembersRecursively() {
		this.collectRecursively = true;
		return this;
	}

	@Override
	public AnnotatedSettings.Builder collectOnlyAnnotatedMembers() {
		this.annotatedOnly = true;
		return this;
	}

	@Override
	public AnnotatedSettings.Builder collectMembersWith(MemberCollector collector) {
		this.collector = collector;
		return this;
	}

	@Override
	public AnnotatedSettings.Builder useNamingConvention(SettingNamingConvention convention) {
		this.convention = convention;
		return this;
	}

	@Override
	public <T> AnnotatedSettings.Builder registerTypeMapping(Class<? super T> clazz, ConfigType<T, ?, ?> type) {
		if (clazz.isArray()) throw new IllegalArgumentException("Cannot register custom mappings for arrays");

		if (this.registeredTypes.containsKey(clazz)) {
			throw new IllegalStateException(clazz + " is already linked with " + this.registeredTypes.get(clazz));
		}

		this.registeredTypes.put(clazz, type);
		return this;
	}

	@Override
	public <T> AnnotatedSettings.Builder registerTypeMapping(Class<? super T> clazz, ParameterizedTypeProcessor<T> processor) {
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
	public <A extends Annotation> AnnotatedSettings.Builder registerSettingProcessor(Class<A> annotationType, LeafAnnotationProcessor<A> processor) {
		checkAnnotationValidity(annotationType);

		if (this.valueSettingProcessors.containsKey(annotationType)) {
			throw new IllegalStateException("Cannot register multiple setting processors for the same annotation (" + annotationType + ")");
		}

		this.valueSettingProcessors.put(annotationType, processor);
		return this;
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
	public <A extends Annotation> AnnotatedSettings.Builder registerGroupProcessor(Class<A> annotationType, BranchAnnotationProcessor<A> processor) {
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
	 * @param <A>            the type of annotation to process
	 * @param annotationType a class representing the type of annotation to process
	 * @param processor      a processor for this annotation
	 * @return {@code this}, for chaining
	 */
	@Override
	public <A extends Annotation> AnnotatedSettings.Builder registerConstraintProcessor(Class<A> annotationType, ConstraintAnnotationProcessor<A> processor) {
		checkAnnotationValidity(annotationType);

		if (this.constraintProcessors.containsKey(annotationType)) {
			throw new IllegalStateException("Cannot register multiple processors for the same annotation (" + annotationType + ")");
		}

		this.constraintProcessors.put(annotationType, processor);
		return this;
	}

	private static void checkAnnotationValidity(Class<? extends Annotation> annotationType) {
		if (!annotationType.isAnnotationPresent(Retention.class) || annotationType.getAnnotation(Retention.class).value() != RetentionPolicy.RUNTIME) {
			throw new IllegalArgumentException("Annotation type " + annotationType + " does not have RUNTIME retention");
		}
	}

	@Override
	public AnnotatedSettings build() {
		@Nonnull MemberCollector collector;

		if (this.collector != null) {
			collector = this.collector;
		} else if (this.collectRecursively) {
			collector = new MemberCollectorRecursiveImpl(this.annotatedOnly);
		} else {
			collector = new MemberCollectorImpl(this.annotatedOnly);
		}

		return new AnnotatedSettingsImpl(this.registeredGenericTypes, this.registeredTypes, this.valueSettingProcessors, this.groupSettingProcessors, this.constraintProcessors, collector, this.convention);
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
}
