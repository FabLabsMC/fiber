package me.zeroeightsix.fiber.api.schema.type.derived;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.zeroeightsix.fiber.api.exception.FiberConversionException;
import org.junit.jupiter.api.Test;

public class ConfigTypesTest {

	@Test
	public void testIntArray() {
		ListConfigType<int[], BigDecimal> type = ConfigTypes.makeIntArray(ConfigTypes.INTEGER);
		int[] arr = { 1, 2, 3, 4 };
		List<BigDecimal> ls = Arrays.stream(arr).mapToObj(BigDecimal::valueOf).collect(toList());
		List<BigDecimal> err = Collections.singletonList(BigDecimal.valueOf(2L * Integer.MAX_VALUE));
		assertEquals(ls, type.toSerializedType(arr), "Convert int[] -> List<BigDecimal>");
		assertArrayEquals(arr, type.toRuntimeType(ls), "Convert List<BigDecimal> -> int[]");
		assertThrows(FiberConversionException.class, () -> type.toRuntimeType(err), "Convert List<overflowed> -> int[]");
	}

	@Test
	public void testCharArray() {
		ListConfigType<char[], String> type = ConfigTypes.makeCharArray(ConfigTypes.CHARACTER);
		char[] arr = { 'a', 'b', 'c', 'd' };
		List<String> ls = Arrays.asList("a", "b", "c", "d");
		List<String> err = Arrays.asList("", "aa");
		assertEquals(ls, type.toSerializedType(arr), "Convert char[] -> List<String>");
		assertArrayEquals(arr, type.toRuntimeType(ls), "Convert List<String> -> char[]");
		assertThrows(FiberConversionException.class, () -> type.toRuntimeType(err), "Convert List<bad lengths> -> char[]");
	}

	@Test
	public void testBooleanArray() {
		ListConfigType<boolean[], Boolean> type = ConfigTypes.makeBooleanArray(ConfigTypes.BOOLEAN);
		boolean[] arr = { false, true };
		List<Boolean> ls = Arrays.asList(false, true);
		assertEquals(ls, type.toSerializedType(arr), "Convert boolean[] -> List<Boolean>");
		assertArrayEquals(arr, type.toRuntimeType(ls), "Convert List<Boolean> -> boolean[]");
	}

	@Test
	public void testObjArray() {
		ListConfigType<String[], String> type = ConfigTypes.makeArray(ConfigTypes.STRING);
		String[] arr = { "string1", "string2", "string3", "string4" };
		List<String> ls = Arrays.asList(arr.clone());
		assertEquals(ls, type.toSerializedType(arr), "Convert String[] -> List<String>");
		assertArrayEquals(arr, type.toRuntimeType(ls), "Convert List<String> -> String[]");
	}
}
