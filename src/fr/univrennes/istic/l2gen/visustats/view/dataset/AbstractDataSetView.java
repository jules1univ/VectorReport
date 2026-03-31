package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.Group;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

@SVGTag("g")
public abstract class AbstractDataSetView extends Group implements IDataSetView {

    @SVGField({ "data-x", "data-y" })
    protected Point center;

    public AbstractDataSetView(Point center) {
        this.center = center;
    }

    @Override
    public void setData(DataSet data) {
        this.elements.clear();
    }

    @Override
    public final Point getCenter() {
        return this.center;
    }

    public final void move(double dx, double dy) {
        // this.transform.translate(dx, dy);

        this.center.add(dx, dy);
        for (IShape element : this.elements) {
            element.move(dx, dy);
        }
    }

    @Override
    public final void resize(double px, double py) {
        this.transform.scale(px, py);
    }

    @Override
    public final void rotate(double deg) {
        this.transform.rotate(deg, this.getCenter().getX(), this.getCenter().getY());
    }
}
