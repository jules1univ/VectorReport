package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Path;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

@SVGTag("g")
public class PieDataSetView extends AbstractDataSetView {

    @SVGField("data-radius")
    private double radius;

    public PieDataSetView() {
        super(new Point(0, 0));
    }

    public PieDataSetView(Point center, double radius) {
        super(center);
        this.radius = radius;
    }

    @Override
    public double getWidth() {
        return this.radius * 2;
    }

    @Override
    public double getHeight() {
        return this.radius * 2;
    }

    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(indent));
        sb.append("PieView");
        return sb.toString();
    }

    @Override
    public IShape copy() {
        return new PieDataSetView((Point) this.center.copy(), this.radius);
    }

    @Override
    public void setData(DataSet data) {
        double total = data.sum();
        for (int i = 0; i < data.size(); i++) {
            double startAngle = 0;
            for (int j = 0; j < i; j++) {
                startAngle += data.getValue(j) / total * 360;
            }
            double endAngle = startAngle + data.getValue(i) / total * 360;

            Path slice = createSlice(startAngle, endAngle);
            slice.getStyle()
                    .fillColor(data.getColor(i))
                    .strokeColor(Color.BLACK)
                    .strokeWidth(2);

            this.elements.add(slice);
        }
    }

    public Path createSlice(double startAngle, double endAngle) {
        Path slice = new Path();
        slice
                .draw()
                .move(center.getX(), center.getY(), false)
                .line(center.getX() + radius * Math.cos(Math.toRadians(startAngle)),
                        center.getY() + radius * Math.sin(Math.toRadians(startAngle)),
                        false)
                .arc(radius, radius, 0, endAngle - startAngle > 180, true,
                        center.getX() + radius * Math.cos(Math.toRadians(endAngle)),
                        center.getY() + radius * Math.sin(Math.toRadians(endAngle)), false)
                .close();
        return slice;
    }
}
