package fr.univrennes.istic.l2gen.svg.attributes.path.commands;

/**
 * Représente une commande de déplacement ou de ligne dans un chemin SVG.
 * Implémente IPathCommand et accepte les coordonnées nullables pour les
 * commandes horizontales/verticales.
 * 
 * @param x    la coordonnée x (peut être null pour les commandes verticales)
 * @param y    la coordonnée y (peut être null pour les commandes horizontales)
 * @param type le type de commande (move, line, horizontal, vertical, etc.)
 */
public record MoveCommand(Double x, Double y, MoveCommandType type) implements IPathCommand {

    public MoveCommand(double value, MoveCommandType type) {
        this(value, null, type);
    }

    /**
     * Retourne la représentation SVG de cette commande de déplacement.
     * 
     * @return la commande au format SVG (ex: "M 10,20", "L 30,40", "H 50", "V 60")
     */
    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case ABSOLUTE -> sb.append("M");
            case RELATIVE -> sb.append("m");

            case LINE -> sb.append("L");
            case LINE_RELATIVE -> sb.append("l");

            case HORIZONTAL -> sb.append("H");
            case HORIZONTAL_RELATIVE -> sb.append("h");

            case VERTICAL -> sb.append("V");
            case VERTICAL_RELATIVE -> sb.append("v");
        }

        sb.append(x);
        if (y != null) {
            sb.append(",").append(y);
        }
        return sb.toString();
    }

    @Override
    public IPathCommand translate(double dx, double dy) {
        return new MoveCommand(
                this.x != null ? this.x + dx : null,
                this.y != null ? this.y + dy : null,
                this.type);
    }

}
