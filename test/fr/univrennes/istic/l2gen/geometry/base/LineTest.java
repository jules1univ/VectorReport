package fr.univrennes.istic.l2gen.geometry.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.AbstractShapeTest;

public class LineTest extends AbstractShapeTest<Line> {

    @Override
    public Line create() {
        return new Line(400, 400, 600, 600);
    }

    @Test
    @Override
    public void testCenter() {
        assert create().getCenter().equals(new Point(500, 500));
    }

    @Test
    @Override
    public void testMove() {
        Line line = create();
        line.move(5, 5);
        assert line.getStart().equals(new Point(405, 405));
        assert line.getEnd().equals(new Point(605, 605));
    }

    @Test
    @Override
    public void testResize() {
        Line line = create();
        line.resize(2, 2);
        assertEquals(line.getWidth(), 400, 0.0001);
        assertEquals(line.getHeight(), 400, 0.0001);
    }

    @Test
    @Override
    public void testDescription() {
        assert create().getDescription(0).contains("Y1=400.0");
    }

    @Test
    @Override
    public void testWidth() {
        assert create().getWidth() == 200;
    }

    @Test
    @Override
    public void testHeight() {
        assert create().getHeight() == 200;
    }

}
