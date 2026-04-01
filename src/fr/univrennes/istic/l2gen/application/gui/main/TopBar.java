package fr.univrennes.istic.l2gen.application.gui.main;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.swing.*;

import fr.univrennes.istic.l2gen.application.core.lang.Lang;
import fr.univrennes.istic.l2gen.application.gui.GUIController;

public final class TopBar extends JMenuBar {

    private final GUIController controller;

    public TopBar(GUIController controller) {
        this.controller = controller;
        this.build();
    }

    private void build() {
        JMenu file = new JMenu(Lang.get("menu.file"));

        JMenuItem openItem = new JMenuItem(Lang.get("menu.file.open"));
        openItem.addActionListener(e -> controller.onOpenFileDialog());
        file.add(openItem);

        JMenuItem openUrlItem = new JMenuItem(Lang.get("menu.file.open_url"));
        openUrlItem.addActionListener(e -> controller.onOpenUrlDialog());
        file.add(openUrlItem);

        JMenuItem exitItem = new JMenuItem(Lang.get("menu.file.exit"));
        exitItem.addActionListener(e -> System.exit(0));
        file.add(exitItem);

        JMenu view = new JMenu(Lang.get("menu.view"));

        JMenu panels = new JMenu(Lang.get("menu.view.panels"));
        panels.addSeparator();

        view.add(panels);

        JMenu help = new JMenu(Lang.get("menu.help"));

        Set<String> languages = new HashSet<>();
        JMenu langs = new JMenu(Lang.get("menu.help.languages"));

        for (Locale locale : Locale.getAvailableLocales()) {

            if (!Lang.isSupported(locale)) {
                continue;
            }

            if (!languages.add(locale.getLanguage())) {
                continue;
            }

            Locale langLocale = Locale.forLanguageTag(locale.getLanguage());
            String name = langLocale.getDisplayLanguage(langLocale);

            name = name.substring(0, 1).toUpperCase() + langLocale.getDisplayLanguage(langLocale).substring(1);
            JMenuItem langItem = new JMenuItem(name);
            langItem.addActionListener(e -> Lang.setLocale(langLocale));

            langs.add(langItem);
        }

        help.add(langs);

        JMenuItem documentation = new JMenuItem(Lang.get("menu.help.documentation"));
        documentation.addActionListener(e -> controller.onOpenDocDialog());
        help.add(documentation);

        JMenuItem about = new JMenuItem(Lang.get("menu.help.about"));
        about.addActionListener(e -> controller.onOpenAboutDialog());
        help.add(about);

        add(file);
        add(view);
        add(help);
    }
}