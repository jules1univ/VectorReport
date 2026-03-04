package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Group;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;
import fr.univrennes.istic.l2gen.visustats.data.Label;

@SVGTag("g")
public abstract class AbstractDataGroupView extends Group implements IDataGroupView {

    protected DataGroup data;

    @SVGField("data-spacing")
    protected double spacing;

    @SVGField({ "data-center-x", "data-center-y" })
    protected Point center;

    public AbstractDataGroupView(DataGroup data, Point center, double spacing) {
        this.data = data;
        this.center = center;
        this.spacing = spacing;
    }

    protected abstract void update();

    @Override
    public final void setTitle(Label title) {
        this.data = new DataGroup(this.data.datasets(), title, this.data.legends());
        this.update();
    }

    @Override
    public final void setData(DataGroup group) {
        this.data = group;
        this.update();
    }

    @Override
    public final void addData(DataSet data) {
        this.data.add(data);
        this.update();
    }

    @Override
    public final void addLegend(Label legend) {
        this.data.add(legend);
        this.update();
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
