package fr.univrennes.istic.l2gen.application.core.lang;

import java.util.Locale;
import java.util.ResourceBundle;

import fr.univrennes.istic.l2gen.application.VectorReport;

public final class Lang {
    private static Lang instance = new Lang();

    private ResourceBundle bundle;
    private Locale locale;

    private Lang() {
        this.locale = getDefaultLocale();
        this.bundle = ResourceBundle.getBundle("languages.vectoreport", locale);
    }

    public static void setLocale(Locale locale) {
        if (locale == null) {
            instance.locale = Locale.getDefault();
        } else {
            instance.locale = locale;
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

    public static boolean isSupported(Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("languages.vectoreport", locale);
            return bundle.getLocale().getLanguage().equals(locale.getLanguage());
        } catch (Exception e) {
            return false;
        }
    }

    public static Locale getDefaultLocale() {
        Locale systemLocale = Locale.getDefault();
        if (isSupported(systemLocale)) {
            return systemLocale;
        }

        for (Locale locale : Locale.getAvailableLocales()) {
            if (isSupported(locale)) {
                return locale;
            }
        }

        return Locale.ENGLISH;
    }

}
