package fr.univrennes.istic.l2gen.geometry;

import fr.univrennes.istic.l2gen.svg.attributes.path.SVGPath;
import fr.univrennes.istic.l2gen.svg.attributes.path.util.BoundingBox;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Représente une forme basée sur un chemin SVG.
 * Permet de définir des formes complexes à l'aide de commandes de tracer de
 * chemins SVG.
 */
@SVGTag("path")
public final class Path extends AbstractShape {

    @SVGField("d")
    private SVGPath path;

    /**
     * Constructeur par défaut. Crée un chemin vide.
     */
    public Path() {
        this.path = new SVGPath();
    }

    public Path(SVGPath path) {
        if (path == null) {
            throw new IllegalArgumentException("SVGPath cannot be null");
        }
        this.path = path;
    }

    /**
     * Retourne le chemin SVG de cette forme.
     * 
     * @return le chemin SVG
     */
    public SVGPath draw() {
        return path;
    }

    /**
     * Retourne la largeur de la boîte englobante du chemin.
     * 
     * @return la largeur
     */
    @Override
    public double getWidth() {
        return this.path.getBoundingBox().getWidth();
    }

    /**
     * Retourne la hauteur de la boîte englobante du chemin.
     * 
     * @return la hauteur
     */
    @Override
    public double getHeight() {
        return this.path.getBoundingBox().getHeight();
    }

    /**
     * Retourne le centre de la forme (position du chemin).
     * 
     * @return le point centre
     */
    @Override
    public Point getCenter() {
        if (!this.path.hasContent()) {
            return new Point(0, 0);
        }

        BoundingBox box = this.path.getBoundingBox();
        return new Point(box.minX() + box.getWidth() / 2.0,
                box.minY() + box.getHeight() / 2.0);
    }

    /**
     * Retourne une description textuelle du chemin.
     * 
     * @param indent le niveau d'indentation pour le formatage
     * @return une description text
     */
    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDescription(indent));

        sb.append(" D=");
        sb.append(path.getContent());
        return sb.toString();
    }

    /**
     * Déplace le chemin d'une certaine distance.
     * 
     * @param dx le déplacement selon l'axe x
     * @param dy le déplacement selon l'axe y
     */
    @Override
    public void move(double dx, double dy) {
        this.path.translate(dx, dy);
    }

    public void moveCenter(Point center) {
        Point currentCenter = this.getCenter();
        double dx = center.getX() - currentCenter.getX();
        double dy = center.getY() - currentCenter.getY();
        this.move(dx, dy);
    }

    /**
     * Redimensionne la forme en appliquant la transformation d'échelle.
     * 
     * @param scaleX le facteur d'échelle selon l'axe x
     * @param scaleY le facteur d'échelle selon l'axe y
     */
    @Override
    public void resize(double scaleX, double scaleY) {
        this.transform.scale(scaleX, scaleY);
    }

    /**
     * Effectue une rotation de la forme autour de son centre.
     * 
     * @param degrees l'angle de rotation en degrés
     */
    @Override
    public void rotate(double degrees) {
        this.transform.rotate(degrees, this.getCenter().getX(), this.getCenter().getY());
    }

    /**
     * Crée une copie du chemin.
     * 
     * @return une nouvelle instance de Path avec le même chemin SVG
     */
    @Override
    public IShape copy() {
        return new Path(this.path);
    }

}