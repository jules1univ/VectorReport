package fr.univrennes.istic.l2gen.geometry.base;

import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.geometry.AbstractShape;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Représente un triangle implémentant l'interface IShape.
 * Un triangle est défini par trois sommets (points).
 */
@SVGTag("polygon")
public final class Triangle extends AbstractShape {

    @SVGField("points")
    private final List<Point> vertices;

    /**
     * Constructeur par défaut. Crée un triangle équilatéral avec tous les sommets à
     * l'origine.
     */
    public Triangle() {
        this.vertices = new ArrayList<>(3);
        this.vertices.add(new Point(0, 0));
        this.vertices.add(new Point(0, 0));
        this.vertices.add(new Point(0, 0));
    }

    /**
     * Constructeur avec coordonnées des trois sommets.
     * 
     * @param x1 la coordonnée x du premier sommet
     * @param y1 la coordonnée y du premier sommet
     * @param x2 la coordonnée x du deuxième sommet
     * @param y2 la coordonnée y du deuxième sommet
     * @param x3 la coordonnée x du troisième sommet
     * @param y3 la coordonnée y du troisième sommet
     */
    public Triangle(double x1, double y1,
            double x2, double y2,
            double x3, double y3) {
        this(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3));
    }

    /**
     * Constructeur avec trois points.
     * 
     * @param p1 le premier sommet
     * @param p2 le deuxième sommet
     * @param p3 le troisième sommet
     */
    public Triangle(Point p1, Point p2, Point p3) {
        this.vertices = new ArrayList<>(3);
        this.vertices.add(new Point(p1.getX(), p1.getY()));
        this.vertices.add(new Point(p2.getX(), p2.getY()));
        this.vertices.add(new Point(p3.getX(), p3.getY()));
    }

    /**
     * Retourne une liste non modifiable des sommets du triangle.
     * 
     * @return une copie de la liste des sommets
     */
    public List<Point> getVertices() {
        return List.copyOf(vertices);
    }

    /**
     * Retourne la largeur du triangle (différence entre x max et x min).
     * 
     * @return la largeur
     */
    @Override
    public double getWidth() {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        for (Point p : vertices) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
        }
        return maxX - minX;
    }

    /**
     * Retourne la hauteur du triangle (différence entre y max et y min).
     * 
     * @return la hauteur
     */
    @Override
    public double getHeight() {
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Point p : vertices) {
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }
        return maxY - minY;
    }

    /**
     * Retourne le centre du triangle (barycentre, moyenne des trois sommets).
     * 
     * @return le point centre
     */
    @Override
    public Point getCenter() {
        double sumX = 0.0;
        double sumY = 0.0;
        for (Point p : vertices) {
            sumX += p.getX();
            sumY += p.getY();
        }
        return new Point(sumX / 3.0, sumY / 3.0);
    }

    /**
     * Retourne une description textuelle du triangle avec la liste de ses sommets.
     * 
     * @param indent le nombre d'espaces pour l'indentation
     * @return une string décrivant le triangle
     */
    @Override
    public String getDescription(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDescription(indent));

        sb.append(" POINTS=");
        for (Point p : vertices) {
            sb.append(" ").append(p.getX()).append(",").append(p.getY());
        }
        return sb.toString();
    }

    /**
     * Déplace le triangle d'une certaine distance.
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
     * Redimensionne le triangle en appliquant des facteurs d'échelle à partir du
     * centre.
     * 
     * @param sx le facteur d'échelle selon l'axe x
     * @param sy le facteur d'échelle selon l'axe y
     */
    @Override
    public void resize(double sx, double sy) {
        Point center = getCenter();
        for (int i = 0; i < vertices.size(); i++) {
            vertices.set(i, scalePoint(vertices.get(i), center, sx, sy));
        }
    }

    /**
     * Effectue une rotation du triangle autour de son centre.
     * 
     * @param deg l'angle de rotation en degrés
     */
    @Override
    public void rotate(double deg) {
        // Rotation manuelle sans utiliser transform

        Point center = getCenter();
        double rad = Math.toRadians(deg);
        for (int i = 0; i < vertices.size(); i++) {
            vertices.set(i, rotatePoint(vertices.get(i), center, rad));
        }
    }

    /**
     * Calcule le point result de la mise à l'échelle d'un point par rapport à un
     * centre.
     * 
     * @param p  le point à redimensionner
     * @param c  le centre de référence
     * @param sx le facteur d'échelle en x
     * @param sy le facteur d'échelle en y
     * @return le point redéfini
     */
    private Point scalePoint(Point p, Point c, double sx, double sy) {
        return new Point(
                c.getX() + (p.getX() - c.getX()) * sx,
                c.getY() + (p.getY() - c.getY()) * sy);
    }

    /**
     * Calcule le point résultant de la rotation d'un point autour d'un centre.
     * 
     * @param point  le point à faire pivoter
     * @param center le centre de rotation
     * @param rad    l'angle de rotation en radians
     * @return le point résultant de la rotation
     */
    private Point rotatePoint(Point point, Point center, double rad) {
        double cosTheta = Math.cos(rad);
        double sinTheta = Math.sin(rad);
        double translatedX = point.getX() - center.getX();
        double translatedY = point.getY() - center.getY();

        double rotatedX = translatedX * cosTheta - translatedY * sinTheta;
        double rotatedY = translatedX * sinTheta + translatedY * cosTheta;

        return new Point(
                rotatedX + center.getX(),
                rotatedY + center.getY());
    }

    /**
     * Crée une copie du triangle.
     * 
     * @return une nouvelle instance de Triangle avec les mêmes sommets
     */
    @Override
    public IShape copy() {
        return new Triangle(vertices.get(0), vertices.get(1), vertices.get(2));
    }
}
