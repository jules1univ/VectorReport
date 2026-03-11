package fr.univrennes.istic.l2gen.svg.attributes.style;

import java.util.Optional;

import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGAttribute;

/**
 * Représente un style SVG implémentant ISVGAttribute.
 * Gére l'apparence visuelle des formes SVG : couleurs, traits, polices, etc.
 * Les propriétés sont stockées en tant que Optional pour indiquer leur
 * présence.
 */
public final class SVGStyle implements ISVGAttribute {
    private Optional<Double> strokeWidth = Optional.empty();
    private Optional<Color> strokeColor = Optional.empty();
    private Optional<String> strokeDashArray = Optional.empty();
    private Optional<Color> fillColor = Optional.empty();

    private Optional<Double> fontSize = Optional.empty();
    private Optional<String> fontFamily = Optional.empty();
    private Optional<String> textAnchor = Optional.empty();
    private Optional<String> alignmentBaseline = Optional.empty();

    /**
     * Constructeur par défaut. Crée un style vide sans propriétés définies.
     */
    public SVGStyle() {

    }

    /**
     * Constructeur avec chaîne de style PARSant au format SVG CSS.
     * 
     * @param raw la chaîne de style au format CSS SVG (ex:
     *            "stroke-width:2;fill:#ff0000;")
     */
    public SVGStyle(String raw) {
        String[] declarations = raw.split(";");
        for (String declaration : declarations) {
            if (declaration.startsWith("stroke-width:")) {
                try {
                    double width = Double.parseDouble(declaration.substring("stroke-width:".length()));
                    this.strokeWidth = Optional.of(width);
                } catch (NumberFormatException e) {
                }
            } else if (declaration.startsWith("stroke:")) {
                Color color = Color.raw(declaration.substring("stroke:".length()));
                if (color != null) {
                    this.strokeColor = Optional.of(color);
                }
            } else if (declaration.startsWith("fill:")) {
                Color color = Color.raw(declaration.substring("fill:".length()));
                if (color != null) {
                    this.fillColor = Optional.of(color);
                }
            } else if (declaration.startsWith("font-size:")) {
                try {
                    double size = Double.parseDouble(declaration.substring("font-size:".length()));
                    this.fontSize = Optional.of(size);
                } catch (NumberFormatException e) {
                }
            } else if (declaration.startsWith("font-family:")) {
                String family = declaration.substring("font-family:".length());
                this.fontFamily = Optional.of(family);
            } else if (declaration.startsWith("text-anchor:")) {
                String anchor = declaration.substring("text-anchor:".length());
                this.textAnchor = Optional.of(anchor);
            } else if (declaration.startsWith("alignment-baseline:")) {
                String baseline = declaration.substring("alignment-baseline:".length());
                this.alignmentBaseline = Optional.of(baseline);
            } else if (declaration.startsWith("stroke-dasharray:")) {
                String dashArray = declaration.substring("stroke-dasharray:".length());
                this.strokeDashArray = Optional.of(dashArray);
            }
        }
    }

    /**
     * Définit la largeur du trait (stroke-width).
     * 
     * @param width la largeur du trait
     * @return cette instance pour enchainage de méthodes
     */
    public SVGStyle strokeWidth(double width) {
        this.strokeWidth = Optional.of(width);
        return this;
    }

    /**
     * Retourne la largeur du trait.
     * 
     * @return un Optional contenant la largeur ou vide si non défini
     */
    public Optional<Double> strokeWidth() {
        return strokeWidth;
    }

    /**
     * Définit la couleur du trait (stroke).
     * 
     * @param color la couleur du trait
     * @return cette instance pour enchainage de méthodes
     */
    public SVGStyle strokeColor(Color color) {
        this.strokeColor = Optional.of(color);
        return this;
    }

    /**
     * Retourne la couleur du trait.
     * 
     * @return un Optional contenant la couleur ou vide si non défini
     */
    public Optional<Color> strokeColor() {
        return strokeColor;
    }

