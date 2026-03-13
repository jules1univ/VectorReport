package fr.univrennes.istic.l2gen.application.gui.panels.table.filter;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import fr.univrennes.istic.l2gen.application.core.services.filter.FilterCondition;
import fr.univrennes.istic.l2gen.application.gui.GUIController;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

public final class FilterDialog extends JDialog {

    public static final String OPERATOR_AND = "AND";
    public static final String OPERATOR_OR = "OR";
    public static final String OPERATOR_NOT = "NOT";

    public static final String CONDITION_EQUALS = "equals";
    public static final String CONDITION_CONTAINS = "contains";
    public static final String CONDITION_STARTS_WITH = "starts with";
    public static final String CONDITION_ENDS_WITH = "ends with";
    public static final String CONDITION_GREATER_THAN = ">";
    public static final String CONDITION_LESS_THAN = "<";
    public static final String CONDITION_GREATER_OR_EQUAL = ">=";
    public static final String CONDITION_LESS_OR_EQUAL = "<=";
    public static final String CONDITION_IS_EMPTY = "empty";
    public static final String CONDITION_IS_NOT_EMPTY = "not empty";

    private final String[] columnNames;
    private final GUIController controller;

    private final JPanel conditionsContainer;
    private final List<FilterConditionRow> conditionRows = new ArrayList<>();

    public FilterDialog(Window parent, String[] columnNames, GUIController controller) {
        super(parent, "Filters", ModalityType.APPLICATION_MODAL);
        this.columnNames = columnNames;
        this.controller = controller;

        setLayout(new BorderLayout(8, 8));
        setMinimumSize(new Dimension(700, 400));

        conditionsContainer = new JPanel();
        conditionsContainer.setLayout(new BoxLayout(conditionsContainer, BoxLayout.Y_AXIS));
        conditionsContainer.setBorder(new EmptyBorder(8, 8, 8, 8));

        addConditionRow(null);

        JScrollPane conditionsScrollPane = new JScrollPane(conditionsContainer);
        conditionsScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(conditionsScrollPane, BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel buildBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(4, 8, 8, 8));
        bottomPanel.add(buildAddRowPanel(), BorderLayout.WEST);
        bottomPanel.add(buildActionButtonsPanel(), BorderLayout.EAST);
        return bottomPanel;
    }

    private JPanel buildAddRowPanel() {
        JPanel addRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addAndButton = new JButton("AND");
        addAndButton.addActionListener(e -> appendConditionRow(OPERATOR_AND));

        JButton addOrButton = new JButton("OR");
        addOrButton.addActionListener(e -> appendConditionRow(OPERATOR_OR));

        JButton addNotButton = new JButton("NOT");
        addNotButton.addActionListener(e -> appendConditionRow(OPERATOR_NOT));

        addRowPanel.add(addAndButton);
        addRowPanel.add(addOrButton);
        addRowPanel.add(addNotButton);
        return addRowPanel;
    }

    private JPanel buildActionButtonsPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton clearAllButton = new JButton("Clear all");
        clearAllButton.addActionListener(e -> resetConditions());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            List<FilterCondition> conditions = buildFilterConditions();
            if (!conditions.isEmpty()) {
                this.controller.onFilterRequested(conditions);
            }
            dispose();
        });

        actionPanel.add(clearAllButton);
        actionPanel.add(cancelButton);
        actionPanel.add(applyButton);
        return actionPanel;
    }

    private void appendConditionRow(String logicalOperator) {
        addConditionRow(logicalOperator);
        conditionsContainer.revalidate();
        conditionsContainer.repaint();
    }

    private void resetConditions() {
        conditionRows.clear();
        conditionsContainer.removeAll();
        addConditionRow(null);
        conditionsContainer.revalidate();
        conditionsContainer.repaint();
    }

    private void addConditionRow(String logicalOperator) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

        JComboBox<String> operatorComboBox;
        if (logicalOperator == null) {
            operatorComboBox = null;
            rowPanel.add(new JLabel("WHERE"));
        } else {
            operatorComboBox = new JComboBox<>(new String[] { OPERATOR_AND, OPERATOR_OR, OPERATOR_NOT });
            operatorComboBox.setSelectedItem(logicalOperator);
            rowPanel.add(operatorComboBox);
        }

        JComboBox<String> columnComboBox = new JComboBox<>(columnNames);

        JComboBox<String> conditionComboBox = new JComboBox<>(new String[] {
                CONDITION_EQUALS,
                CONDITION_CONTAINS,
                CONDITION_STARTS_WITH,
                CONDITION_ENDS_WITH,
                CONDITION_GREATER_THAN,
                CONDITION_LESS_THAN,
                CONDITION_GREATER_OR_EQUAL,
                CONDITION_LESS_OR_EQUAL,
                CONDITION_IS_EMPTY,
                CONDITION_IS_NOT_EMPTY
        });

        JTextField valueTextField = new JTextField(12);

        conditionComboBox.addActionListener(e -> {
            String selectedCondition = (String) conditionComboBox.getSelectedItem();
            boolean requiresValue = !CONDITION_IS_EMPTY.equals(selectedCondition)
                    && !CONDITION_IS_NOT_EMPTY.equals(selectedCondition);
            valueTextField.setEnabled(requiresValue);
        });

        Icon removeIcon = UIManager.getIcon("InternalFrame.closeIcon");
        JButton removeRowButton = new JButton(removeIcon);
        removeRowButton.setPreferredSize(new Dimension(28, 24));
        removeRowButton.addActionListener(e -> {
            conditionRows.removeIf(row -> row.rowPanel == rowPanel);
            conditionsContainer.remove(rowPanel);
            conditionsContainer.revalidate();
            conditionsContainer.repaint();
        });

        rowPanel.add(columnComboBox);
        rowPanel.add(conditionComboBox);
        rowPanel.add(valueTextField);
        rowPanel.add(removeRowButton);

        conditionRows.add(
                new FilterConditionRow(rowPanel, operatorComboBox, columnComboBox, conditionComboBox, valueTextField));
        conditionsContainer.add(rowPanel);
    }

    private List<FilterCondition> buildFilterConditions() {
        List<FilterCondition> conditions = new ArrayList<>();
        for (int i = 0; i < conditionRows.size(); i++) {
            FilterConditionRow row = conditionRows.get(i);
            String logicalOperator = (i == 0) ? null : (String) row.operatorComboBox.getSelectedItem();
            String columnName = (String) row.columnComboBox.getSelectedItem();
            String conditionType = (String) row.conditionComboBox.getSelectedItem();
            String value = row.valueTextField.getText().trim();
            conditions.add(new FilterCondition(logicalOperator, columnName, conditionType, value));
        }
        return conditions;
    }
}