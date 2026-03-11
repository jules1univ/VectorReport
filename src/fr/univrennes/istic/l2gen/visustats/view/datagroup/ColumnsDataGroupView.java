package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.view.datagroup.axis.DataAxisView;
import fr.univrennes.istic.l2gen.visustats.view.dataset.ColumnsDataSetView;
import fr.univrennes.istic.l2gen.visustats.view.dataset.IDataSetView;

@SVGTag("g")
public class ColumnsDataGroupView extends AbstractDataGroupView {
    private static final int DEFAULT_AXIS_STEP_COUNT = 5;

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
    protected double getTotalElementsHeight() {
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

    @Override
    protected boolean isAxisEnabled() {
        return true;
    }

    @Override
    protected IShape getAxisElement() {
        return new DataAxisView(this.center, this.getTotalElementsWidth(), this.getTotalElementsHeight(), this.spacing,
                this.data.max(), DEFAULT_AXIS_STEP_COUNT);
    }
}