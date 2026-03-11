package fr.univrennes.istic.l2gen.geometry.base;

import fr.univrennes.istic.l2gen.geometry.AbstractShape;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Représente une ligne implémentant l'interface IShape.
 * Une ligne est définie par deux points : un point de départ et un point
 * d'arrivée.
 */
@SVGTag("line")
public final class Line extends AbstractShape {

    @SVGField({ "x1", "y1" })
    private Point start;

    @SVGField({ "x2", "y2" })
    private Point end;

    /**
     * Constructeur par défaut. Crée une ligne avec les deux extrémités à l'origine.
     */
    public Line() {
        this.start = new Point(0, 0);
        this.end = new Point(0, 0);
    }

    /**
     * Constructeur avec coordonnées des deux extrémités.
     * 
     * @param x1 la coordonnée x du point de départ
     * @param y1 la coordonnée y du point de départ
     * @param x2 la coordonnée x du point d'arrivée
     * @param y2 la coordonnée y du point d'arrivée
     */
    public Line(double x1, double y1, double x2, double y2) {
        this.start = new Point(x1, y1);
        this.end = new Point(x2, y2);
    }

    /**
     * Retourne le point de départ de la ligne.
     * 
     * @return le point de départ
     */
    public Point getStart() {
        return this.start;
    }

    /**
     * Retourne le point d'arrivée de la ligne.
     * 
     * @return le point d'arrivée
     */
    public Point getEnd() {
        return this.end;
    }

    /**
     * Retourne la largeur de la ligne (différence absolue en x).
     * 
     * @return la largeur
     */
    @Override
    public double getWidth() {
        return Math.abs(this.end.getX() - this.start.getX());
    }

    /**
     * Retourne la hauteur de la ligne (différence absolue en y).
     * 
     * @return la hauteur
     */
    @Override
    public double getHeight() {
        return Math.abs(this.end.getY() - this.start.getY());
    }

    /**
     * Retourne le centre de la ligne (point milieu).
     * 
     * @return le point centre
     */
    @Override
    public Point getCenter() {
        return new Point((this.start.getX() + this.end.getX()) / 2,
                (this.start.getY() + this.end.getY()) / 2);
    }

    /**
     * Retourne une description textuelle de la ligne.
     * 
     * @param indent le nombre d'espaces pour l'indentation
     * @return une string décrivant la ligne
     */
    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDescription(indent));

        sb.append(" X1=").append(this.start.getX());
        sb.append(" Y1=").append(this.start.getY());
        sb.append(" X2=").append(this.end.getX());
        sb.append(" Y2=").append(this.end.getY());
        return sb.toString();
    }

    /**
     * Déplace la ligne d'une certaine distance.
     * 
     * @param dx le déplacement selon l'axe x
     * @param dy le déplacement selon l'axe y
     */
    @Override
    public void move(double dx, double dy) {
        this.start = new Point(this.start.getX() + dx, this.start.getY() + dy);
        this.end = new Point(this.end.getX() + dx, this.end.getY() + dy);
    }

    /**
     * Redimensionne la ligne en appliquant des facteurs d'échelle.
     * 
     * @param px le facteur d'échelle selon l'axe x
     * @param py le facteur d'échelle selon l'axe y
     */
    @Override
    public void resize(double px, double py) {
        this.start = new Point(this.start.getX() * px, this.start.getY() * py);
        this.end = new Point(this.end.getX() * px, this.end.getY() * py);
    }

    /**
     * Crée une copie de la ligne.
     * 
     * @return une nouvelle instance de Line avec les mêmes extrémités
     */
    @Override
    public IShape copy() {
        return new Line(this.start.getX(), this.start.getY(), this.end.getX(), this.end.getY());
    }

}
