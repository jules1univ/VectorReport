package fr.univrennes.istic.l2gen.svg.animations;

import fr.univrennes.istic.l2gen.svg.interfaces.ISVGAttribute;

/**
 * Record représentant la durée d'une animation SVG en millisecondes.
 * Utilise -1 pour indiquer une durée infinie.
 */
public record AnimationDuration(long miliseconds) implements ISVGAttribute {
    /**
     * Constante représentant une durée infinie.
     */
    public static final AnimationDuration INDEFINITE = new AnimationDuration(-1);

    public AnimationDuration(String raw) {
        this(raw.equals("indefinite") ? -1 : Long.parseLong(raw.replace("ms", "")));
    }

    /**
     * Crée une durée d'animation à partir de secondes.
     * 
     * @param seconds la durée en secondes
     * @return une new AnimationDuration
     */
    public static AnimationDuration s(double seconds) {
        return new AnimationDuration((long) (seconds * 1000));
    }

    /**
     * Crée une durée d'animation à partir de millisecondes.
     * 
     * @param miliseconds la durée en millisecondes
     * @return une new AnimationDuration
     */
    public static AnimationDuration ms(long miliseconds) {
        return new AnimationDuration(miliseconds);
    }

    /**
     * Crée une durée d'animation à partir de minutes.
     * 
     * @param minutes la durée en minutes
     * @return une new AnimationDuration
     */
    public static AnimationDuration min(double minutes) {
        return new AnimationDuration((long) (minutes * 60 * 1000));
    }

    /**
     * Crée une durée infinie.
     * 
     * @return AnimationDuration.INDEFINITE
     */
    public static AnimationDuration infinite() {
        return INDEFINITE;
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    public String getContent() {
        if (miliseconds == -1) {
            return "indefinite";
        }
        return miliseconds + "ms";

    }

}
