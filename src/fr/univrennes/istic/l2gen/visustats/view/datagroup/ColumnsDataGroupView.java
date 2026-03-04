package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.view.dataset.ColumnsDataSetView;
import fr.univrennes.istic.l2gen.visustats.view.dataset.IDataSetView;

@SVGTag("g")
public class ColumnsDataGroupView extends AbstractDataGroupView {

    @SVGField("data-maxheight")
    private double maxHeight;

    @SVGField("data-barwidth")
    private double barWidth;

    private double innerSpacing;

    public ColumnsDataGroupView(DataGroup data, Point center, double spacing, double barWidth, double maxHeight) {
        super(data, center, spacing);

        this.innerSpacing = spacing / 2f;
        this.maxHeight = maxHeight;
        this.barWidth = barWidth;

        this.update();

    }

    @Override
    protected double getTotalHeight() {
        return this.maxHeight;
    }

    @Override
    protected double getElementWidth() {
        return this.barWidth * data.maxSize() + this.innerSpacing * Math.max(0, this.data.maxSize() - 1);
    }

    @Override
    protected IDataSetView createElement(Point position) {
        return new ColumnsDataSetView(position, this.innerSpacing, this.barWidth, this.maxHeight);
    }

}
