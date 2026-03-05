package fr.univrennes.istic.l2gen.svg.attributes.path.commands;

/**
 * Représente une commande d'arc elliptique dans un chemin SVG.
 * Définit un arc elliptique avec différents paramètres de contrôle.
 * Implémente IPathCommand.
 * 
 * @param rx            le rayon majeur selon l'axe x
 * @param ry            le rayon majeur selon l'axe y
 * @param xAxisRotation la rotation de l'axe x en degrés
 * @param largeArcFlag  true pour sélectionner l'arc majeur, false pour l'arc
 *                      mineur
 * @param sweepFlag     true pour un balayage positif, false pour négatif
 * @param x             la coordonnée x du point final
 * @param y             la coordonnée y du point final
 * @param type          le type d'arc (A pour absolu, a pour relatif)
 */
public record ArcCommand(double rx, double ry, double xAxisRotation, boolean largeArcFlag, boolean sweepFlag, double x,
        double y, ArcCommandType type) implements IPathCommand {

    /**
     * Retourne la représentation SVG de cet arc elliptique.
     * 
     * @return la commande au format SVG (ex: "A 25,25 0 0 1 50,50")
     */
    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case ABSOLUTE -> sb.append("A");
            case RELATIVE -> sb.append("a");
        }
        sb.append(rx).append(",").append(ry).append(" ").append(xAxisRotation).append(" ")
                .append(largeArcFlag ? "1" : "0").append(" ").append(sweepFlag ? "1" : "0").append(" ")
                .append(x).append(",").append(y);
        return sb.toString();
    }

    @Override
    public IPathCommand translate(double dx, double dy) {
        return new ArcCommand(this.rx, this.ry, this.xAxisRotation, this.largeArcFlag, this.sweepFlag, this.x + dx,
                this.y + dy,
                this.type);
    }
}
