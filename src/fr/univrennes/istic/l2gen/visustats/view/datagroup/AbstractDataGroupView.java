package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Group;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;
import fr.univrennes.istic.l2gen.visustats.data.Label;
import fr.univrennes.istic.l2gen.visustats.view.dataset.IDataSetView;

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

    protected abstract double getTotalHeight();

    private double getTotalWidth() {
        return this.getElementWidth() * this.data.size() + this.spacing * Math.max(0, this.data.size() - 1);
    }

    protected abstract boolean isAxisEnabled();

    protected abstract double getElementWidth();

    protected abstract IShape getAxisElement();

    protected abstract IDataSetView createElement(Point position);

    private Point getElementCenterAt(int index) {
        double startX = center.getX() - (this.getTotalWidth() / 2);
        double centerX = startX + index * (this.getElementWidth() + this.spacing) + this.getElementWidth() / 2;

        return new Point(centerX, this.center.getY());
    }

    protected final void update() {
        this.elements.clear();

        if (this.data.size() == 0) {
            return;
        }

        for (int i = 0; i < this.data.size(); i++) {
            IDataSetView element = createElement(getElementCenterAt(i));
            element.setData(this.data.get(i));

            this.elements.add(element);
        }

        if (this.isAxisEnabled()) {
            this.elements.add(this.getAxisElement());
        }

        Point titlePoint = new Point(center.getX(), center.getY() - this.getTotalHeight() * 0.65);
        this.elements.add(this.data.title().createTitle(titlePoint));
    }

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
