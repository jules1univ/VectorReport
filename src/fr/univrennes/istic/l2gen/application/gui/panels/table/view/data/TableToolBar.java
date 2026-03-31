package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import fr.univrennes.istic.l2gen.application.core.icon.Ico;
import fr.univrennes.istic.l2gen.application.core.lang.Lang;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;

public final class TableToolBar extends JToolBar {

    TableToolBar(GUIController controller, TablePanel tablePanel) {
        setFloatable(false);

        JButton advancedFilterButton = new JButton(Lang.get("tabletoolbar.filters"),
                Ico.get("icons/filter.svg"));
        advancedFilterButton.addActionListener(e -> controller.onOpenFilterDialog());

        JButton showAllColumnsButton = new JButton(Lang.get("tabletoolbar.show_all_columns"),
                Ico.get("icons/show_columns.svg"));
        showAllColumnsButton.addActionListener(e -> tablePanel.getTable().showAllColumns());

        JButton hideEmptyColumnsButton = new JButton(Lang.get("tabletoolbar.hide_empty_columns"),
                Ico.get("icons/hide_columns.svg"));
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