package io.github.wysohn.rapidframework4.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NMSWrapperTest {

    @Test
    public void invoke() throws Exception {
        assertEquals(12.0, NMSWrapper.target(Test1.class)
                        .prepare("iamstatic", int.class, double.class, double.class)
                        .invoke(3, 4.0, 5.0)
                        .result()
                        .map(Double.class::cast)
                        .orElseThrow(RuntimeException::new),
                     0.000001);

        ItemStack itemStack = new ItemStack(Material.DIAMOND);
        assertEquals(99.84, NMSWrapper.target(Test1.class)
                        .prepare("iamstatic2", ItemStack.class)
                        .invoke(itemStack)
                        .result()
                        .map(Double.class::cast)
                        .orElseThrow(RuntimeException::new),
                0.000001);

        Test1 test1 = new Test1();
        assertEquals("invoked", NMSWrapper.target(test1)
                .prepare("chained")
                .invoke()
                .prepare("immethod")
                .invoke()
                .result()
                .map(String.class::cast)
                .orElseThrow(RuntimeException::new));
    }

    @Test
    public void get() throws Exception {
        Test1 test1 = new Test1();
        assertEquals("i am field", NMSWrapper.target(test1).get("test"));
    }

    public static class Test1 {
        private final String test = "i am field";

        private static double iamstatic(int a, double b, double c) {
            return a + b + c;
        }

        private static double iamstatic2(ItemStack itemStack) {
            return 99.84;
        }

        private Test2 chained() {
            return new Test2();
        }
    }

    public static class Test2 {
        private String immethod() {
            return "invoked";
        }
    }
}