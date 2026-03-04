package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Point;

public class BarDataGroupViewTest extends AbstractDataGroupViewTest<BarDataGroupView> {

    @Override
    public BarDataGroupView create() {
        return new BarDataGroupView(createDataGroup(5, 10, 150), new Point(500, 500), 30, 200, 100);
    }

}
