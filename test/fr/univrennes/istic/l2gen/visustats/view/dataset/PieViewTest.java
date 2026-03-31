package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.Point;

public final class PieViewTest extends AbstractDataSetViewTest<PieDataSetView> {

    @Override
    public PieDataSetView create() {
        return new PieDataSetView(new Point(500, 500), 200);
    }

}
