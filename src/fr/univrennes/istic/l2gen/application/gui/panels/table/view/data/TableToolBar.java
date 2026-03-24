package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;

public final class TableToolBar extends JToolBar {

    TableToolBar(GUIController controller, TablePanel tablePanel) {
        setFloatable(false);

        JButton advancedFilterButton = new JButton("Filters", new FlatSVGIcon("icons/filter.svg", 14, 14));
        advancedFilterButton.addActionListener(e -> controller.onOpenFilterDialog());

        JButton showAllColumnsButton = new JButton("Show all columns",
                new FlatSVGIcon("icons/show_columns.svg", 14, 14));
        showAllColumnsButton.addActionListener(e -> tablePanel.getTable().showAllColumns());

        JButton hideEmptyColumnsButton = new JButton("Hide empty columns",
                new FlatSVGIcon("icons/hide_columns.svg", 14, 14));
        hideEmptyColumnsButton.addActionListener(e -> tablePanel.getTable().hideEmptyColumns());

        Icon closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
        JButton closeButton = new JButton(closeIcon);
        closeButton.addActionListener(e -> controller.onCloseTable());

        add(advancedFilterButton);
        addSeparator();
        add(showAllColumnsButton);
        add(hideEmptyColumnsButton);
        add(Box.createHorizontalGlue());
        add(closeButton);
    }
}