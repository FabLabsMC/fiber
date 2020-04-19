package me.zeroeightsix.fiber.api.annotation;

import me.zeroeightsix.fiber.api.builder.ConfigTreeBuilder;
import me.zeroeightsix.fiber.api.exception.FiberException;
import me.zeroeightsix.fiber.api.tree.ConfigLeaf;
import me.zeroeightsix.fiber.api.tree.ConfigNode;
import me.zeroeightsix.fiber.api.tree.ConfigTree;
import me.zeroeightsix.fiber.api.tree.Property;
import me.zeroeightsix.fiber.schema.ConfigTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

// TODO add tests for user-defined types and processors
@SuppressWarnings({"unused", "FieldMayBeFinal"})
class AnnotatedSettingsTest {

    private AnnotatedSettings annotatedSettings;
    private ConfigTreeBuilder node;

    @BeforeEach
    void setup() {
        this.annotatedSettings = new AnnotatedSettings();
        this.node = ConfigTree.builder();
    }

    @Test
    @DisplayName("Convert POJO to IR")
    void testPojoIR() throws FiberException {
        OneFieldPojo pojo = new OneFieldPojo();
        this.annotatedSettings.applyToNode(this.node, pojo);

        Collection<ConfigNode> items = this.node.build().getItems();
        assertEquals(1, items.size(), "Setting map is 1 entry large");
        ConfigNode item = this.node.lookup("a");
        assertNotNull(item, "Setting exists");
        assertTrue(ConfigLeaf.class.isAssignableFrom(item.getClass()), "Setting is a ConfigLeaf");
        ConfigLeaf<?, ?> leaf = (ConfigLeaf<?, ?>) item;
        assertNotNull(leaf.getValue(), "Setting value is non-null");
        assertEquals(ConfigTypes.INTEGER, leaf.getConfigType(), "Setting type is correct");
        assertEquals(Integer.class, leaf.getValue().getClass(), "Setting value reflects correct type");
        Integer integer = (Integer) leaf.getValue();
        assertEquals(integer, 5, "Setting value is correct");
    }

