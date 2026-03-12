package fr.univrennes.istic.l2gen.application.gui.panels.table;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import fr.univrennes.istic.l2gen.application.gui.GUIController;

final class TableToolBar extends JToolBar {

    TableToolBar(GUIController controller, TablePanel tablePanel, Runnable onClose) {
        setFloatable(false);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> controller.onTableSaveRequested());

        JButton advancedFilterButton = new JButton("Filters");
        advancedFilterButton.addActionListener(e -> tablePanel.openAdvancedFilterDialog());

        Icon closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
        JButton closeButton = new JButton(closeIcon);
        closeButton.addActionListener(e -> {
            controller.onTableClosed();
            onClose.run();
        });

        add(saveButton);
        addSeparator();
        add(advancedFilterButton);
        add(Box.createHorizontalGlue());
        add(closeButton);
    }
}