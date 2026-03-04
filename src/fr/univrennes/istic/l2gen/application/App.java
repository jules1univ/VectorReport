package fr.univrennes.istic.l2gen.application;

// TODO: remove this file & add handlers for CLI/GUI

import java.io.File;
import java.util.List;

import fr.univrennes.istic.l2gen.geometry.Group;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Path;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.geometry.base.Circle;
import fr.univrennes.istic.l2gen.geometry.base.Ellipse;
import fr.univrennes.istic.l2gen.geometry.base.PolyLine;
import fr.univrennes.istic.l2gen.geometry.base.Polygon;
import fr.univrennes.istic.l2gen.geometry.base.Rectangle;
import fr.univrennes.istic.l2gen.geometry.base.Text;
import fr.univrennes.istic.l2gen.geometry.base.Triangle;
import fr.univrennes.istic.l2gen.io.svg.SVGExport;
import fr.univrennes.istic.l2gen.io.svg.SVGImport;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGShape;

/**
 * Classe principale de l'application de géométrie.
 * Gère la génération de fractales, leur export en SVG et leur réimport.
 * Enregistre toutes les formes géométriques pour l'export/import SVG.
 */
public class App {

    static {
        SVGImport.register(Point.class);
        SVGImport.register(Group.class);
        SVGImport.register(Path.class);

        SVGImport.register(Circle.class);
        SVGImport.register(Ellipse.class);
        SVGImport.register(PolyLine.class);
        SVGImport.register(Polygon.class);
        SVGImport.register(Rectangle.class);
        SVGImport.register(Text.class);
        SVGImport.register(Triangle.class);
    }

    /**
     * Méthode principale de l'application.
     * Génère une fractale, l'exporte en SVG, puis le réimporte pour validation.
     * Affiche les temps d'exécution de chaque étape.
     * 
     * @param args les arguments de la ligne de commande (non utilisés)
     * @throws Exception si une erreur se produit lors de l'export/import
     */
    public static void main(String[] args) throws Exception {
        // IMPORTANT: ne pas mettre a jour le code present ici
        // il faut séparer les fonctionnalité dans d'autre fichier de l'app puis les
        // charger ici

        long startTime = System.currentTimeMillis();
        IShape fractal = new Fractal().draw(new Triangle(new Point(500, 100), new Point(100, 900), new Point(900, 900)),
                5);

        IShape background = new Rectangle(0, 0, 1000, 1000);
        background.getStyle().fillColor(Color.WHITE);

        long endTime = System.currentTimeMillis();
        System.out.println("Fractal: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        File outputPath = new File("output/fractal.svg");
        outputPath.getParentFile().mkdirs();
        SVGExport.export(List.of(background, fractal), outputPath.getAbsolutePath(), 1000, 1000);
        endTime = System.currentTimeMillis();
        System.out.println("Export: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        List<ISVGShape> importShapes = SVGImport.load(outputPath.getAbsolutePath());
        endTime = System.currentTimeMillis();

        System.out.println("Import: " + (endTime - startTime) + " ms");

        if (importShapes.size() < 2) {
            System.out.println("IMPORT FAILED: expected 2 shapes, got " + importShapes.size());
            return;
        }

        ISVGShape backgroundSvgShape = importShapes.get(0);
        if (backgroundSvgShape instanceof IShape backgroundShape) {
            backgroundShape.getStyle().strokeColor()
                    .ifPresent(c -> System.out.println("Background stroke color: " + c));
            ;
        }

        ISVGShape fractalSvgShape = importShapes.get(1);
        if (fractalSvgShape instanceof IShape fractalShape) {
            boolean importSuccess = fractalShape.getDescription(0).equals(fractal.getDescription(0));
            if (importSuccess) {
                System.out.println("IMPORT SUCCESS");
            }
        }
    }
}
