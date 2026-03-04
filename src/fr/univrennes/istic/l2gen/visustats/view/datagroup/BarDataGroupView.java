package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Group;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Path;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.base.Triangle;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.view.dataset.BarDataSetView;

@SVGTag("g")
public class BarDataGroupView extends AbstractDataGroupView {

        @SVGField("data-maxheight")
        private double maxHeight;

        @SVGField("data-barwidth")
        private double barWidth;

        public BarDataGroupView(
                        DataGroup data,
                        Point center,
                        double spacing,

                        double barWidth,
                        double maxHeight) {
                super(data, center, spacing);

                this.maxHeight = maxHeight;

                this.spacing = spacing;

                this.center = center;
                this.barWidth = barWidth;
                this.update();
        }

        @Override
        protected double getTotalElementsHeight() {
                return this.maxHeight;
        }

        @Override
        protected double getElementWidth() {
                return this.barWidth;
        }

        @Override
        protected BarDataSetView createElement(Point position) {
                return new BarDataSetView(position, this.barWidth, this.maxHeight);
        }

        @Override
        protected boolean isAxisEnabled() {
                return true;
        }

        @Override
        protected IShape getAxisElement() {
                Group axisGroup = new Group();

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
                                                this.center.getX() - (this.getTotalElementsWidth() / 2),
                                                this.center.getY() + (this.getTotalElementsHeight() / 2),
                                                false)
                                .line(
                                                this.center.getX() + (this.getTotalElementsWidth() / 2) + padding
                                                                - triangleSize,
                                                this.center.getY() + (this.getTotalElementsHeight() / 2),
                                                false);
                axisGroup.add(new Triangle(
                                new Point(
                                                this.center.getX() + (this.getTotalElementsWidth() / 2) + padding
                                                                - triangleSize,
                                                this.center.getY() + (this.getTotalElementsHeight() / 2)
                                                                - triangleSize),
                                new Point(
                                                this.center.getX() + (this.getTotalElementsWidth() / 2) + padding
                                                                - triangleSize,
                                                this.center.getY() + (this.getTotalElementsHeight() / 2)
                                                                + triangleSize),
                                new Point(
                                                this.center.getX() + (this.getTotalElementsWidth() / 2) + padding,
                                                this.center.getY() + (this.getTotalElementsHeight() / 2))));

                axisGroup.add(horizontal);

                Path vertical = new Path();
                vertical
                                .getStyle()
                                .strokeColor(Color.BLACK)
                                .strokeWidth(2);
                vertical.draw()
                                .move(
                                                this.center.getX() - (this.getTotalElementsWidth() / 2),
                                                this.center.getY() + (this.getTotalElementsHeight() / 2),
                                                false)
                                .line(
                                                this.center.getX() - (this.getTotalElementsWidth() / 2),
                                                this.center.getY() - (this.getTotalElementsHeight() / 2) - padding,
                                                false);

                axisGroup.add(new Triangle(
                                new Point(
                                                this.center.getX() - (this.getTotalElementsWidth() / 2) - triangleSize,
                                                this.center.getY() - (this.getTotalElementsHeight() / 2)
                                                                - padding),

                                new Point(
                                                this.center.getX() - (this.getTotalElementsWidth() / 2) + triangleSize,
                                                this.center.getY() - (this.getTotalElementsHeight() / 2)
                                                                - padding),

                                new Point(
                                                this.center.getX() - (this.getTotalElementsWidth() / 2),
                                                this.center.getY() - (this.getTotalElementsHeight() / 2) - padding
                                                                - triangleSize)));

                axisGroup.add(vertical);

                return axisGroup;
        }

}
