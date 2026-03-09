package fr.univrennes.istic.l2gen.svg.animations;

import java.util.Optional;

import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Élément d'animation SVG {@code <animateTransform>}.
 * Anime les attributs de transformation d'un élément SVG (translation,
 * rotation, échelle, skew).
 * Hérite des attributs de timing communs depuis {@link AbstractAnimate}.
 *
 * <p>
 * Exemple d'usage :
 * 
 * <pre>{@code
 * <animateTransform attributeName="transform" type="rotate"
 *     from="0 50 50" to="360 50 50" dur="5s" repeatCount="indefinite"/>
 * }</pre>
 */
@SVGTag("animateTransform")
public class SVGAnimateTransform extends AbstractAnimate {

    @SVGField
    private Optional<String> attributeName = Optional.empty();

    @SVGField
    private Optional<AnimationTransformType> type = Optional.empty();

    @SVGField
    private Optional<String> from = Optional.empty();

    @SVGField
    private Optional<String> to = Optional.empty();

    @SVGField
    private Optional<String> by = Optional.empty();

    @SVGField
    private Optional<Boolean> additive = Optional.empty();

    @SVGField
    private Optional<Boolean> accumulate = Optional.empty();

    /**
     * Constructeur par défaut. Crée une animation {@code <animateTransform>} avec
     * tous les paramètres vides.
     */
    public SVGAnimateTransform() {
    }

    /**
     * Définit le nom de l'attribut de transformation à animer.
     * Généralement {@code "transform"} ou {@code "gradientTransform"}.
     *
     * @param attributeName le nom de l'attribut (ex: "transform")
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateTransform attributeName(String attributeName) {
        this.attributeName = Optional.of(attributeName);
        return this;
    }

    /**
     * Retourne le nom de l'attribut de transformation animé.
     *
     * @return le nom de l'attribut, ou null si non défini
     */
    public String attributeName() {
        return attributeName.orElse(null);
    }

    /**
     * Définit le type de transformation à appliquer.
     *
     * @param type le type de transformation (ex: ROTATE, SCALE, TRANSLATE, SKEW_X,
     *             SKEW_Y)
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateTransform type(AnimationTransformType type) {
        this.type = Optional.of(type);
        return this;
    }

    /**
     * Retourne le type de transformation de l'animation.
     *
     * @return le type de transformation, ou null si non défini
     */
    public AnimationTransformType type() {
        return type.orElse(null);
    }

    /**
     * Définit la valeur initiale de la transformation.
     * Le format dépend du type : "cx cy" pour rotate, "sx sy" pour scale, etc.
     *
     * @param from la valeur initiale (ex: "0 50 50" pour une rotation)
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateTransform from(String from) {
        this.from = Optional.of(from);
        return this;
    }

    /**
     * Retourne la valeur initiale de la transformation.
     *
     * @return la valeur initiale, ou null si non définie
     */
    public String from() {
        return from.orElse(null);
    }

    /**
     * Définit la valeur finale de la transformation.
     *
     * @param to la valeur finale (ex: "360 50 50" pour une rotation complète)
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateTransform to(String to) {
        this.to = Optional.of(to);
        return this;
    }

    /**
     * Retourne la valeur finale de la transformation.
     *
     * @return la valeur finale, ou null si non définie
     */
    public String to() {
        return to.orElse(null);
    }

    /**
     * Définit le changement relatif de valeur pour la transformation (delta).
     *
     * @param by le changement de valeur
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateTransform by(String by) {
        this.by = Optional.of(by);
        return this;
    }

    /**
     * Retourne le changement relatif de valeur de la transformation.
     *
     * @return le changement, ou null si non défini
     */
    public String by() {
        return by.orElse(null);
    }

    /**
     * Définit si l'animation est additive par rapport à la valeur courante de
     * l'attribut.
     * Si {@code true}, correspond à {@code additive="sum"} ; sinon à
     * {@code additive="replace"}.
     *
     * @param additive true pour additionner, false pour remplacer
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateTransform additive(boolean additive) {
        this.additive = Optional.of(additive);
        return this;
    }

    /**
     * Retourne si l'animation est additive.
     *
     * @return true si additive, false si remplacement, null si non défini
     */
    public Boolean additive() {
        return additive.orElse(null);
    }

    /**
     * Définit si les répétitions de l'animation s'accumulent.
     * Si {@code true}, correspond à {@code accumulate="sum"} ; sinon à
     * {@code accumulate="none"}.
     *
     * @param accumulate true pour accumuler les valeurs entre répétitions
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateTransform accumulate(boolean accumulate) {
        this.accumulate = Optional.of(accumulate);
        return this;
    }

    /**
     * Retourne si les répétitions s'accumulent.
     *
     * @return true si accumulation active, false sinon, null si non défini
     */
    public Boolean accumulate() {
        return accumulate.orElse(null);
    }

    /**
     * Réinitialise tous les paramètres de l'animation à vide,
     * y compris les paramètres communs hérités.
     */
    @Override
    public void reset() {
        super.reset();
        attributeName = Optional.empty();
        type = Optional.empty();
        from = Optional.empty();
        to = Optional.empty();
        by = Optional.empty();
        additive = Optional.empty();
        accumulate = Optional.empty();
    }

}