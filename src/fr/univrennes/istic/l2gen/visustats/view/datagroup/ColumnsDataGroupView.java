package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import java.util.List;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;
import fr.univrennes.istic.l2gen.visustats.view.dataset.ColumnsDataSetView;

@SVGTag("g")
public class ColumnsDataGroupView extends AbstractDataGroupView {
    private double maxHeight;
    private Point center;
    private double barWidth;

    public ColumnsDataGroupView(DataGroup data, Point center, double spacing, double barWidth, double maxHeight) {
        super(data, center, spacing);
        this.maxHeight = maxHeight;
        this.center = center;
        this.barWidth = barWidth;
        this.update();

    }

    @Override
    protected void update() {
        List<DataSet> dataSets = data.datasets();

        this.elements.clear();

        double barSpacing = spacing * 0.8;
        // largeur dataview
        double elWidth = (barWidth + barSpacing) * data.maxSize();
        int n = data.size();
        double totalWidth = elWidth * n + spacing * Math.max(0, n - 1);

        double offsetX = -totalWidth / 2.0;

        for (int i = 0; i < dataSets.size(); i++) {
            DataSet ds = dataSets.get(i);

            double x = center.getX() + offsetX + elWidth / 2.0;
            ColumnsDataSetView colView = new ColumnsDataSetView(new Point(
                    x,
                    center.getY()), barWidth, barSpacing, maxHeight);
            colView.setData(ds);
            this.elements.add(colView);

            offsetX += elWidth + spacing;
        }
        Point titlePoint = new Point(center.getX(), center.getY() - maxHeight * 2 / 3);
        this.elements.add(this.data.title().createTitle(titlePoint));

    }

}
