package fr.univrennes.istic.l2gen.geometry.base;

import org.junit.Test;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.AbstractShapeTest;

public final class PolygonTest extends AbstractShapeTest<Polygon> {

    @Override
    public Polygon create() {
        return new Polygon(450.0, 450.0, 550.0, 450.0, 550.0, 550.0, 450.0, 550.0);
    }

    @Test
    @Override
    public void testCenter() {
        Polygon poly = new Polygon(1.0, 5.0, 7.0, 60.0);
        Polygon polyVide = new Polygon();

        assert polyVide.getCenter().equals(new Point(0.0, 0.0));
        assert poly.getCenter().equals(new Point(8.0 / 2, 65.0 / 2));
    }

    @Test
    @Override
    public void testMove() {
        Polygon poly = new Polygon(1.0, 2.0);
        Polygon polyAfterMove = new Polygon(3.0, 4.0);
        poly.move(2.0, 2.0);

        assert poly.getCenter().equals(polyAfterMove.getCenter());
    }

    @Test
    @Override
    public void testResize() {
        Polygon poly = new Polygon(20.0, 30.0);
        poly.resize(10.0, 20.0);
        assert poly != null;
    }

    @Test
    @Override
    public void testDescription() {
        Polygon poly = new Polygon();
        assert poly.getDescription(0).equals("Polygon POINTS=");

        poly = new Polygon(1.0, 5.0, 6.0, 7.0);
        assert poly.getDescription(0).equals("Polygon POINTS=1.0,5.0 6.0,7.0 ");
    }

    @Test
    @Override
    public void testWidth() {
        Polygon poly = new Polygon(450.0, 450.0, 550.0, 450.0, 550.0, 550.0, 450.0, 550.0);
        assert poly.getWidth() == 100.0;
    }

    @Test
    @Override
    public void testHeight() {
        Polygon poly = new Polygon(450.0, 450.0, 550.0, 450.0, 550.0, 550.0, 450.0, 550.0);
        assert poly.getHeight() == 100.0;
    }
}
