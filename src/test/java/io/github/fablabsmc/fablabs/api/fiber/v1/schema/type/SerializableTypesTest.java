package io.github.fablabsmc.fablabs.api.fiber.v1.schema.type;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SerializableTypesTest {
	@DisplayName("Test decimal type comprehension")
	@Test
	void testDecimalType() {
		DecimalSerializableType typeA = new DecimalSerializableType(BigDecimal.TEN.negate(), null, null);
		DecimalSerializableType typeB = new DecimalSerializableType(null, BigDecimal.TEN, null);
		assertFalse(typeA.isAssignableFrom(typeB));
		assertFalse(typeB.isAssignableFrom(typeA));
		DecimalSerializableType typeC = new DecimalSerializableType(BigDecimal.TEN.negate(), BigDecimal.TEN, null);
		assertTrue(typeA.isAssignableFrom(typeC));
		assertTrue(typeB.isAssignableFrom(typeC));
		assertFalse(typeC.isAssignableFrom(typeA));
		assertFalse(typeC.isAssignableFrom(typeB));
		assertThrows(IllegalStateException.class, () -> new DecimalSerializableType(null, null, BigDecimal.ONE));
		DecimalSerializableType typeD = new DecimalSerializableType(BigDecimal.TEN.negate(), BigDecimal.TEN, BigDecimal.ONE);
		assertTrue(typeA.isAssignableFrom(typeD));
		assertTrue(typeB.isAssignableFrom(typeD));
		assertTrue(typeC.isAssignableFrom(typeD));
		assertFalse(typeD.isAssignableFrom(typeA));
		assertFalse(typeD.isAssignableFrom(typeB));
		assertFalse(typeD.isAssignableFrom(typeC));
		DecimalSerializableType typeE = new DecimalSerializableType(BigDecimal.ONE.negate(), BigDecimal.ONE, BigDecimal.valueOf(0.5));
		assertTrue(typeA.isAssignableFrom(typeE));
		assertTrue(typeB.isAssignableFrom(typeE));
		assertTrue(typeC.isAssignableFrom(typeE));
		assertFalse(typeD.isAssignableFrom(typeE));
		assertFalse(typeE.isAssignableFrom(typeA));
		assertFalse(typeE.isAssignableFrom(typeB));
		assertFalse(typeE.isAssignableFrom(typeC));
		assertFalse(typeE.isAssignableFrom(typeD));
		DecimalSerializableType typeF = new DecimalSerializableType(BigDecimal.ONE.negate(), BigDecimal.ONE, BigDecimal.ONE);
		assertTrue(typeE.isAssignableFrom(typeF));
		assertFalse(typeF.isAssignableFrom(typeE));
		DecimalSerializableType typeG = new DecimalSerializableType(BigDecimal.ONE.negate(), BigDecimal.ONE, BigDecimal.ONE.setScale(100, BigDecimal.ROUND_UNNECESSARY));
		assertTrue(typeF.isAssignableFrom(typeG));
		assertTrue(typeG.isAssignableFrom(typeF));
	}
}
