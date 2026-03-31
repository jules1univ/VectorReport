package fr.univrennes.istic.l2gen.geometry.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.univrennes.istic.l2gen.geometry.AbstractShapeTest;

public class CircleTest extends AbstractShapeTest<Circle> {

    /**
     * @return Circle
     */
    @Override
    public Circle create() {
        return new Circle(500, 500, 100);
    }

    @Test
    @Override
    public void testCenter() {
        Circle circle = create();
        assertTrue(circle.getCenter().getX() == 500);
        assertTrue(circle.getCenter().getY() == 500);
    }

    @Test
    @Override
    public void testMove() {
        Circle circle = create();
        circle.move(1, 1);
        assertEquals(501, circle.getCenter().getX(), 0.0001);
        assertEquals(501, circle.getCenter().getY(), 0.0001);
    }

    @Test
    @Override
    public void testResize() {
        Circle circle = create();
        circle.resize(2, 0);
        assertEquals(circle.getHeight(), 400, 0.0001);
    }

    @Test
    @Override
    public void testDescription() {
        Circle circle = create();
        assertTrue("Circle C=500,500 R=100.0".compareTo(circle.getDescription(0)) == 0);
    }

    @Test
    @Override
    public void testWidth() {
        Circle circle = create();
        assertTrue(circle.getWidth() == 200);
    }

    @Test
    @Override
    public void testHeight() {
        Circle circle = create();
        assertTrue(circle.getWidth() == circle.getHeight());
    }
}
