package fr.univrennes.istic.l2gen.svg.attributes.path.commands;

/**
 * Représente une commande de fermeture de chemin SVG (Z).
 * Ferme le chemin en traçant une ligne droite vers le point de début du chemin.
 */
public record CloseCommand() implements IPathCommand {

    /**
     * Retourne la représentation SVG de cette commande de fermeture.
     * 
     * @return la chaîne "Z"
     */
    @Override
    public String getValue() {
        return "Z";
    }

    @Override
    public IPathCommand translate(double dx, double dy) {
        return this;
    }

}
