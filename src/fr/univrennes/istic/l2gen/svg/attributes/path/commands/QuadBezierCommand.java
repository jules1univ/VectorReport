package fr.univrennes.istic.l2gen.svg.attributes.path.commands;

/**
 * Représente une courbe de Bézier quadratique dans un chemin SVG.
 * Peut contenir un point de contrôle nullable pour les courbes lisses.
 * Implémente IPathCommand.
 * 
 * @param x1   la coordonnée x du point de contrôle (peut être null pour les
 *             courbes lisses)
 * @param y1   la coordonnée y du point de contrôle (peut être null pour les
 *             courbes lisses)
 * @param x    la coordonnée x du point final
 * @param y    la coordonnée y du point final
 * @param type le type de courbe (Q, q, T, t)
 */
public record QuadBezierCommand(Double x1, Double y1, Double x, Double y, QuadBezierCommandType type)
        implements IPathCommand {

    /**
     * Retourne la représentation SVG de cette courbe de Bézier quadratique.
     * 
     * @return la commande au format SVG (ex: "Q 10,20 30,40" ou "T 30,40")
     */
    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case ABSOLUTE -> sb.append("Q");
            case RELATIVE -> sb.append("q");
            case SMOOTH -> sb.append("T");
            case SMOOTH_RELATIVE -> sb.append("t");
        }
        if (x1 != null && y1 != null) {
            sb.append(x1).append(",").append(y1).append(" ");
        }
        sb.append(x).append(",").append(y);
        return sb.toString();
    }

    @Override
    public IPathCommand translate(double dx, double dy) {
        return new QuadBezierCommand(
                this.x1 != null ? this.x1 + dx : null,
                this.y1 != null ? this.y1 + dy : null,
                this.x + dx,
                this.y + dy,
                this.type);
    }

}
