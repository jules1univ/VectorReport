package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Path;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

@SVGTag("g")
public class ColumnsDataSetView extends AbstractDataSetView {

    @SVGField("data-bar-width")
    private double barWidth;

    @SVGField("data-spacing")
    private double spacing;

    @SVGField("data-max-height")
    private double maxHeight;

    public ColumnsDataSetView() {
        super(new Point(0, 0));
        this.barWidth = 40;
        this.spacing = 10;
        this.maxHeight = 200;
    }

    public ColumnsDataSetView(Point center, double spacing, double barWidth, double maxHeight) {
        super(center);

        this.spacing = spacing;
        this.barWidth = barWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public void setData(DataSet data) {
        super.setData(data);

        double maxValue = data.max();

        double baseX = center.getX() - ((data.size() * barWidth + (data.size() - 1) * spacing) / 2.0);
        double baseY = center.getY() + maxHeight / 2;

        for (int i = 0; i < data.size(); i++) {
            double val = data.getValue(i);
            double height = (val / maxValue) * maxHeight;

            double left = baseX + i * (barWidth + spacing);
            double right = left + barWidth;
            double top = baseY - height;

            Path bar = new Path();
            bar.draw()
                    .move(left, baseY, false)
                    .line(right, baseY, false)
                    .line(right, top, false)
                    .line(left, top, false)
                    .line(left, baseY, false)
                    .close();

            bar.getStyle()
                    .fillColor(data.getColor(i))
                    .strokeColor(Color.BLACK)
                    .strokeWidth(1);

            this.elements.add(bar);
        }

    }

    @Override
    public IShape copy() {
        return new ColumnsDataSetView(new Point(this.center.getX(), this.center.getY()), this.barWidth, this.spacing,
                this.maxHeight);
    }

    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(Math.max(0, indent)));
        sb.append("ColumnView");
        return sb.toString();
    }

    @Override
    public double getHeight() {
        return this.maxHeight;
    }

    @Override
    public double getWidth() {
        return this.barWidth * this.elements.size() + this.spacing * (this.elements.size() - 1);
    }

}