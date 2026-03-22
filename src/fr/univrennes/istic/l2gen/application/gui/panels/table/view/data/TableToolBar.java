package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;

public final class TableToolBar extends JToolBar {

    TableToolBar(GUIController controller, TablePanel tablePanel) {
        setFloatable(false);

        JButton advancedFilterButton = new JButton("Filters");
        advancedFilterButton.addActionListener(e -> controller.onOpenFilterDialog());

        JButton showAllColumnsButton = new JButton("Show all columns");
        showAllColumnsButton.addActionListener(e -> tablePanel.getTable().showAllColumns());

        Icon closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
        JButton closeButton = new JButton(closeIcon);
        closeButton.addActionListener(e -> controller.onCloseTable());

        add(advancedFilterButton);
        addSeparator();
        add(showAllColumnsButton);
        add(Box.createHorizontalGlue());
        add(closeButton);
    }
}