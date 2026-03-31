package fr.univrennes.istic.l2gen.svg.animations;

/**
 * Énumération des modes de remplissage après une animation SVG.
 * Définit le comportement de l'élément après la fin de l'animation.
 */
public enum AnimationFill {
    /**
     * Enlève l'effet de l'animation (reviens à l'état initial).
     */
    remove,
    /**
     * Conserve l'effet de l'animation à la fin.
     */
    freeze,
    /**
     * Mode automatique (par défaut).
     */
    auto;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
