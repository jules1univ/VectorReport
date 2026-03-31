package fr.univrennes.istic.l2gen.geometry.base;

import org.junit.Test;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.AbstractShapeTest;

public class EllipseTest extends AbstractShapeTest<Ellipse> {

    @Override
    public Ellipse create() {
        return new Ellipse(500.0, 500.0, 30.0, 40.0);
    }

    @Test
    @Override
    public void testCenter() {
        assert create().getCenter().equals(new Point(500.0, 500.0));
    }

    @Test
    @Override
    public void testMove() {
        Ellipse ellipse = create();
        ellipse.move(10.0, 10.0);
        assert ellipse.getCenter().equals(new Point(510.0, 510.0));
    }

    @Test
    @Override
    public void testResize() {
        Ellipse ellipse = create();
        ellipse.resize(2.0, 2.0);
        assert ellipse.getWidth() == 60.0 * 2;
        assert ellipse.getHeight() == 80.0 * 2;
    }

    @Test
    @Override
    public void testDescription() {
        assert create().getDescription(0).contains("RX=30.0");
    }

    @Test
    @Override
    public void testWidth() {
        assert create().getWidth() == 60.0;
    }

    @Test
    @Override
    public void testHeight() {
        assert create().getHeight() == 80.0;
    }

}
