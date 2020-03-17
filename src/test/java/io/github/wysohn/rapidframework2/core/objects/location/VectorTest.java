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

    @Test
    public void add() {
        Vector v = new Vector(3, 4, 5);
        Vector u = new Vector(1.5, 3, 4);

        assertEquals(new Vector(4.5, 7, 9), v.add(u));
    }

    @Test
    public void testAdd() {
        Vector v = new Vector(3, 4, 5);

        assertEquals(new Vector(5, 10, 11.3), v.add(2, 6, 6.3));
    }

    @Test
    public void mult() {
        Vector v = new Vector(3, 4, 5);
        Vector u = new Vector(1.5, 3, 4);

        assertEquals(new Vector(4.5, 12, 20), v.mult(u));
    }

    @Test
    public void testMult() {
        Vector v = new Vector(3, 4, 5);

        assertEquals(new Vector(5, 10, 11.3), v.mult(5 / 3.0, 5 / 2.0, 11.3 / 5.0));
    }

    @Test
    public void testMult1() {
        Vector v = new Vector(3, 4, 5);

        assertEquals(new Vector(24, 32, 40), v.mult(8));
    }
}