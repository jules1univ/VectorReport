package fr.univrennes.istic.l2gen.application;

// TODO: remove this file

import java.util.List;

import fr.univrennes.istic.l2gen.geometry.Group;
import fr.univrennes.istic.l2gen.geometry.IShape;

/**
 * Classe pour générer des fractales récursives.
 * Utilise une forme de base et la divise en sous-formes résizées et
 * repositionnées.
 */
public final class Fractal {
    // IDrawer, IDraw, ect...

    /**
     * Constructeur par défaut pour la génération de fractales.
     */
    public Fractal() {
    }

    /**
     * Génère une fractale récursive à partir d'une forme de base.
     * Chaque niveau crée 4 copies résizées et repositionnées de la forme.
     * 
     * @param base   la forme de base pour la fractale
     * @param niveau le nombre de niveaux de récursion
     * @return un groupe contenant la fractale générée
     */
    public IShape draw(IShape base, int niveau) {
        if (niveau <= 0) {
            return new Group(List.of(base));
        }
        Group g = new Group();

        Group subG = new Group();
        subG.add(base);

        IShape topRight = base.copy();
        topRight.resize(0.5, 0.5);
        topRight.move(base.getWidth() / 4, -base.getHeight() / 2);
        topRight.move(topRight.getWidth() / 2, 0);
        subG.add(this.draw(topRight, niveau - 1));

        IShape bottomRight = base.copy();
        bottomRight.resize(0.5, 0.5);
        bottomRight.move(base.getWidth() / 4, base.getHeight() / 4);
        bottomRight.move(bottomRight.getWidth() / 2, bottomRight.getHeight() / 2);
        subG.add(this.draw(bottomRight, niveau - 1));

        IShape topLeft = base.copy();
        topLeft.resize(0.5, 0.5);
        topLeft.move(-base.getWidth() / 2, -base.getHeight() / 2);
        subG.add(this.draw(topLeft, niveau - 1));

        IShape bottomLeft = base.copy();
        bottomLeft.resize(0.5, 0.5);
        bottomLeft.move(-base.getWidth() / 2, base.getHeight() / 4);
        bottomLeft.move(0, bottomLeft.getHeight() / 2);
        subG.add(this.draw(bottomLeft, niveau - 1));

        g.add(subG);

        return g;
    }

}
