package fr.univrennes.istic.l2gen.geometry.base;

import fr.univrennes.istic.l2gen.geometry.AbstractShape;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Représente une ellipse implémentant l'interface IShape.
 * Une ellipse est définie par un centre et deux rayons (rx et ry).
 */
@SVGTag("ellipse")
public final class Ellipse extends AbstractShape {

    @SVGField({ "cx", "cy" })
    private Point center;

    @SVGField({ "rx", "ry" })
    private Point radius;

    /**
     * Constructeur par défaut. Crée une ellipse avec centre à l'origine et rayons
     * nuls.
     */
    public Ellipse() {
        this.center = new Point(0, 0);
        this.radius = new Point(0, 0);
    }

    /**
     * Constructeur avec coordonnées du centre et rayons.
     * 
     * @param cx la coordonnée x du centre
     * @param cy la coordonnée y du centre
     * @param rx le rayon selon l'axe x
     * @param ry le rayon selon l'axe y
     */
    public Ellipse(double cx, double cy, double rx, double ry) {
        this.center = new Point(cx, cy);
        this.radius = new Point(rx, ry);
    }

    /**
     * Constructeur avec points pour centre et rayons.
     * 
     * @param center le centre de l'ellipse
     * @param radius un point contenant les rayons rx et ry
     */
    public Ellipse(Point center, Point radius) {
        this(center.getX(), center.getY(), radius.getX(), radius.getY());
    }

    /**
     * Retourne la largeur de l'ellipse (diamètre selon l'axe x).
     * 
     * @return la largeur
     */
    @Override
    public double getWidth() {
        return this.radius.getX() * 2;
    }

    /**
     * Retourne la hauteur de l'ellipse (diamètre selon l'axe y).
     * 
     * @return la hauteur
     */
    @Override
    public double getHeight() {
        return this.radius.getY() * 2;
    }

    /**
     * Retourne le centre de l'ellipse.
     * 
     * @return le point centre
     */
    @Override
    public Point getCenter() {
        return this.center;
    }

    /**
     * Retourne une description textuelle de l'ellipse.
     * 
     * @param indent le nombre d'espaces pour l'indentation
     * @return une string décrivant l'ellipse
     */
    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDescription(indent));

        sb.append(" X=");
        sb.append(this.center.getX());
        sb.append(" Y=");
        sb.append(this.center.getY());
        sb.append(" RX=");
        sb.append(this.radius.getX());
        sb.append(" RY=");
        sb.append(this.radius.getY());
        return sb.toString();
    }

    /**
     * Déplace l'ellipse d'une certaine distance.
     * 
     * @param dx le déplacement selon l'axe x
     * @param dy le déplacement selon l'axe y
     */
    @Override
    public void move(double dx, double dy) {
        this.center.add(dx, dy);
    }

    /**
     * Redimensionne l'ellipse en appliquant des facteurs d'échelle.
     * 
     * @param px le facteur d'échelle selon l'axe x
     * @param py le facteur d'échelle selon l'axe y
     */
    @Override
    public void resize(double px, double py) {
        this.radius.mult(px, py);
    }

    /**
     * Crée une copie de l'ellipse.
     * 
     * @return une nouvelle instance d'Ellipse avec les mêmes propriétés
     */
    @Override
    public IShape copy() {
        return new Ellipse(this.center.getX(), this.center.getY(), this.radius.getX(), this.radius.getY());
    }

}
