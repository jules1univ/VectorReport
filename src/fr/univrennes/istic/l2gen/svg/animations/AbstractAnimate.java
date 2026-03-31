package fr.univrennes.istic.l2gen.svg.animations;

import java.util.Optional;

import fr.univrennes.istic.l2gen.svg.attributes.style.SVGStyle;
import fr.univrennes.istic.l2gen.svg.attributes.transform.SVGTransform;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGShape;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;

/**
 * Classe abstraite regroupant les attributs communs à tous les éléments
 * d'animation SVG (animate, animateMotion, animateTransform).
 * Basée sur la spécification SMIL/SVG pour les attributs de timing et de
 * valeur.
 */
public abstract class AbstractAnimate implements ISVGShape {

    @SVGField
    private Optional<String> begin = Optional.empty();

    @SVGField
    private Optional<String> end = Optional.empty();

    @SVGField
    private Optional<AnimationDuration> dur = Optional.empty();

    @SVGField
    private Optional<AnimationDuration> min = Optional.empty();

    @SVGField
    private Optional<AnimationDuration> max = Optional.empty();

    @SVGField
    private Optional<AnimationRestart> restart = Optional.empty();

    @SVGField
    private Optional<AnimationCount> repeatCount = Optional.empty();

    @SVGField
    private Optional<AnimationDuration> repeatDur = Optional.empty();

    @SVGField
    private Optional<AnimationFill> fill = Optional.empty();

    /**
     * Définit quand l'animation doit commencer.
     *
     * @param begin le moment de début (ex: "0s", "click", "id.end")
     * @return cette animation (pour le chaînage)
     */
    public AbstractAnimate begin(String begin) {
        this.begin = Optional.of(begin);
        return this;
    }

    /**
     * Retourne le moment de début de l'animation.
     *
     * @return le moment de début, ou null si non défini
     */
    public String begin() {
        return begin.orElse(null);
    }

    /**
     * Définit quand l'animation doit se terminer.
     *
     * @param end le moment de fin
     * @return cette animation (pour le chaînage)
     */
    public AbstractAnimate end(String end) {
        this.end = Optional.of(end);
        return this;
    }

    /**
     * Retourne le moment de fin de l'animation.
     *
     * @return le moment de fin, ou null si non défini
     */
    public String end() {
        return end.orElse(null);
    }

    /**
     * Définit la durée de l'animation.
     *
     * @param dur la durée
     * @return cette animation (pour le chaînage)
     */
    public AbstractAnimate dur(AnimationDuration dur) {
        this.dur = Optional.of(dur);
        return this;
    }

    /**
     * Retourne la durée de l'animation.
     *
     * @return la durée, ou null si non définie
     */
    public AnimationDuration dur() {
        return dur.orElse(null);
    }

    /**
     * Définit la durée minimale de l'animation.
     *
     * @param min la durée minimale
     * @return cette animation (pour le chaînage)
     */
    public AbstractAnimate min(AnimationDuration min) {
        this.min = Optional.of(min);
        return this;
    }

    /**
     * Retourne la durée minimale de l'animation.
     *
     * @return la durée minimale, ou null si non définie
     */
    public AnimationDuration min() {
        return min.orElse(null);
    }

    /**
     * Définit la durée maximale de l'animation.
     *
     * @param max la durée maximale
     * @return cette animation (pour le chaînage)
     */
    public AbstractAnimate max(AnimationDuration max) {
        this.max = Optional.of(max);
        return this;
    }

    /**
     * Retourne la durée maximale de l'animation.
     *
     * @return la durée maximale, ou null si non définie
     */
    public AnimationDuration max() {
        return max.orElse(null);
    }

    /**
     * Définit le comportement de redémarrage de l'animation.
     *
     * @param restart le mode de redémarrage
     * @return cette animation (pour le chaînage)
     */
    public AbstractAnimate restart(AnimationRestart restart) {
        this.restart = Optional.of(restart);
        return this;
    }

    /**
     * Retourne le mode de redémarrage de l'animation.
     *
     * @return le mode de redémarrage, ou null si non défini
     */
    public AnimationRestart restart() {
        return restart.orElse(null);
    }

    /**
     * Définit le nombre de répétitions de l'animation.
     *
     * @param repeatCount le nombre de répétitions
     * @return cette animation (pour le chaînage)
     */
    public AbstractAnimate repeatCount(AnimationCount repeatCount) {
        this.repeatCount = Optional.of(repeatCount);
        return this;
    }

    /**
     * Retourne le nombre de répétitions de l'animation.
     *
     * @return le nombre de répétitions, ou null si non défini
     */
    public AnimationCount repeatCount() {
        return repeatCount.orElse(null);
    }

    /**
     * Définit la durée totale de répétition de l'animation.
     *
     * @param repeatDur la durée de répétition
     * @return cette animation (pour le chaînage)
     */
    public AbstractAnimate repeatDur(AnimationDuration repeatDur) {
        this.repeatDur = Optional.of(repeatDur);
        return this;
    }

    /**
     * Retourne la durée totale de répétition de l'animation.
     *
     * @return la durée de répétition, ou null si non définie
     */
    public AnimationDuration repeatDur() {
        return repeatDur.orElse(null);
    }

    /**
     * Définit le comportement de remplissage après la fin de l'animation.
     *
     * @param fill le mode de remplissage
     * @return cette animation (pour le chaînage)
     */
    public AbstractAnimate fill(AnimationFill fill) {
        this.fill = Optional.of(fill);
        return this;
    }

    /**
     * Retourne le mode de remplissage après la fin de l'animation.
     *
     * @return le mode de remplissage, ou null si non défini
     */
    public AnimationFill fill() {
        return fill.orElse(null);
    }

    /**
     * Réinitialise tous les paramètres communs de l'animation à vide.
     * Les sous-classes doivent appeler super.reset() pour réinitialiser
     * également leurs propres paramètres.
     */
    public void reset() {
        begin = Optional.empty();
        end = Optional.empty();
        dur = Optional.empty();
        min = Optional.empty();
        max = Optional.empty();
        restart = Optional.empty();
        repeatCount = Optional.empty();
        repeatDur = Optional.empty();
        fill = Optional.empty();
    }

    @Override
    public SVGStyle getStyle() {
        return null; // Les animations n'ont pas de style propre
    }

    @Override
    public SVGTransform getTransform() {
        return null; // Les animations n'ont pas de transformation propre
    }
}