package fr.univrennes.istic.l2gen.geometry.base;

import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.geometry.AbstractShape;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Représente un polygone implémentant l'interface IShape.
 * Un polygone est défini par une liste de sommets.
 */
@SVGTag("polygon")
public final class Polygon extends AbstractShape {

    @SVGField("points")
    private final List<Point> vertices;

    /**
     * Constructeur par défaut. Crée un polygone vide.
     */
    public Polygon() {
        this.vertices = new ArrayList<>();
    }

    /**
     * Constructeur avec liste de coordonnées.
     * Les coordonnées doivent être en nombre pair (x1, y1, x2, y2, ...) pour former
     * des points.
     * 
     * @param coords les coordonnées des sommets
     * @throws IllegalArgumentException si le nombre de coordonnées est impair
     */
    public Polygon(double... coords) {
        this.vertices = new ArrayList<>();
        if (coords.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of coordinates");
        }
        for (int i = 0; i < coords.length; i += 2) {
            this.vertices.add(new Point(coords[i], coords[i + 1]));
        }
    }

    /**
     * Ajoute un sommet au polygone.
     * 
     * @param p le point à ajouter
     */
    public void addVertex(Point p) {
        vertices.add(new Point(p.getX(), p.getY()));
    }

    /**
     * Ajoute un sommet au polygone avec ses coordonnées.
     * 
     * @param x la coordonnée x
     * @param y la coordonnée y
     */
    public void addVertex(double x, double y) {
        vertices.add(new Point(x, y));
    }

    /**
     * Retourne la largeur du polygone (différence entre x max et x min).
     * 
     * @return la largeur, ou -1 si le polygone est vide
     */
    @Override
    public double getWidth() {
        if (vertices.isEmpty())
            return -1;

        double minX = vertices.get(0).getX();
        double maxX = minX;

        for (Point p : vertices) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
        }
        return maxX - minX;
    }

    /**
     * Retourne la hauteur du polygone (différence entre y max et y min).
     * 
     * @return la hauteur, ou -1 si le polygone est vide
     */
    @Override
    public double getHeight() {
        if (vertices.isEmpty())
            return -1;

        double minY = vertices.get(0).getY();
        double maxY = minY;

        for (Point p : vertices) {
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }
        return maxY - minY;
    }

    /**
     * Retourne le centre du polygone (moyenne des coordonnées de tous les sommets).
     * 
     * @return le point centre
     */
    @Override
    public Point getCenter() {
        if (vertices.isEmpty()) {
            return new Point(0, 0);
        }

        double totalX = 0;
        double totalY = 0;

        for (Point p : vertices) {
            totalX += p.getX();
            totalY += p.getY();
        }

        return new Point(totalX / vertices.size(), totalY / vertices.size());
    }

    /**
     * Retourne une description textuelle du polygone.
     * 
     * @param indent le nombre d'espaces pour l'indentation
     * @return une string décrivant le polygone
     */
    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDescription(indent));

        sb.append(" POINTS=");
        for (Point p : vertices) {
            sb.append(p.getX()).append(",").append(p.getY()).append(" ");
        }
        return sb.toString();
    }

    /**
     * Redimensionne le polygone en appliquant des facteurs d'échelle à partir du
     * centre.
     * 
     * @param dx le facteur d'échelle selon l'axe x
     * @param dy le facteur d'échelle selon l'axe y
     */
    @Override
    public void resize(double dx, double dy) {
        Point center = getCenter();

        for (Point p : vertices) {
            double newX = p.getX() + (p.getX() < center.getX() ? -dx : dx);
            double newY = p.getY() + (p.getY() < center.getY() ? -dy : dy);
            p.set(newX, newY);
        }
    }

    /**
     * Déplace le polygone d'une certaine distance.
     * 
     * @param dx le déplacement selon l'axe x
     * @param dy le déplacement selon l'axe y
     */
    @Override
    public void move(double dx, double dy) {
        for (Point p : vertices) {
            p.add(dx, dy);
        }
    }

    /**
     * Effectue une rotation du polygone autour de son centre.
     * 
     * @param deg l'angle de rotation en degrés
     */
    @Override
    public void rotate(double deg) {
        // Rotation manuelle sans utiliser transform

        Point center = getCenter();
        double radians = Math.toRadians(deg);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        for (Point p : vertices) {
            double translatedX = p.getX() - center.getX();
            double translatedY = p.getY() - center.getY();

            double rotatedX = translatedX * cos - translatedY * sin;
            double rotatedY = translatedX * sin + translatedY * cos;

            p.set(rotatedX + center.getX(), rotatedY + center.getY());
        }
    }

    /**
     * Crée une copie du polygone.
     * 
     * @return une nouvelle instance de Polygon avec les mêmes sommets
     */
    @Override
    public IShape copy() {
        Polygon copy = new Polygon();
        copy.vertices.addAll(this.vertices);
        return copy;
    }

}
