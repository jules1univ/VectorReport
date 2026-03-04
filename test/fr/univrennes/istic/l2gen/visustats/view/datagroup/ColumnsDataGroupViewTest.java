package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Point;

public class ColumnsDataGroupViewTest extends AbstractDataGroupViewTest<ColumnsDataGroupView> {

    @Override
    public ColumnsDataGroupView create() {
        return new ColumnsDataGroupView(createDataGroup(3, 10, 150), new Point(500, 500), 15,
                10,
                100);
    }

}
