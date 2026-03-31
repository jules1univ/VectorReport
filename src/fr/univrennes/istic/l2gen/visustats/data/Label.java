package fr.univrennes.istic.l2gen.visustats.data;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.base.Text;
import fr.univrennes.istic.l2gen.svg.color.Color;

public record Label(String name, Color color) {
    public Label(String name) {
        this(name, Color.BLACK);
    }

    public Text createText(Point position) {
        Text text = new Text(position.getX(), position.getY(), name);
        text.getStyle()
                .fontFamily("Arial")
                .fontSize(12)
                .textAnchor("middle");
        return text;
    }

    public Text createTitle(Point position) {
        Text title = new Text(position.getX(), position.getY(), name);
        title.getStyle()
                .fontFamily("Arial")
                .fontSize(22)
                .textAnchor("middle");
        return title;
    }
}
