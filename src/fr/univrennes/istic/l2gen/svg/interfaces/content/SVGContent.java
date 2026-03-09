package fr.univrennes.istic.l2gen.svg.interfaces.content;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation pour marquer un champ String comme contenu textuel d'une balise
 * SVG.
 * Utilisée lors de la sérialisation et désérialisation SVG.
 * Cette annotation a une rétention RUNTIME et est traitée par le compilateur.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SVGContent {

}
