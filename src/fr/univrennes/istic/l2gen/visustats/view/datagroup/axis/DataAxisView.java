package fr.univrennes.istic.l2gen.visustats.view.datagroup.axis;

import fr.univrennes.istic.l2gen.geometry.Group;
import fr.univrennes.istic.l2gen.geometry.Path;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.base.Triangle;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

@SVGTag("g")
public final class DataAxisView extends Group {

    @SVGField({ "data-center-x", "data-center-y" })
    private Point center;

    @SVGField("data-totalwidth")
    private double totalWidth;

    @SVGField("data-totalheight")
    private double totalHeight;

    @SVGField("data-spacing")
    private double spacing;

    @SVGField("data-maxvalue")
    private double maxValue;

    @SVGField("data-stepcount")
    private int stepCount;

    public DataAxisView(Point center, double totalWidth, double totalHeight, double spacing, double maxValue,
            int stepCount) {
        this.center = center;
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.spacing = spacing;

        this.maxValue = maxValue;
        this.stepCount = stepCount;

        this.update();
    }

    private void update() {
        this.elements.clear();

        double padding = this.spacing * 2;
        double triangleSize = 5;

        Path horizontal = new Path();
        horizontal
                .getStyle()
                .strokeColor(Color.BLACK)
                .strokeWidth(2);

        horizontal
                .draw()
                .move(
                        this.center.getX() - (this.totalWidth / 2),
                        this.center.getY() + (this.totalHeight / 2),
                        false)
                .line(
                        this.center.getX() + (this.totalWidth / 2) + padding
                                - triangleSize,
                        this.center.getY() + (this.totalHeight / 2),
                        false);
        this.elements.add(new Triangle(
                new Point(
                        this.center.getX() + (this.totalWidth / 2) + padding
                                - triangleSize,
                        this.center.getY() + (this.totalHeight / 2)
                                - triangleSize),
                new Point(
                        this.center.getX() + (this.totalWidth / 2) + padding
                                - triangleSize,
                        this.center.getY() + (this.totalHeight / 2)
                                + triangleSize),
                new Point(
                        this.center.getX() + (this.totalWidth / 2) + padding,
                        this.center.getY() + (this.totalHeight / 2))));

        this.elements.add(horizontal);

        Path vertical = new Path();
        vertical
                .getStyle()
                .strokeColor(Color.BLACK)
                .strokeWidth(2);

        vertical.draw()
                .move(
                        this.center.getX() - (this.totalWidth / 2),
                        this.center.getY() + (this.totalHeight / 2),
                        false)
                .line(
                        this.center.getX() - (this.totalWidth / 2),
                        this.center.getY() - (this.totalHeight / 2) - padding,
                        false);

        double[] steps = new double[this.stepCount + 1];
        for (int i = 0; i <= this.stepCount; i++) {
            steps[i] = (this.maxValue / this.stepCount) * i;
            double y = this.center.getY() + (this.totalHeight / 2) - (steps[i] /
                    this.maxValue) * this.totalHeight;
            vertical.draw()
                    .move(this.center.getX() - (this.totalWidth / 2) - 5, y, false)
                    .line(this.center.getX() - (this.totalWidth / 2), y, false);
        }

        this.elements.add(new Triangle(
                new Point(
                        this.center.getX() - (this.totalWidth / 2) - triangleSize,
                        this.center.getY() - (this.totalHeight / 2)
                                - padding),

                new Point(
                        this.center.getX() - (this.totalWidth / 2) + triangleSize,
                        this.center.getY() - (this.totalHeight / 2)
                                - padding),

                new Point(
                        this.center.getX() - (this.totalWidth / 2),
                        this.center.getY() - (this.totalHeight / 2) - padding
                                - triangleSize)));
        this.elements.add(vertical);

    }
}
