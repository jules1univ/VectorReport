package fr.univrennes.istic.l2gen.geometry.base;

import org.junit.Test;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.AbstractShapeTest;

import static org.junit.Assert.assertEquals;

import java.util.List;

public class TriangleTest extends AbstractShapeTest<Triangle> {

    @Override
    public Triangle create() {
        return new Triangle(480, 480, 520, 480, 500, 540);
    }

    @Test
    @Override
    public void testCenter() {
        Triangle t1 = create();
        Point center = t1.getCenter();
        assertEquals(500.0, center.getX(), 0.0001);
        assertEquals(500.0, center.getY(), 0.0001);
    }

    @Test
    @Override
    public void testMove() {
        double MOVE_X = 50.0;
        double MOVE_Y = -50.0;

        Triangle t1 = create();
        t1.move(MOVE_X, MOVE_Y);

        Point center = t1.getCenter();
        assertEquals(550.0, center.getX(), 0.0001);
        assertEquals(450.0, center.getY(), 0.0001);
    }

    @Test
    @Override
    public void testResize() {
        Triangle t1 = create();
        List<Point> originalVertices = t1.getVertices();
        double[] originalX = new double[originalVertices.size()];
        double[] originalY = new double[originalVertices.size()];
        for (int i = 0; i < originalVertices.size(); i++) {
            originalX[i] = originalVertices.get(i).getX();
            originalY[i] = originalVertices.get(i).getY();
        }
        t1.resize(2, 0);
        List<Point> resizedVertices = t1.getVertices();
        for (int i = 0; i < resizedVertices.size(); i++) {
            assertEquals(
                    t1.getCenter().getX() + (originalX[i] - t1.getCenter().getX()) * 2,
                    resizedVertices.get(i).getX(), 0.0001);
            assertEquals(
                    t1.getCenter().getY() + (originalY[i] - t1.getCenter().getY()) * 0,
                    resizedVertices.get(i).getY(), 0.0001);
        }
    }

    @Test
    @Override
    public void testDescription() {
        Triangle triangle = create();
        String expected = " TrianglePOINTS= 480.0,480.0 520.0,480.0 500.0,540.0";
        String desc = triangle.getDescription(1);
        assertEquals(expected, desc);
    }

    @Test
    @Override
    public void testWidth() {
        Triangle triangle = create();
        assertEquals(1.0, triangle.getCenter().getWidth(), 0.0001);
    }

    @Test
    @Override
    public void testHeight() {
        Triangle triangle = create();
        assertEquals(1.0, triangle.getCenter().getHeight(), 0.0001);
    }

}
