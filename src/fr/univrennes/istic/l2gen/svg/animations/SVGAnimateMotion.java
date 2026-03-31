package fr.univrennes.istic.l2gen.svg.animations;

import java.util.Optional;

import fr.univrennes.istic.l2gen.svg.attributes.path.SVGPath;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Élément d'animation SVG {@code <animateMotion>}.
 * Déplace un élément SVG le long d'un chemin de mouvement au fil du temps.
 * Hérite des attributs de timing communs depuis {@link AbstractAnimate}.
 *
 * <p>
 * Exemple d'usage :
 * 
 * <pre>{@code
 * <animateMotion path="M 0 0 L 100 100" dur="3s" repeatCount=
"indefinite" rotate="auto"/>
 * }</pre>
 */
@SVGTag("animateMotion")
public class SVGAnimateMotion extends AbstractAnimate {

    @SVGField
    private Optional<SVGPath> path = Optional.empty();

    @SVGField
    private Optional<String> keyPoints = Optional.empty();

    @SVGField
    private Optional<String> rotate = Optional.empty();

    @SVGField
    private Optional<String> origin = Optional.empty();

    /**
     * Constructeur par défaut. Crée une animation {@code <animateMotion>} avec tous
     * les paramètres vides.
     */
    public SVGAnimateMotion() {
    }

    /**
     * Définit le chemin le long duquel l'élément se déplace.
     * Utilise la même syntaxe que l'attribut {@code d} d'un élément {@code <path>}.
     *
     * @param path le chemin de mouvement (ex: "M 0 0 C 50 -50 100 50 150 0")
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateMotion path(SVGPath path) {
        this.path = Optional.of(path);
        return this;
    }

    /**
     * Retourne le chemin de mouvement de l'animation.
     *
     * @return le chemin, ou null si non défini
     */
    public SVGPath path() {
        return path.orElse(null);
    }

    /**
     * Définit les points clés sur le chemin correspondant aux {@code keyTimes}.
     * Liste de valeurs entre 0 et 1 séparées par des points-virgules.
     *
     * @param keyPoints les points clés (ex: "0; 0.5; 1")
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateMotion keyPoints(String keyPoints) {
        this.keyPoints = Optional.of(keyPoints);
        return this;
    }

    /**
     * Retourne les points clés de l'animation.
     *
     * @return les points clés, ou null si non définis
     */
    public String keyPoints() {
        return keyPoints.orElse(null);
    }

    /**
     * Définit la rotation de l'élément au cours du déplacement.
     * Valeurs possibles : "auto" (aligne sur la direction du chemin),
     * "auto-reverse", ou un angle en degrés.
     *
     * @param rotate la valeur de rotation (ex: "auto", "90")
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateMotion rotate(String rotate) {
        this.rotate = Optional.of(rotate);
        return this;
    }

    /**
     * Retourne la rotation appliquée durant l'animation.
     *
     * @return la rotation, ou null si non définie
     */
    public String rotate() {
        return rotate.orElse(null);
    }

    /**
     * Définit l'origine du mouvement.
     * Généralement "default" selon la spécification SVG.
     *
     * @param origin l'origine du mouvement
     * @return cette animation (pour le chaînage)
     */
    public SVGAnimateMotion origin(String origin) {
        this.origin = Optional.of(origin);
        return this;
    }

    /**
     * Retourne l'origine du mouvement.
     *
     * @return l'origine, ou null si non définie
     */
    public String origin() {
        return origin.orElse(null);
    }

    /**
     * Réinitialise tous les paramètres de l'animation à vide,
     * y compris les paramètres communs hérités.
     */
    @Override
    public void reset() {
        super.reset();
        path = Optional.empty();
        keyPoints = Optional.empty();
        rotate = Optional.empty();
        origin = Optional.empty();
    }

}