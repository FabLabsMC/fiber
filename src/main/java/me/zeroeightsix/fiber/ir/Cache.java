package me.zeroeightsix.fiber.ir;

import java.util.HashMap;
import java.util.Set;

public class Cache {

    HashMap<String, Object> cachedValueMap = new HashMap<>();

    public Object get(String name) {
        return cachedValueMap.get(name);
    }

    public void put(String name, Object value) {
        cachedValueMap.put(name, value);
    }

    public Set<String> getCachedNames() {
        return cachedValueMap.keySet();
    }

    public void remove(String name) {
        cachedValueMap.remove(name);
    }

}
