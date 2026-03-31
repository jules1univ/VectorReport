package fr.univrennes.istic.l2gen.svg.interfaces.point;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPoint")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class SVGPointProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

                if (element.getKind() != ElementKind.CLASS) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "@SVGPoint can only be applied to classes",
                            element);
                    continue;
                }

                TypeElement classElement = (TypeElement) element;

                boolean foundX = false;
                boolean foundY = false;
                for (Element field : classElement.getEnclosedElements()) {
                    if (field.getKind() == ElementKind.FIELD) {
                        if (field.getAnnotation(SVGPointX.class) != null) {
                            TypeMirror fieldType = field.asType();
                            if (!fieldType.toString().equals("double")) {
                                processingEnv.getMessager().printMessage(
                                        Diagnostic.Kind.ERROR,
                                        "@SVGPointX can only be applied to double fields, but found: " + fieldType,
                                        field);
                            }
                            foundX = true;
                        } else if (field.getAnnotation(SVGPointY.class) != null) {
                            TypeMirror fieldType = field.asType();
                            if (!fieldType.toString().equals("double")) {
                                processingEnv.getMessager().printMessage(
                                        Diagnostic.Kind.ERROR,
                                        "@SVGPointY can only be applied to double fields, but found: " + fieldType,
                                        field);
                            }
                            foundY = true;
                        }
                    }

                    if (!foundX) {
                        processingEnv.getMessager().printMessage(
                                Diagnostic.Kind.ERROR,
                                "No field annotated with @SVGPointX found in class annotated with @SVGPoint",
                                classElement);
                    }

                    if (!foundY) {
                        processingEnv.getMessager().printMessage(
                                Diagnostic.Kind.ERROR,
                                "No field annotated with @SVGPointY found in class annotated with @SVGPoint",
                                classElement);
                    }

                }
            }
        }
        return true;
    }

}