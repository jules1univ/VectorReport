package fr.univrennes.istic.l2gen.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PathTest extends AbstractShapeTest<Path> {

    @Override
    public Path create() {
        Path path = new Path();
        path.draw()
                .move(40, 100, false)
                .arc(60, 60, 0, false, true, 160, 100, false)
                .arc(60, 60, 0, false, true, 40, 100, false)
                .close()

                .move(75, 85, false)
                .arc(6, 8, 0, false, true, 87, 85, false)
                .arc(6, 8, 0, false, true, 75, 85, false)
                .close()

                .move(113, 85, false)
                .arc(6, 8, 0, false, true, 125, 85, false)
                .arc(6, 8, 0, false, true, 113, 85, false)
                .close()

                .move(100, 95, false)
                .line(100, 105, false)
                .line(110, 105, false)
                .line(110, 95, false)
                .close()

                .move(75, 115, false)
                .cubicBezier(80, 140, 120, 140, 125, 115, false)
                .close();
        path.moveCenter(new Point(500, 500));
        return path;
    }

    @Test
    @Override
    public void testWidth() {
        Path path = create();
        assertEquals(120, path.getWidth(), 1.0);

        Path empty = new Path();
        assertEquals(0, empty.getWidth(), 0.001);

        Path simple = new Path();
        simple.draw().move(0, 0, false).line(10, 0, false);
        assertEquals(10, simple.getWidth(), 0.001);
    }

    @Test
    @Override
    public void testHeight() {
        Path path = create();
        assertEquals(120, path.getHeight(), 1.0);

        Path empty = new Path();
        assertEquals(0, empty.getHeight(), 0.001);

        Path simple = new Path();
        simple.draw().move(0, 0, false).line(0, 10, false);
        assertEquals(10, simple.getHeight(), 0.001);
    }

    @Test
    @Override
    public void testCenter() {
        Path empty = create();
        empty.draw().reset();
        assertEquals(new Point(0, 0), empty.getCenter());

        Path simple = new Path();
        simple.draw()
                .move(0, 0, false)
                .line(10, 0, false)
                .line(10, 10, false)
                .line(0, 10, false)
                .close();
        assertEquals(new Point(5, 5), simple.getCenter());

        Path path = create();
        Point c = path.getCenter();

        assertNotNull(c);
        assertEquals(500, c.getX(), 2.0);
        assertEquals(500, c.getY(), 2.0);
    }

    @Test
    @Override
    public void testMove() {
        Path path = new Path();
        path.draw()
                .move(0, 0, false)
                .line(3, 0, false)
                .line(0, 4, false);

        Point before = path.getCenter();
        double cx = before.getX();
        double cy = before.getY();

        path.move(5, 5);
        assertEquals(new Point(cx + 5, cy + 5), path.getCenter());

        path.move(-10, -15);
        assertEquals(new Point(cx - 5, cy - 10), path.getCenter());
    }

    @Test
    @Override
    public void testResize() {
        Path path = new Path();
        path.draw().move(0, 0, false).line(3, 0, false).line(0, 4, false);

        path.resize(2, 3);
        String content = path.getTransform().getContent();
        assertTrue(content.contains("scale("));
        assertTrue(content.contains("2") || content.contains("3"));

        Path p2 = new Path();
        p2.resize(1, 1);
        assertNotNull(p2.getTransform());
    }

    @Test
    @Override
    public void testDescription() {
        Path path = new Path();
        String expected = "Path D=" + path.draw().getContent();
        assertEquals(expected, path.getDescription(0));
    }

    @Test
    public void testBoundingBoxMoveAndLine() {

        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .line(10, 0, false)
                .line(10, 10, false)
                .line(0, 10, false)
                .close();

        assertEquals(10, p.getWidth(), 0.001);
        assertEquals(10, p.getHeight(), 0.001);
        assertEquals(new Point(5, 5), p.getCenter());
    }

    @Test
    public void testBoundingBoxRelativeLine() {

        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .line(10, 0, true)
                .line(0, 10, true)
                .line(-10, 0, true)
                .close();

        assertEquals(10, p.getWidth(), 0.001);
        assertEquals(10, p.getHeight(), 0.001);
    }

    @Test
    public void testBoundingBoxRelativeMove() {

        Path p = new Path();
        p.draw()
                .move(5, 5, true)
                .line(15, 5, false)
                .line(15, 15, false)
                .line(5, 15, false)
                .close();

        assertEquals(10, p.getWidth(), 0.001);
        assertEquals(10, p.getHeight(), 0.001);
        assertEquals(new Point(10, 10), p.getCenter());
    }

    @Test
    public void testBoundingBoxHorizontalVertical() {

        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .horizontal(10, false)
                .vertical(10, false)
                .horizontal(0, false)
                .close();

        assertEquals(10, p.getWidth(), 0.001);
        assertEquals(10, p.getHeight(), 0.001);
        assertEquals(new Point(5, 5), p.getCenter());
    }

    @Test
    public void testBoundingBoxHorizontalVerticalRelative() {
        Path p = new Path();
        p.draw()
                .move(2, 2, false)
                .horizontal(8, true)
                .vertical(8, true)
                .horizontal(-8, true)
                .close();

        assertEquals(8, p.getWidth(), 0.001);
        assertEquals(8, p.getHeight(), 0.001);
        assertEquals(new Point(6, 6), p.getCenter());
    }

    @Test
    public void testBoundingBoxCubicBezier() {

        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .cubicBezier(0, 100, 100, 100, 100, 0, false);

        assertEquals(100, p.getWidth(), 0.001);

        assertTrue("Height should be less than 100 (control points are not on the curve)",
                p.getHeight() < 100);
        assertTrue("Height should be around 75", p.getHeight() > 70 && p.getHeight() < 80);
    }

    @Test
    public void testBoundingBoxCubicBezierRelative() {

        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .cubicBezier(0, 100, 100, 100, 100, 0, true);

        assertEquals(100, p.getWidth(), 0.001);
        assertTrue(p.getHeight() < 100);
        assertTrue(p.getHeight() > 70 && p.getHeight() < 80);
    }

    @Test
    public void testBoundingBoxSmoothCubicBezier() {
        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .cubicBezier(0, 50, 50, 50, 50, 0, false)
                .cubicBezierSmooth(100, 50, 100, 0, false);

        assertEquals(100, p.getWidth(), 0.001);

        assertTrue("Smooth cubic height must be <= 100", p.getHeight() <= 100);
        assertTrue("Smooth cubic height must be > 50", p.getHeight() > 50);
    }

    @Test
    public void testBoundingBoxQuadBezier() {

        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .quadBezier(50, 100, 100, 0, false);

        assertEquals(100, p.getWidth(), 0.001);
        assertEquals(50, p.getHeight(), 0.001);
    }

    @Test
    public void testBoundingBoxQuadBezierRelative() {
        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .quadBezier(50, 100, 100, 0, true);

        assertEquals(100, p.getWidth(), 0.001);
        assertEquals(50, p.getHeight(), 0.001);
    }

    @Test
    public void testBoundingBoxSmoothQuadBezier() {

        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .quadBezier(25, 50, 50, 0, false)
                .quadBezierSmooth(100, 0, false);

        assertEquals(100, p.getWidth(), 0.001);

        assertTrue("Should have positive height", p.getHeight() > 0);
        assertTrue("minY should be negative (smooth quad dips below start)",
                p.getCenter().getY() < 25);
    }

    @Test
    public void testBoundingBoxArc() {

        Path p = new Path();
        p.draw()
                .move(0, 50, false)
                .arc(50, 50, 0, false, true, 100, 50, false)
                .arc(50, 50, 0, false, true, 0, 50, false)
                .close();

        assertEquals(100, p.getWidth(), 1.0);
        assertEquals(100, p.getHeight(), 1.0);
        assertEquals(50, p.getCenter().getX(), 1.0);
        assertEquals(50, p.getCenter().getY(), 1.0);
    }

    @Test
    public void testBoundingBoxArcRelative() {

        Path p = new Path();
        p.draw()
                .move(0, 50, false)
                .arc(50, 50, 0, false, true, 100, 0, true)
                .arc(50, 50, 0, false, true, -100, 0, true)
                .close();

        assertEquals(100, p.getWidth(), 1.0);
        assertEquals(100, p.getHeight(), 1.0);
    }

    @Test
    public void testBoundingBoxArcQuarterCircle() {

        Path p = new Path();
        p.draw()
                .move(100, 0, false)
                .arc(100, 100, 0, false, false, 0, 100, false);

        assertEquals(100, p.getWidth(), 1.0);
        assertEquals(100, p.getHeight(), 1.0);
    }

    @Test
    public void testBoundingBoxRotatedArc() {

        Path p = new Path();
        p.draw()
                .move(50, 0, false)
                .arc(60, 30, 45, false, true, 0, 50, false);

        assertTrue("Width should be > 0", p.getWidth() > 0);
        assertTrue("Height should be > 0", p.getHeight() > 0);

        assertTrue("Width must be >= 50", p.getWidth() >= 50 - 1.0);
        assertTrue("Height must be >= 50", p.getHeight() >= 50 - 1.0);
    }

    @Test
    public void testEmptyPath() {
        Path emptyPath = new Path();
        assertNotNull(emptyPath.draw());
        assertEquals(0, emptyPath.getWidth(), 0.001);
        assertEquals(0, emptyPath.getHeight(), 0.001);
        assertEquals(new Point(0, 0), emptyPath.getCenter());
        assertNotNull(emptyPath.getDescription(0));
    }

    @Test
    public void testComplexPath() {
        Path path = new Path();
        path.draw()
                .move(0, 0, false)
                .line(10, 0, false)
                .line(10, 10, false)
                .line(0, 10, false)
                .close();

        assertEquals(10, path.getWidth(), 0.001);
        assertEquals(10, path.getHeight(), 0.001);
        assertEquals(new Point(5, 5), path.getCenter());
        assertNotNull(path.getDescription(0));
    }

    @Test
    public void testResetClearsBoundingBogetX() {
        Path p = new Path();
        p.draw()
                .move(0, 0, false)
                .line(100, 100, false);

        assertTrue(p.getWidth() > 0);

        p.draw().reset();

        assertEquals(0, p.getWidth(), 0.001);
        assertEquals(0, p.getHeight(), 0.001);
        assertEquals(new Point(0, 0), p.getCenter());
    }

}