package fr.univrennes.istic.l2gen.visustats.view.dataset;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

public interface IDataSetView extends IShape {

    public void setData(DataSet data);

}
