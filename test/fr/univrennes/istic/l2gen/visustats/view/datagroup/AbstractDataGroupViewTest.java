package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import org.junit.Test;

import fr.univrennes.istic.l2gen.io.svg.SVGExportTestUtil;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.Label;
import fr.univrennes.istic.l2gen.visustats.view.dataset.AbstractDataSetViewTest;

public abstract class AbstractDataGroupViewTest<GroupView extends IDataGroupView> {

    public abstract GroupView create();

    public static DataGroup createDataGroup(int size, double minValue, double maxValue) {
        DataGroup group = new DataGroup(new Label("Test title"));
        for (int i = 0; i < size; i++) {
            group.add(
                    AbstractDataSetViewTest.createDataSet(size, minValue, maxValue));
        }
        return group;
    }

    @Test
    public final void testSVG() {
        SVGExportTestUtil.export(create());
    }

}
