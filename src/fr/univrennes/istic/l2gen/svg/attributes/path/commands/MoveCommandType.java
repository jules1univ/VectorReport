package fr.univrennes.istic.l2gen.svg.attributes.path.commands;

/**
 * Énumération des types de commandes de déplacement (move, line, horizontal,
 * vertical).
 * Indique le type et le mode (absolu ou relatif) de la commande.
 */
public enum MoveCommandType {
    VERTICAL_RELATIVE,
    VERTICAL,
    HORIZONTAL_RELATIVE,
    HORIZONTAL,
    LINE_RELATIVE,
    LINE,
    RELATIVE,
    ABSOLUTE

}
