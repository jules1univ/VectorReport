package fr.univrennes.istic.l2gen.svg.interfaces.point;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation pour marquer une classe comme représentant un point SVG.
 * Utilisée avec les annotations {@link SVGPointX} et {@link SVGPointY}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SVGPoint {
}
