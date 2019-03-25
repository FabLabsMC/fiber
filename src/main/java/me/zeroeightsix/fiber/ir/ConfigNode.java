package me.zeroeightsix.fiber.ir;

import com.google.common.collect.ImmutableMap;
import me.zeroeightsix.fiber.ConfigValueBuilder;
import me.zeroeightsix.fiber.Converter;

import java.util.HashMap;
import java.util.function.Function;

public final class ConfigNode {

	private String name;
	private HashMap<String, ConfigNode> subSettingsHashMap = new HashMap<>();
	private HashMap<String, ConfigValue> settingHashMap = new HashMap<>();
	private HashMap<String, Object> cachedValueMap = new HashMap<>();
	private Function<Class, Converter> converterFunction;

	public ConfigNode(Function<Class, Converter> converterFunction, String name) {
		this.converterFunction = converterFunction;
		this.name = name;
	}

	public ConfigNode(String name) {
		this(null, name);
	}

	/**
	 * Creates a new {@link ConfigValueBuilder} with type {@link Object}
	 *
	 * @return the created {@link ConfigValueBuilder}
	 */
	public ConfigValueBuilder<Object> builder() {
		return builder(Object.class);
	}

	/**
	 * Creates a new {@link ConfigValueBuilder}
	 *
	 * @param clazz The class of type of the to-be created {@link ConfigValueBuilder}
	 * @param <T>   The class of type of the to-be created {@link ConfigValueBuilder}
	 * @return The created {@link ConfigValueBuilder}
	 */
	public <T> ConfigValueBuilder<T> builder(Class<T> clazz) {
		return new ConfigValueBuilder<>(this, clazz);
	}

	/**
	 * Finds the sub settings object by name <code>name</code> or, if none found, creates a new {@link ConfigNode} object and stores it in this objects subsettings map
	 *
	 * @param name The name of the new {@link ConfigNode} object
	 * @return The created {@link ConfigNode} object
	 */
	public ConfigNode sub(String name) {
		if (!subSettingsHashMap.containsKey(name)) {
			subSettingsHashMap.put(name, new ConfigNode(converterFunction, name));
		}
		return subSettingsHashMap.get(name);
	}

	/**
	 * Finds the setting by the given name, and sets its value. If no setting is found, the value is cached for when said setting is registered.
	 *
	 * @param name  The name of the setting
	 * @param value The new value of the setting
	 */
	public void set(String name, Object value) {
		if (hasSetting(name)) {
			if (attemptSet(name, value)) return;
		}
		cachedValueMap.put(name, value);
	}

	/**
	 * @param name The name of the setting
	 * @return Whether or not this {@link ConfigNode} object has a registered setting by name <code>name</code>.
	 */
	public boolean hasSetting(String name) {
		return settingHashMap.containsKey(name);
	}

	/**
	 * @param name The name of the setting
	 * @return The setting by name <code>name</code> or <code>null</code> if none was found.
	 */
	public ConfigValue getSetting(String name) {
		return settingHashMap.get(name);
	}

	/**
	 * Registers a configValue and sets its value if there was a value cached for its name.
	 */
	public <T> void registerAndRecover(ConfigValue<T> configValue) {
		String name = configValue.getName();
		settingHashMap.put(name, configValue);
		if (converterFunction != null && cachedValueMap.containsKey(name)) {
			Converter converter = converterFunction.apply(configValue.getType());
			if (converter != null) {
				attemptSet(name, converter.deserialize(cachedValueMap.get(name)));
				cachedValueMap.remove(name);
			}
		}
	}

	private boolean attemptSet(String name, Object value) {
		if (!getSetting(name).getType().isAssignableFrom(value.getClass())) return false;
		getSetting(name).setValue(value);
		return true;
	}

	/**
	 * @return This {@link ConfigNode}' name
	 */
	public String getName() {
		return name;
	}

	protected HashMap<String, ConfigValue> getSettings() {
		return settingHashMap;
	}

	protected HashMap<String, Object> getCachedValues() {
		return cachedValueMap;
	}

	protected HashMap<String, ConfigNode> getSubSettings() {
		return subSettingsHashMap;
	}

	public ImmutableMap<String, ConfigNode> getSubSettingsImmutable() {
		return new ImmutableMap.Builder<String, ConfigNode>().putAll(getSubSettings()).build();
	}

	public ImmutableMap<String, ConfigValue> getSettingsImmutable() {
		return new ImmutableMap.Builder<String, ConfigValue>().putAll(getSettings()).build();
	}

	public ImmutableMap<String, Object> getCachedValuesImmutable() {
		return new ImmutableMap.Builder<String, Object>().putAll(getCachedValues()).build();
	}

	public void setConverterFunction(Function<Class, Converter> converterFunction) {
		this.converterFunction = converterFunction;
	}

}
