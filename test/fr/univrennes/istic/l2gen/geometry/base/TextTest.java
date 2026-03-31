package fr.univrennes.istic.l2gen.geometry.base;

import org.junit.Test;

import fr.univrennes.istic.l2gen.geometry.AbstractShapeTest;

public class TextTest extends AbstractShapeTest<Text> {

    @Override
    public Text create() {
        Text text = new Text(500, 500, "Hello World");
        text.getStyle().alignmentBaseline("middle");
        text.getStyle().textAnchor("middle");
        return text;
    }

    @Test
    @Override
    public void testCenter() {
        Text text = create();
        assert text.getCenter().getX() == 500;
        assert text.getCenter().getY() == 500;
    }

    @Test
    @Override
    public void testMove() {
        Text text = create();
        text.move(10, 15);
        assert text.getCenter().getX() == 510;
        assert text.getCenter().getY() == 515;
    }

    @Test
    @Override
    public void testResize() {
        Text text = create();
        text.resize(200, 50);

        assert text.getWidth() != 200;
        assert text.getHeight() != 50;
    }

    @Test
    @Override
    public void testDescription() {
        Text text = create();
        String desc = text.getDescription(1);
        assert desc.contains("Text");
        assert desc.contains("Hello World");
    }

    @Test
    @Override
    public void testWidth() {
        // Ne pas tester car la largeur d'un texte dépend de la police
    }

    @Test
    @Override
    public void testHeight() {
        // Ne pas tester car la hauteur d'un texte dépend de la police
    }

}