    @Test
    @DisplayName("Throw final exception")
    void testNoFinal() {
        FinalSettingPojo pojo = new FinalSettingPojo();
        assertThrows(FiberException.class, () -> this.annotatedSettings.applyToNode(this.node, pojo));
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Listener")
    void testListener() throws FiberException {
        ListenerPojo pojo = new ListenerPojo();
        this.annotatedSettings.applyToNode(this.node, pojo);

        ConfigNode treeItem = this.node.lookup("a");
        assertNotNull(treeItem, "Setting A exists");
        assertTrue(treeItem instanceof Property, "Setting A is a property");
        Property<Integer> property = (Property<Integer>) treeItem;
        property.setValue(10);
        assertTrue(pojo.listenedA, "Listener for A was triggered");

        treeItem = this.node.lookup("b");
        assertNotNull(treeItem, "Setting B exists");
        assertTrue(treeItem instanceof Property, "Setting B is a property");
        property = (Property<Integer>) treeItem;
        property.setValue(10);
        assertTrue(pojo.listenedB, "Listener for B was triggered");

        treeItem = this.node.lookup("c");
        assertNotNull(treeItem, "Setting C exists");
        assertTrue(treeItem instanceof Property, "Setting C is a property");
        property = (Property<Integer>) treeItem;
        property.setValue(10);
        assertTrue(pojo.listenedC, "Listener for C was triggered");
    }

    @Test
    @DisplayName("Listener with different generics")
    void testTwoGenerics() {
        NonMatchingListenerPojo pojo = new NonMatchingListenerPojo();
        assertThrows(FiberException.class, () -> this.annotatedSettings.applyToNode(this.node, pojo));
    }

    @Test
    @DisplayName("Listener with wrong generic type")
    void testWrongGenerics() {
        WrongGenericListenerPojo pojo = new WrongGenericListenerPojo();
        assertThrows(FiberException.class, () -> this.annotatedSettings.applyToNode(this.node, pojo));
    }

    @Test
    @DisplayName("Numerical constraints")
    void testNumericalConstraints() throws FiberException {
        NumericalConstraintsPojo pojo = new NumericalConstraintsPojo();
        this.annotatedSettings.applyToNode(this.node, pojo);
        @SuppressWarnings("unchecked")
        Property<Integer> value = (Property<Integer>) this.node.lookup("a");
        assertNotNull(value, "Setting exists");
        assertFalse(value.accepts(-10));
        assertTrue(value.setValue(-10));
        assertEquals(0, value.getValue());
        assertTrue(value.accepts(5));
        assertTrue(value.setValue(5));
        assertEquals(5, value.getValue());
        assertFalse(value.accepts(20));
        assertTrue(value.setValue(20));
        assertEquals(10, value.getValue());
    }

    @Test
    @DisplayName("String constraints")
    void testStringConstraints() throws FiberException {
        StringConstraintsPojo pojo = new StringConstraintsPojo();
        this.annotatedSettings.applyToNode(this.node, pojo);
        @SuppressWarnings("unchecked")
        Property<String> value = (Property<String>) this.node.lookup("a");
        assertNotNull(value, "Setting exists");
        assertFalse(value.setValue("BAD STRING::"));
        assertTrue(value.setValue("good:string"));
        assertFalse(value.setValue("b:s"), "Too short");
        assertFalse(value.setValue("bad_string:because_it_is_way_too_long"), "Too long");
    }

    @Test
    @DisplayName("Array constraints")
    void testArrayConstraints() throws FiberException {
        ArrayConstraintsPojo pojo = new ArrayConstraintsPojo();
        this.annotatedSettings.applyToNode(this.node, pojo);
        @SuppressWarnings("unchecked")
        Property<String[]> value1 = (Property<String[]>) this.node.lookup("nonEmptyArrayShortStrings");
        assertNotNull(value1, "Setting exists");
        assertTrue(value1.setValue(new String[]{"ab", "", "ba", ""}));
        assertFalse(value1.setValue(new String[0]), "Empty array");
        assertFalse(value1.setValue(new String[]{"aaaaaaaaaaaa"}), "Strings too long");
        @SuppressWarnings("unchecked")
        Property<int[]> value2 = (Property<int[]>) this.node.lookup("numbers");
        assertNotNull(value2, "Setting exists");
        assertTrue(value2.setValue(new int[]{3, 4, 5}));
        assertArrayEquals(new int[]{3, 4, 5}, value2.getValue());
        assertTrue(value2.setValue(new int[0]));
        assertArrayEquals(new int [0], value2.getValue());
        assertFalse(value2.setValue(new int[]{1, 2, 3, 4, 5, 6, 7}), "Too many elements");
        assertArrayEquals(new int[0], value2.getValue(), "Value should not change after unrecoverable setValue");
        assertFalse(value2.accepts(new int[]{-1, 0, 1}), "Negative number not allowed");
        assertTrue(value2.setValue(new int[]{-1, 0, 1}), "Correction for out of bounds numbers available");
        assertArrayEquals(new int[]{0, 0, 1}, value2.getValue(), "Negative number should be brought back into range");
        assertFalse(value2.accepts(new int[]{9, 10, 11}), "Numbers above 10 not allowed");
        assertTrue(value2.setValue(new int[]{9, 10, 11}), "Correction for out of bounds numbers available");
        assertArrayEquals(new int[]{9, 10, 10}, value2.getValue(), ">10 number should be brought back into range");
        @SuppressWarnings("unchecked")
        Property<List<String>> value3 = (Property<List<String>>) this.node.lookup("shortArrayIdStrings");
        assertNotNull(value3, "Setting exists");
        assertTrue(value3.setValue(Arrays.asList("a:b", "fabric:test")));
        assertTrue(value3.setValue(Collections.emptyList()));
        assertFalse(value3.setValue(Arrays.asList("a:b", "b:c", "c:d", "d:e")), "Too many elements");
        assertFalse(value3.setValue(Collections.singletonList("aaaaaaaaaaaa")), "Bad regex");
    }

    @Test
    @DisplayName("Invalid constraints")
    void testInvalidConstraints() {
        assertThrows(FiberException.class, () -> this.annotatedSettings.makeTree(new InvalidConstraintPojo()));
    }

    @Test
    @DisplayName("Only annotated fields")
    void testOnlyAnnotatedFields() throws FiberException {
        OnlyAnnotatedFieldsPojo pojo = new OnlyAnnotatedFieldsPojo();
        this.annotatedSettings.applyToNode(this.node, pojo);
        assertEquals(1, this.node.getItems().size(), "Node has one item");
    }

    @Test
    @DisplayName("Custom named setting")
    void testCustomNames() throws FiberException {
        CustomNamePojo pojo = new CustomNamePojo();
        this.annotatedSettings.applyToNode(this.node, pojo);
        assertNotNull(this.node.lookup("custom_name"), "Custom named setting exists");
    }

    @Test
    @DisplayName("Subnodes")
    void testSubNodes() throws FiberException {
        SubNodePojo pojo = new SubNodePojo();
        this.annotatedSettings.applyToNode(this.node, pojo);
        assertEquals(1, this.node.getItems().size(), "Node has one item");
        ConfigTree subnode = (ConfigTree) this.node.lookup("a");
        assertNotNull(subnode, "Subnode exists");
        assertEquals(1, subnode.getItems().size(), "Subnode has one item");
    }

    @Test
    @DisplayName("Commented setting")
    @SuppressWarnings("unchecked")
    void testComment() throws FiberException {
        CommentPojo pojo = new CommentPojo();
        this.annotatedSettings.applyToNode(this.node, pojo);
        assertEquals("comment", ((ConfigLeaf<Integer, ?>) Objects.requireNonNull(this.node.lookup("a"))).getComment(), "Comment exists and is correct");
    }

    @Test
    @DisplayName("Ignored settings")
    void testIgnore() throws FiberException {
        IgnoredPojo pojo = new IgnoredPojo();
        this.annotatedSettings.applyToNode(this.node, pojo);
        assertEquals(0, this.node.getItems().size(), "Node is empty");
    }

    private static class FinalSettingPojo {
        private final int a = 5;
    }

    private static class OneFieldPojo {
        private int a = 5;
    }

    private static class ListenerPojo {
        private transient boolean listenedA = false;
        private transient boolean listenedB = false;
        private transient boolean listenedC = false;

        private int a = 5;
        private int b = 5;
        private int c = 5;

        @Listener("a")
        private transient BiConsumer<Integer, Integer> aListener = (now, then) -> this.listenedA = true;

        @Listener("b")
        private void bListener(Integer oldValue, Integer newValue) {
            this.listenedB = true;
        }

        @Listener("c")
        private void cListener(Integer newValue) {
            this.listenedC = true;
        }
    }

    private static class NonMatchingListenerPojo {
        private int a = 5;

        @Listener("a")
        private BiConsumer<Double, Integer> aListener = (now, then) -> {};
    }

    private static class WrongGenericListenerPojo {
        private int a = 5;

        @Listener("a")
        private BiConsumer<Double, Double> aListener = (now, then) -> {};
    }

    private static class NumericalConstraintsPojo {
        @Setting.Constrain.Range(min = 0, max = 10)
        private int a = 5;
    }

    private static class StringConstraintsPojo {
        @Setting.Constrain.MinLength(5)
        @Setting.Constrain.MaxLength(20)
        @Setting.Constrain.Regex("[a-z0-9_.-]{2,}:[a-z0-9_./-]+?")
        private String a = "fabric:test";
    }

    private static class ArrayConstraintsPojo {

        private
        @Setting.Constrain.MaxLength(2) String
        @Setting.Constrain.MinLength(1) [] nonEmptyArrayShortStrings = {""};

        private
        @Setting.Constrain.Range(min = 0, max = 10) int
        @Setting.Constrain.MinLength(0) @Setting.Constrain.MaxLength(3)[] numbers = {};

        private @Setting.Constrain.MaxLength(3) List<@Setting.Constrain.Regex("\\w+:\\w+") String> shortArrayIdStrings = Collections.singletonList("fabric:test");
    }

    private static class InvalidConstraintPojo {
        private @Setting.Constrain.Regex("\\d") int i;
    }

    @Settings(onlyAnnotated = true)
    private static class OnlyAnnotatedFieldsPojo {
        @Setting
        private int a = 5;

        private int b = 6;
    }

    private static class CustomNamePojo {
        @Setting(name = "custom_name")
        private int a = 5;
    }

    private static class CommentPojo {
        @Setting(comment = "comment")
        private int a = 5;
    }

    private static class IgnoredPojo {
        @Setting(ignore = true)
        private int a = 5;

        private transient int b = 5;
    }

    private static class SubNodePojo {
        @Setting.Group(name = "a")
        public SubNode node = new SubNode();

        @SuppressWarnings("InnerClassMayBeStatic")  // we want to test this edge case
        class SubNode {
            private int b = 5;
        }
    }

}
