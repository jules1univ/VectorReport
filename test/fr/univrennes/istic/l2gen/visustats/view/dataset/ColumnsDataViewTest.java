package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.Point;

public class ColumnsDataViewTest extends AbstractDataSetViewTest<ColumnsDataSetView> {

    @Override
    public ColumnsDataSetView create() {
        return new ColumnsDataSetView(new Point(500, 500), 100, 10, 200);
    }

}
