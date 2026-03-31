package fr.univrennes.istic.l2gen.geometry.base;

import fr.univrennes.istic.l2gen.geometry.AbstractShape;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Représente un rectangle implémentant l'interface IShape.
 * Un rectangle est défini par une position, une largeur et une hauteur.
 */
@SVGTag("rect")
public final class Rectangle extends AbstractShape {

    @SVGField({ "x", "y" })
    private Point position;

    @SVGField({ "width", "height" })
    private Point size;

    @SVGField({ "rx", "ry" })
    private Point radius = new Point(0, 0);

    /**
     * Constructeur par défaut. Crée un rectangle à l'origine avec des dimensions
     * nulles.
     */
    public Rectangle() {
        this.position = new Point(0, 0);
        this.size = new Point(0, 0);
    }

    /**
     * Constructeur avec position et dimensions.
     * 
     * @param position le point à partir duquel le rectangle est positionné
     * @param width    la largeur du rectangle
     * @param height   la hauteur du rectangle
     */
    public Rectangle(Point position, double width, double height) {
        this(position.getX(), position.getY(), width, height);
    }

    /**
     * Constructeur avec coordonnées et dimensions.
     * 
     * @param x      la coordonnée x du coin supérieur gauche
     * @param y      la coordonnée y du coin supérieur gauche
     * @param width  la largeur du rectangle
     * @param height la hauteur du rectangle
     */
    public Rectangle(double x, double y, double width, double height) {
        this.position = new Point(x, y);
        this.size = new Point(width, height);
    }

    /**
     * Retourne la hauteur du rectangle.
     * 
     * @return la hauteur
     */
    public double getHeight() {
        return this.size.getY();
    }

    /**
     * Retourne la largeur du rectangle.
     * 
     * @return la largeur
     */
    public double getWidth() {
        return this.size.getX();
    }

    /**
     * Retourne le centre du rectangle.
     * 
     * @return le point centre
     */
    @Override
    public Point getCenter() {
        return new Point(this.position.getX() + this.size.getX() / 2,
                this.position.getY() + this.size.getY() / 2);
    }

    /**
     * Retourne une description textuelle du rectangle.
     * 
     * @param indentation le nombre d'espaces pour l'indentation
     * @return une string décrivant le rectangle
     */
    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDescription(indent));

        sb.append(" X=");
        sb.append(this.getCenter().getX());
        sb.append(" Y=");
        sb.append(this.getCenter().getY());
        sb.append(" L=");
        sb.append(this.size.getX());
        sb.append(" H=");
        sb.append(this.size.getY());
        return sb.toString();
    }

    /**
     * Déplace le rectangle d'une certaine distance.
     * 
     * @param dx le déplacement selon l'axe x
     * @param dy le déplacement selon l'axe y
     */
    @Override
    public void move(double dx, double dy) {
        this.position.add(dx, dy);
    }

    /**
     * Redimensionne le rectangle en appliquant des facteurs d'échelle à partir du
     * centre.
     * 
     * @param px le facteur d'échelle selon l'axe x
     * @param py le facteur d'échelle selon l'axe y
     */
    @Override
    public void resize(double px, double py) {
        double newWidth = this.size.getX() * px;
        double newHeight = this.size.getY() * py;

        double dx = (this.size.getX() - newWidth) / 2;
        double dy = (this.size.getY() - newHeight) / 2;

        this.position.add(dx, dy);

        this.size.setX(newWidth);
        this.size.setY(newHeight);
    }

    /**
     * Définit le rayon des coins du rectangle (bordure arrondie).
     * 
     * @param rx le rayon according l'axe x
     * @param ry le rayon selon l'axe y
     * @throws IllegalArgumentException si les rayons sont négatifs ou supérieurs à
     *                                  la moitié des dimensions
     */
    public void radius(double rx, double ry) {
        if (rx < 0 || ry < 0 || rx > this.size.getX() / 2 || ry > this.size.getY() / 2) {
            throw new IllegalArgumentException(
                    "Invalid border radius values, must be between 0 and half of width/height");
        }
        this.radius = new Point(rx, ry);
    }

    /**
     * Crée une copie du rectangle.
     * 
     * @return une nouvelle instance de Rectangle avec les mêmes propriétés
     */
    @Override
    public IShape copy() {
        return new Rectangle((Point) this.position.copy(), this.size.getX(), this.size.getY());
    }

}
