package fr.univrennes.istic.l2gen.svg.interfaces.point;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation pour marquer un champ comme la coordonnée Y d'un point SVG.
 * Le champ annoté doit être numérique (int, double, float, etc.).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SVGPointY {
    /**
     * Le nom de l'attribut SVG pour la coordonnée Y.
     * Si vide, un nom par défaut sera utilisé.
     * 
     * @return le nom de l'attribut Y
     */
    public String value() default "";
}
