package fr.univrennes.istic.l2gen.svg.interfaces.tag;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class SVGTagProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

                if (element.getKind() != ElementKind.CLASS) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "@SVGTag can only be applied to classes",
                            element);
                    continue;
                }
                // get the annotaion value if the value (string) is not an svg element, print an
                // error
                SVGTag svgTag = element.getAnnotation(SVGTag.class);
                String tagName = svgTag.value();
                if (tagName.isEmpty()) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "@SVGTag must have a non-empty value",
                            element);
                    continue;
                }

                // https://developer.mozilla.org/en-US/docs/Web/SVG/Reference/Element
                String[] validTags = {
                        "svg", "g", "defs", "symbol", "use", "switch", "marker",
                        "rect", "circle", "ellipse", "line", "polyline", "polygon", "path",
                        "text", "tspan", "textPath", "tref",
                        "image", "foreignObject",
                        "linearGradient", "radialGradient", "meshGradient", "meshrow", "meshpatch",
                        "pattern", "hatch", "hatchpath", "stop",
                        "clipPath", "mask",
                        "filter",
                        "feBlend", "feColorMatrix", "feComponentTransfer", "feComposite",
                        "feConvolveMatrix", "feDiffuseLighting", "feDisplacementMap", "feDropShadow",
                        "feFlood", "feGaussianBlur", "feImage", "feMerge", "feMergeNode",
                        "feMorphology", "feOffset", "feSpecularLighting", "feTile", "feTurbulence",
                        "feDistantLight", "fePointLight", "feSpotLight",
                        "feFuncR", "feFuncG", "feFuncB", "feFuncA",
                        "animate", "animateTransform", "animateMotion", "set", "discard", "mpath",
                        "a", "script", "style", "view",
                        "title", "desc", "metadata",
                        "color-profile", "cursor" };

                boolean isValidTag = false;
                String nearestValidTag = null;
                int minDistance = Integer.MAX_VALUE;
                for (String validTag : validTags) {
                    if (validTag.equals(tagName)) {
                        isValidTag = true;
                        break;
                    }

                    int distance = levenshteinDistance(tagName, validTag);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestValidTag = validTag;
                    }
                }

                if (!isValidTag) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "Invalid SVG tag name: " + tagName + ". Did you mean: " + nearestValidTag + "?",
                            element);
                }

            }
        }
        return true;
    }

    // https://stackoverflow.com/questions/13564464/problems-with-levenshtein-algorithm-in-java
    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j],
                            Math.min(dp[i][j - 1],
                                    dp[i - 1][j - 1]));
                }
            }
        }
        return dp[a.length()][b.length()];
    }

}