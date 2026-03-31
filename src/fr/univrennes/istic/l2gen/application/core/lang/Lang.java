package fr.univrennes.istic.l2gen.application.core.lang;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import fr.univrennes.istic.l2gen.application.VectorReport;

public final class Lang {
    private static Lang instance = new Lang();

    private ResourceBundle bundle;
    private Locale locale;

    private Lang() {
        this.locale = Locale.getDefault();
        this.bundle = ResourceBundle.getBundle("languages.vectoreport", locale);
    }

    public static void setLocale(Optional<String> langOpt) {
        if (langOpt.isEmpty()) {
            instance.locale = Locale.getDefault();
        } else {
            instance.locale = Locale.forLanguageTag(langOpt.get());
        }
        instance.bundle = ResourceBundle.getBundle("languages.vectoreport", instance.locale);
    }

    public static Locale getLocale() {
        return instance.locale;
    }

    public static String get(String key) {
        try {
            return instance.bundle.getString(key);
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
            return "<" + key + ">";
        }
    }

    public static String get(String key, Object... args) {
        return String.format(get(key), args);
    }

}
