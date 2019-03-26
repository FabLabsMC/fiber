package me.zeroeightsix.fiber.ir;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.zeroeightsix.fiber.ConfigOperations;
import me.zeroeightsix.fiber.builder.ConfigValueBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ConfigNode {

	private String name;
	private HashMap<String, ConfigNode> subSettingsHashMap = new HashMap<>();
	private HashMap<String, ConfigValue> settingHashMap = new HashMap<>();
	private List<Cache> caches = new ArrayList<>();

	public ConfigNode(String name) {
		this.name = name;
	}

    public ConfigNode() {
	    this(null);
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
			subSettingsHashMap.put(name, new ConfigNode(name));
		}
		return subSettingsHashMap.get(name);
	}

	/**
	 * Finds the setting by the given name, and sets its value. If no setting is found, the value is cached for when said setting is registered.
	 *
	 * @param name  The name of the setting
	 * @param value The new value of the setting
	 */
	public boolean set(String name, Object value) {
		if (hasSetting(name)) {
            return attemptSet(name, value);
		}
		return false;
	}

	public <T> boolean putValue(ConfigValue<T> value) {
		if (hasSetting(value.getName())) {
			ConfigOperations.mergeTo(value, getSetting(value.getName()));
			return false;
		}
		register(value);
		return true;
	}

	public void setOrCache(String name, Object value, Cache cache) {
		if (!set(name, value)) {
		    cache.put(name, value);
		}
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

	public void register(ConfigValue configValue) {
		settingHashMap.put(configValue.getName(), configValue);
	}

	/**
	 * Registers a configValue and sets its value if there was a value cached for its name.
	 */
	public <T> void registerAndRecover(ConfigValue<T> configValue) {
		String name = configValue.getName();
		register(configValue);

		for (Cache cache : caches) {
		    Object o = cache.get(name);
		    if (o == null) continue;
		    configValue.setValue((T) o);
		    cache.remove(name);
		    return;
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

	protected HashMap<String, ConfigNode> getSubSettings() {
		return subSettingsHashMap;
	}

	public ImmutableMap<String, ConfigNode> getSubSettingsImmutable() {
		return new ImmutableMap.Builder<String, ConfigNode>().putAll(getSubSettings()).build();
	}

	public ImmutableMap<String, ConfigValue> getSettingsImmutable() {
		return new ImmutableMap.Builder<String, ConfigValue>().putAll(getSettings()).build();
	}

	public ImmutableList<Cache> getCachesImmutable() {
		return new ImmutableList.Builder<Cache>().addAll(caches).build();
	}

    public void addCache(Cache cache) {
    	caches.add(cache);
	}
}
