package fr.univrennes.istic.l2gen.svg.interfaces.content;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("fr.univrennes.istic.l2gen.svg.interfaces.content.SVGContent")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class SVGContentProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

                if (element.getKind() != ElementKind.FIELD) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "@SVGContent can only be applied to fields",
                            element);
                    continue;
                }

                VariableElement field = (VariableElement) element;
                TypeMirror fieldType = field.asType();

                TypeMirror stringType = processingEnv.getElementUtils()
                        .getTypeElement("java.lang.String")
                        .asType();

                if (!processingEnv.getTypeUtils().isSameType(fieldType, stringType)) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "@SVGContent can only be applied to String fields, but found: " + fieldType,
                            field);
                }
            }
        }
        return true;
    }

}