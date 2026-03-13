package fr.univrennes.istic.l2gen.application.gui.panels.table.filter;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class FilterConditionRow {

    public final JPanel rowPanel;
    public final JComboBox<String> operatorComboBox;
    public final JComboBox<String> columnComboBox;
    public final JComboBox<String> conditionComboBox;
    public final JTextField valueTextField;

    public FilterConditionRow(
            JPanel rowPanel,
            JComboBox<String> operatorComboBox,
            JComboBox<String> columnComboBox,
            JComboBox<String> conditionComboBox,
            JTextField valueTextField) {
        this.rowPanel = rowPanel;
        this.operatorComboBox = operatorComboBox;
        this.columnComboBox = columnComboBox;
        this.conditionComboBox = conditionComboBox;
        this.valueTextField = valueTextField;
    }
}
