package fr.univrennes.istic.l2gen.svg.attributes.path.commands;

/**
 * Représente une courbe de Bézier cubique dans un chemin SVG.
 * Peut contenir des points de contrôle nullables pour les courbes lisses.
 * Implémente IPathCommand.
 * 
 * @param x1   la coordonnée x du premier point de contrôle (peut être null pour
 *             les courbes lisses)
 * @param y1   la coordonnée y du premier point de contrôle (peut être null pour
 *             les courbes lisses)
 * @param x2   la coordonnée x du deuxième point de contrôle
 * @param y2   la coordonnée y du deuxième point de contrôle
 * @param x    la coordonnée x du point final
 * @param y    la coordonnée y du point final
 * @param type le type de courbe (C, c, S, s)
 */
public record CubicBezierCommand(Double x1, Double y1, Double x2, Double y2, Double x, Double y,
        CubicBezierCommandType type)
        implements IPathCommand {

    /**
     * Retourne la représentation SVG de cette courbe de Bézier cubique.
     * 
     * @return la commande au format SVG (ex: "C 10,20 30,40 50,60" ou "S 30,40
     *         50,60")
     */
    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case ABSOLUTE -> sb.append("C");
            case RELATIVE -> sb.append("c");
            case SMOOTH -> sb.append("S");
            case SMOOTH_RELATIVE -> sb.append("s");
        }
        if (x1 != null && y1 != null) {
            sb.append(x1).append(",").append(y1).append(" ");
        }
        sb.append(x2).append(",").append(y2).append(" ").append(x).append(",").append(y);
        return sb.toString();
    }

    @Override
    public IPathCommand translate(double dx, double dy) {
        return new CubicBezierCommand(
                this.x1 != null ? this.x1 + dx : null,
                this.y1 != null ? this.y1 + dy : null,
                this.x2 + dx,
                this.y2 + dy,
                this.x + dx,
                this.y + dy,
                this.type);
    }

}
