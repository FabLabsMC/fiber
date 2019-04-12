package me.zeroeightsix.fiber.tree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.zeroeightsix.fiber.builder.ConfigValueOldBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigNodeOld {

	private String name;
	private HashMap<String, ConfigNodeOld> subSettingsHashMap = new HashMap<>();
	private HashMap<String, ConfigValueOld> settingHashMap = new HashMap<>();
	private List<Cache> caches = new ArrayList<>();

	public ConfigNodeOld(String name) {
		this.name = name;
	}

    public ConfigNodeOld() {
	    this(null);
    }

    /**
	 * Creates a new {@link ConfigValueOldBuilder} with type {@link Object}
	 *
	 * @return the created {@link ConfigValueOldBuilder}
	 */
	public ConfigValueOldBuilder<Object> builder() {
		return builder(Object.class);
	}

	/**
	 * Creates a new {@link ConfigValueOldBuilder}
	 *
	 * @param clazz The class of type of the to-be created {@link ConfigValueOldBuilder}
	 * @param <T>   The class of type of the to-be created {@link ConfigValueOldBuilder}
	 * @return The created {@link ConfigValueOldBuilder}
	 */
	public <T> ConfigValueOldBuilder<T> builder(Class<T> clazz) {
		return new ConfigValueOldBuilder<>(this, clazz);
	}

	/**
	 * Finds the sub settings object by name <code>name</code> or, if none found, creates a new {@link ConfigNodeOld} object and stores it in this objects subsettings map
	 *
	 * @param name The name of the new {@link ConfigNodeOld} object
	 * @return The created {@link ConfigNodeOld} object
	 */
	public ConfigNodeOld sub(String name) {
		if (!subSettingsHashMap.containsKey(name)) {
			subSettingsHashMap.put(name, new ConfigNodeOld(name));
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

	public <T> boolean putValue(ConfigValueOld<T> value) {
		if (hasSetting(value.getName())) {
			// TODO: NodeOperations.mergeTo(value, getSetting(value.getName()));
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
	 * @return Whether or not this {@link ConfigNodeOld} object has a registered setting by name <code>name</code>.
	 */
	public boolean hasSetting(String name) {
		return settingHashMap.containsKey(name);
	}

	/**
	 * @param name The name of the setting
	 * @return The setting by name <code>name</code> or <code>null</code> if none was found.
	 */
	public ConfigValueOld getSetting(String name) {
		return settingHashMap.get(name);
	}

	public void register(ConfigValueOld configValue) {
		settingHashMap.put(configValue.getName(), configValue);
	}

	/**
	 * Registers a configValue and sets its value if there was a value cached for its name.
	 */
	public <T> void registerAndRecover(ConfigValueOld<T> configValue) {
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
	 * @return This {@link ConfigNodeOld}' name
	 */
	public String getName() {
		return name;
	}

	protected HashMap<String, ConfigValueOld> getSettings() {
		return settingHashMap;
	}

	protected HashMap<String, ConfigNodeOld> getSubSettings() {
		return subSettingsHashMap;
	}

	public ImmutableMap<String, ConfigNodeOld> getSubSettingsImmutable() {
		return new ImmutableMap.Builder<String, ConfigNodeOld>().putAll(getSubSettings()).build();
	}

	public ImmutableMap<String, ConfigValueOld> getSettingsImmutable() {
		return new ImmutableMap.Builder<String, ConfigValueOld>().putAll(getSettings()).build();
	}

	public ImmutableList<Cache> getCachesImmutable() {
		return new ImmutableList.Builder<Cache>().addAll(caches).build();
	}

    public void addCache(Cache cache) {
    	caches.add(cache);
	}
}
