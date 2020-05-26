package io.github.fablabsmc.fablabs.impl.fiber.tree;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigAttribute;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PropertyMirrorImplTest {
	// see https://github.com/FabLabsMC/fiber/issues/63
	@Test
	@DisplayName("Passively invalidated values are correct")
	public void testPassiveInvalidation() {
		PropertyMirror<Boolean> mirror = PropertyMirror.create(ConfigTypes.BOOLEAN);
		ConfigTree.builder()
				.beginValue("mirrored", ConfigTypes.BOOLEAN, false)
				.finishValue(mirror::mirror)
				.build();

		assertFalse(mirror.getValue());

		assertTrue(mirror.setValue(true));
		assertTrue(mirror.getValue());

		assertTrue(mirror.setValue(true));
		assertTrue(mirror.getValue());

		assertTrue(mirror.setValue(false));
		assertFalse(mirror.getValue());
	}

	@Test
	@DisplayName("Actively invalidated values are correct")
	public void testActiveInvalidation() {
		// Attributes aren't ConfigLeafs, so they will use active invalidation.
		ConfigAttribute<Boolean> attribute = ConfigAttribute.create(null, ConfigTypes.BOOLEAN, false);
		PropertyMirror<Boolean> mirror = PropertyMirror.create(ConfigTypes.BOOLEAN);
		mirror.mirror(attribute);

		assertFalse(mirror.getValue());

		assertTrue(mirror.setValue(true));
		assertTrue(mirror.getValue());

		assertTrue(mirror.setValue(true));
		assertTrue(mirror.getValue());

		assertTrue(mirror.setValue(false));
		assertFalse(mirror.getValue());
	}
}
