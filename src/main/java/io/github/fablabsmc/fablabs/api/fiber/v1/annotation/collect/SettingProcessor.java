package io.github.fablabsmc.fablabs.api.fiber.v1.annotation.collect;

import java.lang.reflect.Field;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ProcessingMemberException;

public interface SettingProcessor {
	void processSetting(Field setting) throws ProcessingMemberException;

	void processGroup(Field group) throws ProcessingMemberException;
}
