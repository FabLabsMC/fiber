package io.github.fablabsmc.fablabs.api.fiber.v1.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.github.fablabsmc.fablabs.api.fiber.v1.NodeOperationsTest;
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.RecordSerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.SerializableType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.NumberConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.RecordConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JanksonValueSerializerTest {
	public static final NumberConfigType<Integer> INT_TYPE = ConfigTypes.INTEGER.derive(int.class, Function.identity(), Function.identity());

	@Test
	@DisplayName("Node -> Node")
	void nodeSerialization() throws IOException, FiberException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JanksonValueSerializer jk = new JanksonValueSerializer(false);
		ConfigTree nodeOne = ConfigTree.builder()
				.beginValue("A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN)
				.withComment("An int")
				.finishValue()
				.build();

		ConfigTree nodeTwo = ConfigTree.builder()
				.withValue("A", ConfigTypes.INTEGER, 20)
				.build();

		FiberSerialization.serialize(nodeOne, bos, jk);
		FiberSerialization.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()), jk);
		NodeOperationsTest.testNodeFor(nodeTwo, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN);
		assertEquals("{\n\t// An int\n\t\"A\": 10\n}", bos.toString("UTF-8"));
	}

	@Test
	@DisplayName("List<Integer> -> List<Integer>")
	void nodeSerializationList() throws IOException, FiberException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JanksonValueSerializer jk = new JanksonValueSerializer(true);
		ConfigType<List<Integer>, List<BigDecimal>, ?> cfgType = ConfigTypes.makeList(ConfigTypes.INTEGER);
		ConfigTree nodeOne = ConfigTree.builder()
				.beginValue("A", cfgType.getSerializedType(), Collections.singletonList(BigDecimal.TEN))
				.finishValue()
				.build();

		ConfigTree nodeTwo = ConfigTree.builder()
				.withValue("A", cfgType, Collections.singletonList(20))
				.build();

		FiberSerialization.serialize(nodeOne, bos, jk);
		FiberSerialization.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()), jk);
		NodeOperationsTest.testNodeFor(nodeTwo, "A", cfgType.getSerializedType(), Collections.singletonList(BigDecimal.TEN));
		assertEquals("{ \"A\": [ 10 ] }", bos.toString("UTF-8"));
	}

	@Test
	@DisplayName("Map<Integer> -> Map<Integer>")
	void nodeSerializationMap() throws IOException, FiberException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JanksonValueSerializer jk = new JanksonValueSerializer(true);
		ConfigType<Map<String, Integer>, Map<String, BigDecimal>, ?> cfgType = ConfigTypes.makeMap(ConfigTypes.STRING, ConfigTypes.INTEGER);
		ConfigTree nodeOne = ConfigTree.builder()
				.beginValue("A", cfgType.getSerializedType(), Collections.singletonMap("K", BigDecimal.TEN))
				.finishValue()
				.build();

		ConfigTree nodeTwo = ConfigTree.builder()
				.withValue("A", cfgType, Collections.singletonMap("L", 20))
				.build();

		FiberSerialization.serialize(nodeOne, bos, jk);
		FiberSerialization.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()), jk);
		NodeOperationsTest.testNodeFor(nodeTwo, "A", cfgType.getSerializedType(), Collections.singletonMap("K", BigDecimal.TEN));
		assertEquals("{ \"A\": { \"K\": 10 } }", bos.toString("UTF-8"));
	}

	@Test
	@DisplayName("Record<Integer, String> -> Record<Integer, String>")
	void nodeSerializationRecord() throws IOException, FiberException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JanksonValueSerializer jk = new JanksonValueSerializer(true);

		Map<String, SerializableType<?>> fields = new LinkedHashMap<>();
		fields.put("I", ConfigTypes.INTEGER.getSerializedType());
		fields.put("S", ConfigTypes.STRING.getSerializedType());
		ConfigType<Map<String, Object>, Map<String, Object>, ?> cfgType = new RecordConfigType<>(
				new RecordSerializableType(fields),
				Map.class,
				Function.identity(),
				Function.identity());
		Map<String, Object> r = new LinkedHashMap<>();
		r.put("I", BigDecimal.TEN);
		r.put("S", "hello");
		ConfigTree nodeOne = ConfigTree.builder()
				.withValue("A", cfgType, r)
				.build();

		Map<String, Object> r2 = new LinkedHashMap<>();
		r2.put("I", BigDecimal.valueOf(5));
		r2.put("S", "world");
		ConfigTree nodeTwo = ConfigTree.builder()
				.withValue("A", cfgType, r2)
				.build();

		FiberSerialization.serialize(nodeOne, bos, jk);
		FiberSerialization.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()), jk);
		NodeOperationsTest.testNodeFor(nodeTwo, "A", cfgType.getSerializedType(), r);
		assertEquals("{ \"A\": { \"I\": 10, \"S\": \"hello\" } }", bos.toString("UTF-8"));
	}

	@Test
	@DisplayName("SubNode -> SubNode")
	void nodeSerialization1() throws IOException, FiberException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JanksonValueSerializer jk = new JanksonValueSerializer(true);
		ConfigTree nodeOne = ConfigTree.builder()
				.fork("child")
				.withValue("A", ConfigTypes.INTEGER, 10)
				.finishBranch()
				.build();

		ConfigTreeBuilder builderTwo = ConfigTree.builder();
		ConfigTree childTwo = builderTwo
				.fork("child")
				.withValue("A", ConfigTypes.INTEGER, 20)
				.build();
		ConfigTree nodeTwo = builderTwo.build();

		FiberSerialization.serialize(nodeOne, bos, jk);
		FiberSerialization.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()), jk);
		NodeOperationsTest.testNodeFor(childTwo, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.TEN);
		assertEquals("{ \"child\": { \"A\": 10 } }", bos.toString("UTF-8"));
	}

	@Test
	@DisplayName("Constraints")
	void nodeSerializationConstrains() throws IOException, FiberException {
		PropertyMirror<String> versionOne = PropertyMirror.create(ConfigTypes.STRING);
		PropertyMirror<Integer> settingOne = PropertyMirror.create(ConfigTypes.INTEGER);

		ConfigTree nodeOne = ConfigTree.builder()
				.beginValue("version", ConfigTypes.STRING.getSerializedType(), "0.1")
				.finishValue(versionOne::mirror)
				.fork("child")
				.beginValue("A", ConfigTypes.INTEGER, 30)
				.finishValue(settingOne::mirror)
				.finishBranch()
				.build();

		PropertyMirror<String> versionTwo = PropertyMirror.create(ConfigTypes.STRING);
		PropertyMirror<Integer> settingTwo = PropertyMirror.create(ConfigTypes.INTEGER);

		ConfigTree nodeTwo = ConfigTree.builder()
				.beginValue("version", ConfigTypes.STRING.withPattern("\\d+\\.\\d+\\.\\d+"), "1.0.0")
				.finishValue(versionTwo::mirror)
				.fork("child")
				.beginValue("A", INT_TYPE.withMinimum(-5).withMaximum(20), 20)
				.finishValue(settingTwo::mirror)
				.finishBranch()
				.build();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JanksonValueSerializer jk = new JanksonValueSerializer(true);

		FiberSerialization.serialize(nodeOne, bos, jk);
		FiberSerialization.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()), jk);
		assertEquals("1.0.0", versionTwo.getValue(), "RegEx constraint bypassed");
		assertEquals(20, settingTwo.getValue(), "Range constraint bypassed");
		assertEquals("{ \"version\": \"0.1\", \"child\": { \"A\": 30 } }", bos.toString("UTF-8"));

		bos.reset();

		versionOne.setValue("0.1.0");
		settingOne.setValue(-5);
		FiberSerialization.serialize(nodeOne, bos, jk);
		FiberSerialization.deserialize(nodeTwo, new ByteArrayInputStream(bos.toByteArray()), jk);
		assertEquals("0.1.0", versionTwo.getValue(), "Valid value rejected");
		assertEquals(-5, settingTwo.getValue(), "Valid value rejected");
		assertEquals("{ \"version\": \"0.1.0\", \"child\": { \"A\": -5 } }", bos.toString("UTF-8"));
	}

	@Test
	@DisplayName("Ignore SubNode")
	void nodeSerialization2() throws IOException, FiberException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JanksonValueSerializer jk = new JanksonValueSerializer(true);
		ConfigTree parentOne = ConfigTree.builder()
				.fork("child").withSeparateSerialization()
				.beginValue("A", INT_TYPE, 10)
				.finishValue()
				.finishBranch()
				.build();
		ConfigTreeBuilder builderTwo = ConfigTree.builder();
		ConfigTree childTwo = builderTwo.fork("child").withSeparateSerialization()
				.beginValue("A", ConfigTypes.INTEGER, 20)
				.finishValue()
				.build();
		ConfigTree parentTwo = builderTwo.build();

		FiberSerialization.serialize(parentOne, bos, jk);
		FiberSerialization.deserialize(parentTwo, new ByteArrayInputStream(bos.toByteArray()), jk);
		// the child data should not have been saved -> default value
		NodeOperationsTest.testNodeFor(childTwo, "A", ConfigTypes.INTEGER.getSerializedType(), BigDecimal.valueOf(20));
		assertEquals("{ }", bos.toString("UTF-8"));
	}
}
