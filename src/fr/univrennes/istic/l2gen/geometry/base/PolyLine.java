package fr.univrennes.istic.l2gen.geometry.base;

import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.geometry.AbstractShape;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Représente une polyligne implémentant l'interface IShape.
 * Une polyligne est définie par une séquence de segments de ligne reliant une
 * suite de points.
 */
@SVGTag("polyline")
public final class PolyLine extends AbstractShape {

    @SVGField("points")
    private final List<Point> vertices;

    /**
     * Constructeur par défaut. Crée une polyligne vide.
     */
    public PolyLine() {
        this.vertices = new ArrayList<>();
    }

    /**
     * constructeur de la classe Line .Ce constructeur crée une Line en utilisant la
     * liste de double pris en paramètre
     * 2 doubles = 1 point
     * 
     * @param coords Les coordonnées (x, y) des points de Line. Les coordonnées
     *               doivent être fournies sous forme de
     *               doubles, où chaque paire consécutive représente les coordonnées
     *               d'un point.
     */
    public PolyLine(double... coords) {
        if (coords.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of coordinates");
        }
        this.vertices = new ArrayList<>();
        for (int i = 0; i < coords.length; i += 2) {
            this.vertices.add(new Point(coords[i], coords[i + 1]));
        }
    }

    /**
     * ajoute un Point à la fin de la liste de Point vertices.
     * 
     * @param pt le Point à ajouter à la fin de la liste.
     */
    public void addVertex(Point pt) {
        this.vertices.add(new Point(pt.getX(), pt.getY()));
    }

    /**
     * ajoute un point a la fin de la liste vertices grace a des double x et y
     * 
     * @param x coordonnée x du Point à ajouter
     * @param y coordonnée y du Point à ajouter
     */
    public void addVertex(double x, double y) {
        this.vertices.add(new Point(x, y));
    }

    /**
     * modifie le Point à l'index spécifié dans la liste vertices.
     * 
     * @param index l'index du Point à modifier
     * @param pt    le nouveau Point à placer à l'index spécifié
     */
    public void setVertex(int index, Point pt) {
        this.vertices.set(index, new Point(pt.getX(), pt.getY()));
    }

    /**
     * calcule et retourne la largeur de la Line en trouvant les coordonnées x
     * minimales et maximales parmi tous les Points dans la liste vertices.
     * 
     * @return la largeur de la Line
     */
    @Override
    public double getWidth() {
        if (vertices.isEmpty())
            return 0;
        double minX = vertices.get(0).getX();
        double maxX = minX;
        for (Point p : vertices) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
        }
        return maxX - minX;
    }

    /**
     * calcule et retourne la hauteur de la Line en trouvant les coordonnées y
     * minimales et maximales parmi tous les Points dans la liste vertices.
     * 
     * @return la hauteur de la Line
     */
    @Override
    public double getHeight() {
        if (vertices.isEmpty())
            return 0;
        double minY = vertices.get(0).getY();
        double maxY = minY;
        for (Point p : vertices) {
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }
        return maxY - minY;
    }

    /**
     * calcule et retourne le centre de la Line en calculant la moyenne des
     * coordonnées x et y de tous les Points dans la liste vertices.
     * 
     * @return un Point représentant le centre de la Line
     */
    @Override
    public Point getCenter() {
        double sumX = 0, sumY = 0;
        for (Point p : vertices) {
            sumX += p.getX();
            sumY += p.getY();
        }
        int n = vertices.size();
        return n > 0 ? new Point(sumX / n, sumY / n) : new Point(0, 0);
    }

    /**
     * génère une description textuelle de la Line avec un certain niveau
     * d'indentation.
     * 
     * @param indent le nombre d'espaces à utiliser pour l'indentation
     * @return une chaîne de caractères décrivant la Line et ses Points
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
     * déplace la Line en ajoutant les valeurs dx et dy aux coordonnées x et y de
     * chaque Point dans la liste vertices.
     * 
     * @param dx le déplacement en x
     * @param dy le déplacement en y
     */
    @Override
    public void move(double dx, double dy) {
        for (Point p : vertices) {
            p.add(dx, dy);
        }
    }

    /**
     * redimensionne la Line en fonction des facteurs sx et sy en utilisant le
     * centre de
     * la Line comme point de référence.
     * 
     * @param sx le facteur d'échelle en x
     * @param sy le facteur d'échelle en y
     */
    @Override
    public void resize(double sx, double sy) {
        Point center = getCenter();
        for (int i = 0; i < vertices.size(); i++) {
            Point p = vertices.get(i);
            Point scaled = new Point(
                    center.getX() + (p.getX() - center.getX()) * sx,
                    center.getY() + (p.getY() - center.getY()) * sy);
            vertices.set(i, scaled);
        }
    }

    /**
     * fait pivoter la Line autour de son centre d'un certain angle en degrés.
     * 
     * @param deg l'angle de rotation en degrés
     */
    @Override
    public void rotate(double deg) {
        // Rotation manuelle sans utiliser transform
        Point center = getCenter();
        double rad = Math.toRadians(deg);

        double cosTheta = Math.cos(rad);
        double sinTheta = Math.sin(rad);

        for (int i = 0; i < vertices.size(); i++) {
            Point p = vertices.get(i);
            double translatedX = p.getX() - center.getX();
            double translatedY = p.getY() - center.getY();

            double rotatedX = translatedX * cosTheta - translatedY * sinTheta;
            double rotatedY = translatedX * sinTheta + translatedY * cosTheta;

            Point rotatedPoint = new Point(
                    rotatedX + center.getX(),
                    rotatedY + center.getY());
            vertices.set(i, rotatedPoint);
        }
    }

    /**
     * crée et retourne une copie de la Line actuelle.
     * 
     * @return une nouvelle instance de Line avec les mêmes Points que l'original
     */
    @Override
    public IShape copy() {
        PolyLine copie = new PolyLine();
        copie.vertices.addAll(this.vertices);
        return copie;
    }
}
