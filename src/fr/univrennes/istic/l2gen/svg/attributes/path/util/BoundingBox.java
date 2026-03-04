package fr.univrennes.istic.l2gen.svg.attributes.path.util;

/**
 * Représente une boîte englobante (bounding box) d'une forme.
 * Stocke les coordonnées minimales et maximales pour définir le région
 * spatiale.
 * 
 * @param minX la coordonnée x minimale
 * @param minY la coordonnée y minimale
 * @param maxX la coordonnée x maximale
 * @param maxY la coordonnée y maximale
 */
public record BoundingBox(double minX, double minY, double maxX, double maxY) {

    /**
     * Crée une boîte englobante vide (tous les coins à 0).
     * 
     * @return une boîte englobante vide
     */
    public static BoundingBox empty() {
        return new BoundingBox(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Retourne la largeur de la boîte englobante.
     * 
     * @return la largeur (maxX - minX)
     */
    public double getWidth() {
        return maxX - minX;
    }

    /**
     * Retourne la hauteur de la boîte englobante.
     * 
     * @return la hauteur (maxY - minY)
     */
    public double getHeight() {
        return maxY - minY;
    }

    /**
     * Retourne la coordonnée x du centre de la boîte englobante.
     * 
     * @return la coordonnée x du centre
     */
    public double getCenterX() {
        return (minX + maxX) / 2;
    }

    /**
     * Retourne la coordonnée y du centre de la boîte englobante.
     * 
     * @return la coordonnée y du centre
     */
    public double getCenterY() {
        return (minY + maxY) / 2;
    }
}
