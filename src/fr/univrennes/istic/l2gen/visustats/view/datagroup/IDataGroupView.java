package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;
import fr.univrennes.istic.l2gen.visustats.data.Label;

public interface IDataGroupView extends IShape {

    public void setTitle(Label title);

    public void setData(DataGroup group);

    public void addData(DataSet dataset);

    public void addLegend(Label legend);

}
