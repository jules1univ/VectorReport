package fr.univrennes.istic.l2gen.svg.interfaces.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour marquer les classes comme éléments SVG avec un nom de balise
 * spécifique.
 * Le nom de balise est utilisé lors de l'export et l'import SVG.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SVGTag {
    /**
     * Le nom de la balise SVG correspondant à cette classe.
     * 
     * @return le nom de la balise
     */
    String value();
}
