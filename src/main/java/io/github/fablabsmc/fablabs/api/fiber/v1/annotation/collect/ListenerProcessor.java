package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ListenerProcessor {
	void processMethod(Method method, String name);

	void processField(Field field, String name);
}
