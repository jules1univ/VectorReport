package fr.univrennes.istic.l2gen.svg.animations;

/**
 * Énumération des modes de redémarrage d'une animation SVG.
 * Définit quand l'animation peut recommencer.
 */
public enum AnimationRestart {
    /**
     * L'animation peut redémarrer n'importe quand.
     */
    always,
    /**
     * L'animation ne peut redémarrer que si elle n'est pas active.
     */
    when_not_active,
    /**
     * L'animation ne peut pas redémarrer.
     */
    never;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
