package fr.univrennes.istic.l2gen.visustats.view.dataset;

import org.junit.Ignore;
import org.junit.Test;

import fr.univrennes.istic.l2gen.io.svg.SVGExportTestUtil;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;
import fr.univrennes.istic.l2gen.visustats.data.Label;
import fr.univrennes.istic.l2gen.visustats.data.Value;

@Ignore("Abstract DataSet View Test")
public abstract class AbstractDataSetViewTest<DataSetView extends IDataSetView> {

    public abstract DataSetView create();

    public final static DataSet createDataSet(int size, double minValue, double maxValue) {

        DataSet dataset = new DataSet(new Label("Test title"));
        for (int i = 0; i < size; i++) {
            double value = Math.random() * (maxValue - minValue) + minValue;
            dataset.values().add(new Value(value, Color.random()));
        }
        return dataset;
    }

    @Test
    public final void testSVG() {
        DataSetView dataView = create();
        assert dataView != null;

        dataView.setData(createDataSet(5, 10, 100));
        SVGExportTestUtil.export(dataView);
    }
}
