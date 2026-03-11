package fr.univrennes.istic.l2gen.svg.attributes.path.commands;

/**
 * Énumération des types de courbes de Bézier cubiques.
 * Dédistingue les courbes lisses et non lisses, en mode absolu ou relatif.
 */
public enum CubicBezierCommandType {
    SMOOTH_RELATIVE,
    SMOOTH,
    RELATIVE,
    ABSOLUTE
}
