import me.zeroeightsix.fiber.annotations.Listener;
import me.zeroeightsix.fiber.annotations.Setting;
import me.zeroeightsix.fiber.ir.ConfigNode;
import me.zeroeightsix.fiber.ir.ConfigValue;
import me.zeroeightsix.fiber.annotations.PojoSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

class PojoTest {

    @Test
    @DisplayName("Convert POJO to IR")
    void testPojoIR() throws IllegalAccessException {
        ConfigNode node = new ConfigNode();
        OneFieldPojo pojo = new OneFieldPojo();
        PojoSettings.applyToIR(node, pojo);

        Map<String, ConfigValue> settingMap = node.getSettingsImmutable();
        assertEquals(1, settingMap.size(), "Setting map is 1 entry large");
        ConfigValue value = settingMap.get("a");
        assertNotNull(value, "Setting exists");
        assertNotNull(value.getValue(), "Setting value is non-null");
        assertEquals(Integer.class, value.getType(), "Setting type is correct");
        assertEquals(Integer.class, value.getValue().getClass(), "Setting value reflects correct type");
        Integer integer = (Integer) value.getValue();
        assertEquals(integer, 5);
    }

    @Test
    @DisplayName("Throw no final exception")
    void testNoFinal() {
        ConfigNode node = new ConfigNode();
        NoFinalPojo pojo = new NoFinalPojo();
        assertThrows(IllegalStateException.class, () -> PojoSettings.applyToIR(node, pojo));
    }

    @Test
    @DisplayName("Listener")
    void testListener() throws IllegalAccessException {
        ConfigNode node = new ConfigNode();
        ListenerPojo pojo = new ListenerPojo();
        PojoSettings.applyToIR(node, pojo);

        ConfigValue value = node.getSetting("a");
        assertNotNull(value, "Setting exists");
        value.setValue(10);
        assertEquals(true, pojo.listened);
    }

    @Test
    @DisplayName("Listener with different generics")
    void testTwoGenerics() {
        ConfigNode node = new ConfigNode();
        NonMatchingListenerPojo pojo = new NonMatchingListenerPojo();
        assertThrows(IllegalStateException.class, () -> PojoSettings.applyToIR(node, pojo));
    }

    @Test
    @DisplayName("Listener with wrong generic type")
    void testWrongGenerics() {
        ConfigNode node = new ConfigNode();
        WrongGenericListenerPojo pojo = new WrongGenericListenerPojo();
        assertThrows(IllegalStateException.class, () -> PojoSettings.applyToIR(node, pojo));
    }

    private static class NoFinalPojo {
        private int a = 5;
    }

    private static class OneFieldPojo {
        private final int a = 5;
    }

    private static class ListenerPojo {
        @Setting.Ignored
        private boolean listened = false;

        private final int a = 5;

        @Listener("a")
        private final BiConsumer<Integer, Integer> aListener = (now, then) -> listened = true;
    }

    private static class NonMatchingListenerPojo {
        private final int a = 5;

        @Listener("a")
        private final BiConsumer<Double, Integer> aListener = (now, then) -> {};
    }

    private static class WrongGenericListenerPojo {
        private final int a = 5;

        @Listener("a")
        private final BiConsumer<Double, Double> aListener = (now, then) -> {};
    }

}
