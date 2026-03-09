package fr.univrennes.istic.l2gen.svg.interfaces.field;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation pour marquer les champs qui doivent être traités comme des
 * attributs SVG.
 * Permet de spécifier les noms d'attributs SVG correspondants.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SVGField {
    /**
     * Les noms d'attributs SVG correspondant à ce champ.
     * Si vide, le nom du champ sera utilisé comme nom d'attribut.
     * 
     * @return les noms des attributs SVG
     */
    String[] value() default {};
}
