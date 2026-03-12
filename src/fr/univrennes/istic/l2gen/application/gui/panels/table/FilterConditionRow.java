package fr.univrennes.istic.l2gen.application.gui.panels.table;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

final class FilterConditionRow {

    final JPanel rowPanel;
    final JComboBox<String> operatorComboBox;
    final JComboBox<String> columnComboBox;
    final JComboBox<String> conditionComboBox;
    final JTextField valueTextField;

    FilterConditionRow(
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
