package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Path;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

@SVGTag("g")
public class BarDataSetView extends AbstractDataSetView {

    @SVGField("data-bar-width")
    private double barWidth;

    @SVGField("data-max-height")
    private double maxHeight;

    public BarDataSetView() {
        super(new Point(0, 0));
        this.barWidth = 40;
        this.maxHeight = 200;
    }

    public BarDataSetView(Point center, double barWidth, double maxHeight) {
        super(center);

        this.barWidth = barWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public void setData(DataSet data) {
        super.setData(data);

        double total = data.sum();
        double baseX = this.center.getX();
        double baseY = this.center.getY() - (maxHeight / 2);
        double accHeight = 0;

        double left = baseX - (barWidth / 2);
        double right = baseX + (barWidth / 2);

        for (int i = 0; i < data.size(); i++) {
            double val = data.getValue(i);
            double height = (val / total) * maxHeight;
            double bottom = baseY + accHeight;
            double top = bottom + height;

            Path barSegment = new Path();
            barSegment.draw()
                    .move(left, bottom, false)
                    .line(right, bottom, false)
                    .line(right, top, false)
                    .line(left, top, false)
                    .line(left, bottom, false)
                    .close();

            barSegment.getStyle()
                    .fillColor(data.getColor(i))
                    .strokeColor(Color.BLACK)
                    .strokeWidth(1);

            this.elements.add(barSegment);
            accHeight += height;
        }
    }

    @Override
    public IShape copy() {
        return new BarDataSetView((Point) this.center, this.barWidth,
                this.maxHeight);
    }

    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(Math.max(0, indent)));
        sb.append("BarDataView");
        return sb.toString();
    }

    @Override
    public double getHeight() {
        return this.maxHeight;
    }

    @Override
    public double getWidth() {
        return this.barWidth;
    }

}