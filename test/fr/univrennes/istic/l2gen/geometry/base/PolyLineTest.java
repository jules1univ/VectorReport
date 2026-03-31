package fr.univrennes.istic.l2gen.geometry.base;

import org.junit.Test;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.AbstractShapeTest;

import static org.junit.Assert.assertEquals;

public class PolyLineTest extends AbstractShapeTest<PolyLine> {

    @Override
    public PolyLine create() {
        return new PolyLine(490, 490, 510, 490, 505, 510, 490, 510);
    }

    @Test
    @Override
    public void testCenter() {
        PolyLine line = create();
        Point center = line.getCenter();
        Point centreLogique = new Point((490 + 510 + 505 + 490) / 4.0, (490 + 490 + 510 + 510) / 4.0);
        assertEquals(centreLogique.getX(), center.getX(), 0.0001);
        assertEquals(centreLogique.getY(), center.getY(), 0.0001);
    }

    @Test
    @Override
    public void testMove() {
        PolyLine line = create();
        PolyLine lineAfterMove = new PolyLine(492, 493, 512, 493, 507, 513, 492, 513);
        line.move(2, 3);
        assertEquals(lineAfterMove.getDescription(0), line.getDescription(0));
    }

    @Test
    @Override
    public void testResize() {
        PolyLine line = create();
        line.resize(2, 2);
        assert line.getWidth() == 20 * 2;
        assert line.getHeight() == 20 * 2;
    }

    @Test
    @Override
    public void testDescription() {
        PolyLine line = create();
        String expectedDescription = "PolyLine POINTS=490.0,490.0 510.0,490.0 505.0,510.0 490.0,510.0 ";
        assertEquals(expectedDescription, line.getDescription(0));
    }

    @Test
    @Override
    public void testWidth() {
        assert create().getWidth() == 20;
    }

    @Test
    @Override
    public void testHeight() {
        assert create().getHeight() == 20;
    }

}
