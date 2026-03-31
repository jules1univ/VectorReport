package fr.univrennes.istic.l2gen.geometry;

import fr.univrennes.istic.l2gen.svg.attributes.style.SVGStyle;
import fr.univrennes.istic.l2gen.svg.attributes.transform.SVGTransform;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPoint;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPointX;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPointY;

/**
 * Représente un point en coordonnées cartésiennes (x, y).
 * Un point est une forme géométrique de dimension 0, annoté pour l'export SVG.
 */
@SVGPoint
public final class Point implements IShape {

    @SVGPointX
    private double x;

    @SVGPointY
    private double y;

    /**
     * Constructeur par défaut. Crée un point à l'origine (0, 0).
     */
    public Point() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Constructeur avec coordonnées spécifiées.
     * 
     * @param x la coordonnée x
     * @param y la coordonnée y
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retourne la coordonnée x du point.
     * 
     * @return la coordonnée x
     */
    public double getX() {
        return x;
    }

    /**
     * Retourne la coordonnée y du point.
     * 
     * @return la coordonnée y
     */
    public double getY() {
        return y;
    }

    /**
     * Définit la coordonnée x du point.
     * 
     * @param x la nouvelle coordonnée x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Définit la coordonnée y du point.
     * 
     * @param y la nouvelle coordonnée y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Définit les coordonnées x et y du point.
     * 
     * @param x la nouvelle coordonnée x
     * @param y la nouvelle coordonnée y
     */
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Ajoute un déplacement aux coordonnées du point (translation).
     * 
     * @param dx le déplacement selon l'axe x
     * @param dy le déplacement selon l'axe y
     * @return ce point (pour le chaîinage)
     */
    public Point add(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        return this;

    }

    /**
     * Ajoute les coordonnées d'un autre point à ce point.
     * 
     * @param p le point à ajouter
     * @return ce point (pour le chaîinage)
     */
    public Point add(Point p) {
        this.x += p.x;
        this.y += p.y;
        return this;
    }

    /**
     * Multiplie les coordonnées du point par des facteurs d'échelle (scaling).
     * 
     * @param dx le facteur d'échelle selon l'axe x
     * @param dy le facteur d'échelle selon l'axe y
     * @return ce point (pour le chaîinage)
     */
    public Point mult(double dx, double dy) {
        this.x *= dx;
        this.y *= dy;
        return this;

    }

    /**
     * Multiplie les coordonnées de ce point par celles d'un autre point.
     * 
     * @param p le point contenant les facteurs de multiplication
     * @return ce point (pour le chaîinage)
     */
    public Point mult(Point p) {
        this.x *= p.x;
        this.y *= p.y;
        return this;

    }

    /**
     * Retourne la largeur d'un point (toujours 1).
     * 
     * @return 1
     */
    @Override
    public double getWidth() {
        return 1;
    }

    /**
     * Retourne la hauteur d'un point (toujours 1).
     * 
     * @return 1
     */
    @Override
    public double getHeight() {
        return 1;
    }

    /**
     * Retourne le centre du point (le point lui-même).
     * 
     * @return ce point
     */
    @Override
    public Point getCenter() {
        return this;
    }

    /**
     * Les points ne support pas les styles SVG.
     * 
     * @return levé toujours une exception
     * @throws UnsupportedOperationException car un point ne peut pas avoir de style
     */
    @Override
    public SVGStyle getStyle() {
        throw new UnsupportedOperationException("A Point does not support styles");
    }

    /**
     * Les points ne supportent pas les transformations SVG.
     * 
     * @return levé toujours une exception
     * @throws UnsupportedOperationException car un point ne peut pas avoir de
     *                                       transformation
     */
    @Override
    public SVGTransform getTransform() {
        throw new UnsupportedOperationException("A Point does not support transformations");
    }

    /**
     * Retourne une description textuelle du point.
     * 
     * @param indent le niveau d'indentation pour le formatage
     * @return une description du point
     */
    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(Math.max(0, indent)));

        sb.append("Point ");
        sb.append(this.toString());
        return sb.toString();
    }

    /**
     * Déplace le point d'une certaine distance.
     * 
     * @param dx le déplacement selon l'axe x
     * @param dy le déplacement selon l'axe y
     */
    @Override
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Redimensionne le point en appliquant des facteurs d'échelle aux coordonnées.
     * 
     * @param px le facteur d'échelle selon l'axe x
     * @param py le facteur d'échelle selon l'axe y
     */
    @Override
    public void resize(double px, double py) {
        this.x *= px;
        this.y *= py;
    }

    /**
     * La rotation d'un point ne change rien car un point remain un point.
     * 
     * @param deg l'angle de rotation en degrés (ignoré)
     */
    @Override
    public void rotate(double deg) {
        // Ne rien faire car un point reste un point après rotation
    }

    /**
     * Crée une copie du point avec les mêmes coordonnées.
     * 
     * @return une nouvelle instance de Point
     */
    @Override
    public IShape copy() {
        return new Point(this.x, this.y);
    }

    /**
     * Représentation textuelle du point au format "x,y" (coordonnées entières).
     * 
     * @return la chaîne de représentation
     */
    @Override
    public String toString() {
        return (int) x + "," + (int) y;
    }

    /**
     * Vérifie l'égalité avec un autre point en comparant les coordonnées.
     * 
     * @param e l'objet à comparer
     * @return true si les deux points ont les mêmes coordonnées, false sinon
     */
    @Override
    public boolean equals(Object e) {
        if (e instanceof Point) {
            return this.x == ((Point) e).getX() && this.y == ((Point) e).getY();
        }
        return false;
    }
}
