package fr.univrennes.istic.l2gen.svg.animations;

import fr.univrennes.istic.l2gen.svg.interfaces.ISVGAttribute;

/**
 * Record représentant le nombre de répétitions d'une animation SVG.
 * Utilise -1 pour indiquer une répétition infinie.
 */
public record AnimationCount(int count) implements ISVGAttribute {
    /**
     * Constante représentant une répétition infinie.
     */
    public static final AnimationCount INDEFINITE = new AnimationCount(-1);

    public AnimationCount(String raw) {
        this(raw.equals("indefinite") ? -1 : Integer.parseInt(raw));
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    @Override
    public String getContent() {
        if (count == -1) {
            return "indefinite";
        }
        return Integer.toString(count);
    }
}
