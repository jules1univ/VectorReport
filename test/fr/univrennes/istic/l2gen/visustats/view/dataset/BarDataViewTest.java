package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.Point;

public class BarDataViewTest extends AbstractDataSetViewTest<BarDataSetView> {

    @Override
    public BarDataSetView create() {
        return new BarDataSetView(new Point(500, 500), 100, 200);
    }

}
