package fr.univrennes.istic.l2gen.geometry;

import org.junit.Test;

public class PointTest extends AbstractShapeTest<Point> {

    @Override
    public Point create() {
        Point point = new Point(5, 10);
        return point;
    }

    @Test
    @Override
    public void testCenter() {
        Point point = create();
        assert point.getX() == 5;
        assert point.getY() == 10;
    }

    @Test
    @Override
    public void testMove() {
        Point point = create();
        point.move(2, 3);
        assert point.getX() == 7;
        assert point.getY() == 13;
    }

    @Test
    @Override
    public void testResize() {
        Point point = create();
        point.resize(2, 3);
        assert point.getX() == 10;
        assert point.getY() == 30;
    }

    @Test
    @Override
    public void testDescription() {
        Point point = create();
        String description = point.getDescription(0);
        assert description.contains("Point");
        assert description.contains("5,10");
    }

    @Test
    @Override
    public void testSVG() {
        // Ne rien faire car point n'est pas un element SVG
    }

    @Test
    @Override
    public void testWidth() {
        // Ne rien faire car point n'est pas un element SVG
    }

    @Test
    @Override
    public void testHeight() {
        // Ne rien faire car point n'est pas un element SVG
    }
}
