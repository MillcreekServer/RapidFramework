package io.github.wysohn.rapidframework2.core.objects.location;

import org.junit.Test;

import static org.junit.Assert.*;

public class VectorTest {

    @Test
    public void unit() {
        Vector v = new Vector(1, 2, 3).unit();
        double d = Math.sqrt(1 + 2 * 2 + 3 * 3);
        assertEquals(new Vector(1 / d, 2 / d, 3 / d), v);
    }

    @Test
    public void unit2() {
        Vector v = new Vector(-3, -2, -1).unit();
        double d = Math.sqrt(3 * 3 + 2 * 2 + 1);
        assertEquals(new Vector(-3 / d, -2 / d, -1 / d), v);
    }

    @Test
    public void unit3() {
        Vector v = new Vector(1, -2, 3).unit();
        double d = Math.sqrt(1 + 2 * 2 + 3 * 3);
        assertEquals(new Vector(1 / d, -2 / d, 3 / d), v);
    }

    @Test
    public void unit4() {
        Vector v = new Vector(-3, 2, -1).unit();
        double d = Math.sqrt(3 * 3 + 2 * 2 + 1);
        assertEquals(new Vector(-3 / d, 2 / d, -1 / d), v);
    }

    @Test(expected = RuntimeException.class)
    public void unit5() {
        Vector.zero().unit();
        fail();
    }

    @Test
    public void isZero() {
        Vector v = Vector.zero();

        assertTrue(v.isZero());
    }

    @Test
    public void zero() {
        Vector v = Vector.zero();

        assertEquals(new Vector(0, 0, 0), v);
    }
}