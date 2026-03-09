package fr.univrennes.istic.l2gen.svg.animations;

import java.util.Optional;

import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Élément d'animation SVG {@code <animate>}.
 * Permet d'animer un attribut spécifique d'un élément SVG de manière
 * progressive.
 * Hérite des attributs de timing communs depuis {@link AbstractAnimate}.
 *
 * <p>
 * Exemple d'usage :
 * 
 * <pre>{@code
 * <animate attributeName="cx" from="50" to="200" dur="2s" repeatCount=
"indefinite"/>
 * }</pre>
 */
@SVGTag("animate")
public class SVGAnimate extends AbstractAnimate {

    @SVGField
    protected Optional<String> attributeName = Optional.empty();

    @SVGField
    private Optional<String> from = Optional.empty();

    @SVGField
    private Optional<String> to = Optional.empty();

    @SVGField
    private Optional<String> by = Optional.empty();

    /**
     * Constructeur par défaut. Crée une animation {@code <animate>} avec tous les
     * paramètres vides.
     */
    public SVGAnimate() {
    }

    /**
     * Définit le nom de l'attribut SVG à animer.
     *
     * @param attributeName le nom de l'attribut (ex: "cx", "opacity", "fill")
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimate attributeName(String attributeName) {
        this.attributeName = Optional.of(attributeName);
        return this;
    }

    /**
     * Retourne le nom de l'attribut animé.
     *
     * @return le nom de l'attribut, ou null si non défini
     */
    public String attributeName() {
        return attributeName.orElse(null);
    }

    /**
     * Définit la valeur initiale de l'animation.
     *
     * @param from la valeur initiale
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimate from(String from) {
        this.from = Optional.of(from);
        return this;
    }

    /**
     * Retourne la valeur initiale de l'animation.
     *
     * @return la valeur initiale, ou null si non définie
     */
    public String from() {
        return from.orElse(null);
    }

    /**
     * Définit la valeur finale de l'animation.
     *
     * @param to la valeur finale
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimate to(String to) {
        this.to = Optional.of(to);
        return this;
    }

    /**
     * Retourne la valeur finale de l'animation.
     *
     * @return la valeur finale, ou null si non définie
     */
    public String to() {
        return to.orElse(null);
    }

    /**
     * Définit le changement relatif de valeur pour l'animation (delta).
     * Utilisé à la place de {@code to} pour spécifier une variation relative.
     *
     * @param by le changement de valeur
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimate by(String by) {
        this.by = Optional.of(by);
        return this;
    }

    /**
     * Retourne le changement relatif de valeur de l'animation.
     *
     * @return le changement, ou null si non défini
     */
    public String by() {
        return by.orElse(null);
    }

    /**
     * Réinitialise tous les paramètres de l'animation à vide,
     * y compris les paramètres communs hérités.
     */
    @Override
    public void reset() {
        super.reset();
        attributeName = Optional.empty();
        from = Optional.empty();
        to = Optional.empty();
        by = Optional.empty();
    }

}