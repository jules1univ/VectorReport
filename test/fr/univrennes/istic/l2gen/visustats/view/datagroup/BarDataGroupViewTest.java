package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import org.junit.Test;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.io.svg.SVGExportTestUtil;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.Label;
import fr.univrennes.istic.l2gen.visustats.view.dataset.AbstractDataSetViewTest;

public class BarDataGroupViewTest {
    private DataGroup createDataGroup(int size, double minValue, double maxValue) {
        DataGroup group = new DataGroup(new Label("Bar Test title"));
        for (int i = 0; i < size; i++) {
            group.add(
                    AbstractDataSetViewTest.createDataSet(size, minValue, maxValue));
        }
        return group;
    }

    @Test
    public void testSVG() {
        BarDataGroupView barview = new BarDataGroupView(createDataGroup(5, 10, 150), 30, 200, 100, new Point(500, 500));
        SVGExportTestUtil.export(barview);
    }
}
