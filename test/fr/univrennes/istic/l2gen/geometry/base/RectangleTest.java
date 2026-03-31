package fr.univrennes.istic.l2gen.geometry.base;

import fr.univrennes.istic.l2gen.geometry.Point;

import org.junit.Test;

import fr.univrennes.istic.l2gen.geometry.AbstractShapeTest;

public final class RectangleTest extends AbstractShapeTest<Rectangle> {

    @Override
    public Rectangle create() {
        return new Rectangle(500 - 100 / 2, 500 - 50 / 2, 100, 50);
    }

    @Test
    @Override
    public void testCenter() {
        Rectangle r = create();
        assert r.getCenter().equals(new Point(500, 500));
    }

    @Test
    @Override
    public void testMove() {
        Rectangle r = create();
        r.move(5, 5);
        assert r.getCenter().equals(new Point(505, 505));
    }

    @Test
    @Override
    public void testResize() {
        Rectangle r = create();
        r.resize(2, 2);

        assert r.getWidth() == 200;
        assert r.getHeight() == 100;

        r.resize(0.5, 0.5);
        assert r.getWidth() == 100;
        assert r.getHeight() == 50;
    }

    @Test
    @Override
    public void testDescription() {
        Rectangle r = create();
        assert r.getDescription(0).contains("Rectangle X=");
    }

    @Test
    @Override
    public void testWidth() {
        assert create().getWidth() == 100;
    }

    @Test
    @Override
    public void testHeight() {
        assert create().getHeight() == 50;
    }

}
