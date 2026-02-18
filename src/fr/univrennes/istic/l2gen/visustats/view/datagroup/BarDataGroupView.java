package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import java.util.List;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;
import fr.univrennes.istic.l2gen.visustats.view.dataset.BarDataSetView;

@SVGTag("g")
public class BarDataGroupView extends AbstractDataGroupView {

    protected double maxHeight;
    protected Point center;
    private double barWidth;

    public BarDataGroupView(
            DataGroup data,
            double spacing,
            double maxHeight,
            double barWidth, Point center) {
        super(data, spacing);
        this.maxHeight = maxHeight;
        this.spacing = spacing;
        this.center = center;
        this.barWidth = barWidth;
        update();
    }

    @Override
    protected void update() {
        List<DataSet> dataSets = data.datasets();

        this.elements.clear();

        double barSpacing = spacing * 0.8;
        double elWidth = (barSpacing) * data.maxSize();
        int n = data.size();
        double totalWidth = elWidth * n + spacing * Math.max(0, n - 1);

        double offsetX = -totalWidth / 2.0;

        for (int i = 0; i < dataSets.size(); i++) {
            DataSet ds = dataSets.get(i);

            double x = center.getX() + offsetX + elWidth / 2.0;
            BarDataSetView barView = new BarDataSetView(new Point(
                    x,
                    center.getY()), barWidth, maxHeight);
            barView.setData(ds);
            this.elements.add(barView);

            offsetX += elWidth + spacing;
        }
    }

}
