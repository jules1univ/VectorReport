package fr.univrennes.istic.l2gen.svg.interfaces;

import fr.univrennes.istic.l2gen.svg.attributes.style.SVGStyle;
import fr.univrennes.istic.l2gen.svg.attributes.transform.SVGTransform;

/**
 * Interface marqueur pour les formes SVG.
 * Les classes implémentant cette interface représentent des éléments SVG
 * exportables et importables.
 */
public interface ISVGShape {

    SVGStyle getStyle();

    SVGTransform getTransform();
}