    public SVGStyle strokeDashArray(int... values) {
        StringBuilder sb = new StringBuilder();
        for (int value : values) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(value);
        }
        this.strokeDashArray = Optional.of(sb.toString());
        return this;
    }

    public Optional<String> strokeDashArray() {
        return strokeDashArray;
    }

    /**
     * Définit la couleur de remplissage (fill).
     * 
     * @param color la couleur de remplissage
     * @return cette instance pour enchainage de méthodes
     */
    public SVGStyle fillColor(Color color) {
        this.fillColor = Optional.of(color);
        return this;
    }

    /**
     * Retourne la couleur de remplissage.
     * 
     * @return un Optional contenant la couleur ou vide si non défini
     */
    public Optional<Color> fillColor() {
        return fillColor;
    }

    /**
     * Définit la taille de la police (font-size).
     * 
     * @param size la taille de la police
     * @return cette instance pour enchainage de méthodes
     */
    public SVGStyle fontSize(double size) {
        this.fontSize = Optional.of(size);
        return this;
    }

    /**
     * Retourne la taille de la police.
     * 
     * @return un Optional contenant la taille ou vide si non défini
     */
    public Optional<Double> fontSize() {
        return fontSize;
    }

    /**
     * Définit la famille de police (font-family).
     * 
     * @param family la famille de police
     * @return cette instance pour enchainage de méthodes
     */
    public SVGStyle fontFamily(String family) {
        this.fontFamily = Optional.of(family);
        return this;
    }

    /**
     * Retourne la famille de police.
     * 
     * @return un Optional contenant la famille ou vide si non défini
     */
    public Optional<String> fontFamily() {
        return fontFamily;
    }

    /**
     * Définit l'ancrage du texte (text-anchor).
     * 
     * @param anchor la valeur d'ancrage du texte
     * @return cette instance pour enchainage de méthodes
     */
    public SVGStyle textAnchor(String anchor) {
        this.textAnchor = Optional.of(anchor);
        return this;
    }

    /**
     * Retourne l'ancrage du texte.
     * 
     * @return un Optional contenant la valeur ou vide si non défini
     */
    public Optional<String> textAnchor() {
        return textAnchor;
    }

    /**
     * Définit l'alignement de la baseline (alignment-baseline).
     * 
     * @param baseline la valeur d'alignement
     * @return cette instance pour enchainage de méthodes
     */
    public SVGStyle alignmentBaseline(String baseline) {
        this.alignmentBaseline = Optional.of(baseline);
        return this;
    }

    /**
     * Retourne l'alignement de la baseline.
     * 
     * @return un Optional contenant la valeur ou vide si non défini
     */
    public Optional<String> alignmentBaseline() {
        return alignmentBaseline;
    }

    /**
     * Vérifie si le style contient au moins une propriété définie.
     * 
     * @return true si au moins une propriété est présente, false sinon
     */
    @Override
    public boolean hasContent() {
        return strokeWidth.isPresent()
                || strokeColor.isPresent()
                || fillColor.isPresent()
                || fontSize.isPresent()
                || fontFamily.isPresent()
                || textAnchor.isPresent()
                || alignmentBaseline.isPresent()
                || strokeDashArray.isPresent();
    }

    /**
     * Retourne la représentation en chaîne CSS du style.
     * 
     * @return la chaîne au format CSS SVG
     */
    @Override
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        strokeWidth.ifPresent(w -> sb.append("stroke-width:").append(w).append(";"));
        strokeColor.ifPresent(c -> sb.append("stroke:").append(c).append(";"));
        strokeDashArray.ifPresent(d -> sb.append("stroke-dasharray:").append(d).append(";"));
        fillColor.ifPresent(c -> sb.append("fill:").append(c).append(";"));
        fontSize.ifPresent(s -> sb.append("font-size:").append(s).append(";"));
        fontFamily.ifPresent(f -> sb.append("font-family:").append(f).append(";"));
        textAnchor.ifPresent(a -> sb.append("text-anchor:").append(a).append(";"));
        alignmentBaseline.ifPresent(a -> sb.append("alignment-baseline:").append(a).append(";"));
        return sb.toString();
    }

    public SVGStyle copy() {
        SVGStyle copy = new SVGStyle();
        copy.strokeWidth = this.strokeWidth;
        copy.strokeColor = this.strokeColor;
        copy.fillColor = this.fillColor;
        copy.fontSize = this.fontSize;
        copy.fontFamily = this.fontFamily;
        copy.textAnchor = this.textAnchor;
        copy.alignmentBaseline = this.alignmentBaseline;
        copy.strokeDashArray = this.strokeDashArray;
        return copy;
    }
}
