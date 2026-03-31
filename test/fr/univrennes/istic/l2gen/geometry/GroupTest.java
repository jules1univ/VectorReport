package fr.univrennes.istic.l2gen.geometry;

import fr.univrennes.istic.l2gen.geometry.base.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroupTest extends AbstractShapeTest<Group> {

    @Override
    public Group create() {
        return new Group();
    }

    @Test
    public void testAdd() {
        Group grp = create();

        grp.add(new Rectangle());
        grp.add(new Circle());
        grp.add(new Ellipse());
        grp.add(new Line());
        grp.add(new Polygon());
        grp.add(new PolyLine());
        grp.add(new Text());
        grp.add(new Triangle());
        grp.add(new Path());
        grp.add(new Point());

        assertEquals(10, grp.getElements().size());
    }

    @Test
    @Override
    public void testCenter() {
        assertEquals(new Point(0, 0), create().getCenter());
    }

    @Test
    @Override
    public void testMove() {
        Group grp = create();
        assertEquals(0, grp.getElements().size());

        grp.move(1.0, 1.0);
        assertEquals(0.0, grp.getCenter().getX(), 0.2);
        assertEquals(0.0, grp.getCenter().getY(), 0.2);
    }

    @Test
    @Override
    public void testResize() {
        Group grp = create();

        double initialWidth = grp.getWidth();
        grp.resize(-1.0, -1.0);
        assertEquals(initialWidth, grp.getWidth(), 0.1);
    }

    @Test
    @Override
    public void testDescription() {
        assertEquals(" Group", create().getDescription(1));
    }

    @Test
    public void testWidth() {
        Group grp = create();

        grp.add(new Circle(1.0, 1.0, 0.5));
        assertEquals(1.0, grp.getCenter().getWidth(), 0.1);
    }

    @Test
    public void testHeight() {
        Group grp = create();

        grp.add(new Circle(1.0, 1.0, 0.5));
        assertEquals(1.0, grp.getCenter().getHeight(), 0.1);
    }
}
