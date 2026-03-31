package fr.univrennes.istic.l2gen.svg.animations;

/**
 * Énumération des types de transformation pour les animations SVG.
 * Détermine quel type de transformation est animée.
 */
public enum AnimationTransformType {
    /**
     * Animation de translation (déplacement).
     */
    translate,
    /**
     * Animation d'échelle (redimensionnement).
     */
    scale,
    /**
     * Animation de rotation.
     */
    rotate,
    /**
     * Animation d'inclinaison selon l'axe X.
     */
    skewx,
    /**
     * Animation d'inclinaison selon l'axe Y.
     */
    skewy;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
