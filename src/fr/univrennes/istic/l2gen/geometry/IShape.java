package fr.univrennes.istic.l2gen.geometry;

import fr.univrennes.istic.l2gen.svg.attributes.style.SVGStyle;
import fr.univrennes.istic.l2gen.svg.attributes.transform.SVGTransform;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGShape;

/**
 * Interface pour les formes géométriques.
 * Définit les opérations communes pour toutes les formes (mouvement,
 * redimensionnement,
 * rotation, copie) et les propriétés (dimensions, centre, style,
 * transformations).
 * Expose les formes pour l'export SVG en implémentant ISVGShape.
 */
public interface IShape extends ISVGShape {

    /**
     * Retourne la largeur de la forme.
     * 
     * @return la largeur
     */
    double getWidth();

    /**
     * Retourne la hauteur de la forme.
     * 
     * @return la hauteur
     */
    double getHeight();

    /**
     * Retourne le centre de la forme.
     * 
     * @return le point centre
     */
    Point getCenter();

    /**
     * Retourne le style SVG de la forme.
     * 
     * @return le style SVG
     */
    SVGStyle getStyle();

    /**
     * Retourne la transformation SVG de la forme.
     * 
     * @return la transformation SVG
     */
    SVGTransform getTransform();

    /**
     * Retourne une description textuelle de la forme pour débogage.
     * 
     * @param indent le niveau d'indentation pour le formatage
     * @return une description de la forme
     */
    String getDescription(int indent);

    /**
     * Déplace la forme d'une certaine distance.
     * 
     * @param dx le déplacement selon l'axe x
     * @param dy le déplacement selon l'axe y
     */
    void move(double dx, double dy);

    /**
     * Redimensionne la forme en appliquant des facteurs d'échelle.
     * 
     * @param px le facteur d'échelle selon l'axe x
     * @param py le facteur d'échelle selon l'axe y
     */
    void resize(double px, double py);

    /**
     * Effectue une rotation de la forme.
     * 
     * @param deg l'angle de rotation en degrés
     */
    void rotate(double deg);

    /**
     * Crée une copie de la forme.
     * 
     * @return une nouvelle instance de la même forme
     */
    IShape copy();

}