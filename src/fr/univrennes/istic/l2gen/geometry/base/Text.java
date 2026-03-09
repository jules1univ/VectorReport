package fr.univrennes.istic.l2gen.geometry.base;

import fr.univrennes.istic.l2gen.geometry.AbstractShape;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.attributes.style.SVGStyle;
import fr.univrennes.istic.l2gen.svg.interfaces.content.SVGContent;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Représente un texte implémentant l'interface IShape.
 * Un texte est une forme caractérisée par une chaîne de caractères et une
 * position.
 */
@SVGTag("text")
public final class Text extends AbstractShape {

    @SVGContent
    private final String text;

    @SVGField({ "x", "y" })
    private Point center;

    /**
     * Constructeur par défaut. Crée un texte vide à l'origine.
     */
    public Text() {
        this.center = new Point(0, 0);
        this.text = "";
    }

    /**
     * Constructeur avec position et contenu texte.
     * 
     * @param x    la coordonnée x du texte
     * @param y    la coordonnée y du texte
     * @param text la chaîne de caractères du texte
     */
    public Text(double x, double y, String text) {
        this.center = new Point(x, y);
        this.text = text;
    }

    /**
     * Constructeur avec position et contenu texte.
     * 
     * @param center la position du text
     * @param text   la chaîne de caractères du texte
     */
    public Text(Point center, String text) {
        this.center = center;
        this.text = text;
    }

    public Text(double x, double y, String text, SVGStyle style) {
        super(style);

        this.center = new Point(x, y);
        this.text = text;
    }

    /**
     * Retourne la largeur du texte (nombre de caractères).
     * 
     * @return la largeur
     */
    @Override
    public double getWidth() {
        return this.text.length() * this.getStyle().fontSize().orElse(1.);
    }

    /**
     * Retourne la hauteur du texte (convention : 1).
     * 
     * @return la hauteur
     */
    @Override
    public double getHeight() {
        return this.getStyle().fontSize().orElse(1.);
    }

    /**
     * Retourne la position du texte.
     * 
     * @return le point centre
     */
    @Override
    public Point getCenter() {
        return this.center;
    }

    /**
     * Retourne une description textuelle du texte avec sa position.
     * 
     * @param indentation le nombre d'espaces pour l'indentation
     * @return une string décrivant le texte
     */
    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDescription(indent));

        sb.append("VALUE=");
        sb.append(text);
        sb.append(" X=");
        sb.append(this.center.getX());
        sb.append(" Y=");
        sb.append(this.center.getY());
        return sb.toString();
    }

    /**
     * Déplace le texte d'une certaine distance.
     * 
     * @param dx le déplacement selon l'axe x
     * @param dy le déplacement selon l'axe y
     */
    @Override
    public void move(double dx, double dy) {
        this.center.add(dx, dy);
    }

    /**
     * Redimensionne le texte en appliquant des facteurs d'échelle de mise à
     * l'échelle.
     * 
     * @param px le facteur d'échelle selon l'axe x
     * @param py le facteur d'échelle selon l'axe y
     */
    @Override
    public void resize(double px, double py) {
        this.transform.scale(px, py);
    }

    /**
     * Crée une copie du texte.
     * 
     * @return une nouvelle instance de Text avec le même contenu et la même
     *         position
     */
    @Override
    public IShape copy() {
        Text copy = new Text(this.center.getX(), this.center.getY(), this.text, this.style.copy());
        return copy;
    }

}
