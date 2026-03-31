package fr.univrennes.istic.l2gen.geometry;

import fr.univrennes.istic.l2gen.svg.attributes.style.SVGStyle;
import fr.univrennes.istic.l2gen.svg.attributes.transform.SVGTransform;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;

public abstract class AbstractShape implements IShape {

    @SVGField
    protected final SVGStyle style;

    @SVGField
    protected final SVGTransform transform;

    public AbstractShape() {
        this.style = new SVGStyle();
        this.transform = new SVGTransform();
    }

    public AbstractShape(SVGStyle style) {
        this.style = style;
        this.transform = new SVGTransform();
    }

    public AbstractShape(SVGTransform transform) {
        this.style = new SVGStyle();
        this.transform = transform;
    }

    @Override
    public String getDescription(int indent) {
        return " ".repeat(Math.max(0, indent)) + this.getClass().getSimpleName();
    }

    @Override
    public void rotate(double deg) {
        this.transform.rotate(deg, this.getCenter().getX(), this.getCenter().getY());
    }

    @Override
    public final SVGStyle getStyle() {
        return this.style;
    }

    @Override
    public final SVGTransform getTransform() {
        return this.transform;
    }
}
