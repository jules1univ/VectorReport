package fr.univrennes.istic.l2gen.io.svg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.univrennes.istic.l2gen.io.xml.model.XMLTag;
import fr.univrennes.istic.l2gen.io.xml.parser.XMLParser;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGAttribute;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGShape;
import fr.univrennes.istic.l2gen.svg.interfaces.content.SVGContent;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPoint;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPointX;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPointY;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Classe utilitaire pour importer des formes SVG depuis des structures XML.
 * Utilise la réflexion et un cache de constructeurs pour créer les objets Java.
 * Les formes doivent être enregistrées avant l'import via
 * {@link #register(Class)}.
 */
public final class SVGImport {
    private static final List<Class<? extends ISVGShape>> shapes = new ArrayList<>();

    private static Class<? extends ISVGShape> point = null;
    private static Field pointFieldX = null;
    private static Field pointFieldY = null;

    private static final Map<Class<?>, Constructor<?>> constructorCache = new HashMap<>();
    private static final Map<Class<?>, List<Field>> fieldCache = new HashMap<>();

    /**
     * Enregistre une classe de forme SVG pour l'importation.
     * La classe doit avoir un constructeur sans paramètres et quelques méthodes ne
     * sont supportées que si c'est un point SVG annoté avec @SVGPoint.
     * 
     * @param <T>   le type de la forme SVG
     * @param shape la classe à enregistrer
     * @throws IllegalArgumentException si la classe n'a pas de constructeur sans
     *                                  paramètres ou pas d'annotation @SVGTag
     */
    public static <T extends ISVGShape> void register(Class<T> shape) {
        Constructor<?> defaultConstructor = null;
        for (Constructor<?> constructor : shape.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                defaultConstructor = constructor;
                break;
            }
        }

        if (defaultConstructor == null) {
            throw new IllegalArgumentException(
                    "Shape class \"" + shape.getSimpleName() + "\" must have a default constructor");
        }

        defaultConstructor.setAccessible(true);
        constructorCache.put(shape, defaultConstructor);

        if (shape.getAnnotation(SVGPoint.class) != null) {
            point = shape;
            cachePointFields(shape);
            return;
        }

        if (shape.getAnnotation(SVGTag.class) == null) {
            throw new IllegalArgumentException(
                    "Shape class \"" + shape.getSimpleName() + "\" must be annotated with @SVGTag");
        }

        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = shape;
        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.getAnnotation(SVGField.class) != null
                        || field.getAnnotation(SVGContent.class) != null) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        fieldCache.put(shape, fields);
        shapes.add(shape);
    }

    /**
     * Cache les champs X et Y d'une classe de point SVG pour optimiser les
     * performances.
     * 
     * @param pointClass la classe du point annoté avec @SVGPoint
     */
    private static void cachePointFields(Class<? extends ISVGShape> pointClass) {
        for (Field field : pointClass.getDeclaredFields()) {
            if (field.getAnnotation(SVGPointX.class) != null) {
                field.setAccessible(true);
                pointFieldX = field;
            } else if (field.getAnnotation(SVGPointY.class) != null) {
                field.setAccessible(true);
                pointFieldY = field;
            }
        }
    }

    /**
     * Crée un point SVG à partir d'attributs XML mappés par un champ annoté.
     * Utilise deux attributs XML (coordonnée X et Y) pour construire le point.
     * 
     * @param pointField le champ annoté @SVGField contenant les noms des
     *                   attributs X et Y
     * @param tag        la balise XML contenant les attributs
     * @return le point créé, ou null si la création échoue
     */
    private static ISVGShape createPoint(SVGField pointField, XMLTag tag) {
        if (pointField.value().length < 2) {
            return null;
        }
        String pointX = pointField.value()[0];
        String pointY = pointField.value()[1];
        if (!tag.hasAttribute(pointX) || !tag.hasAttribute(pointY)) {
            return null;
        }

        String rawPoint = tag.getAttribute(pointX).getValue() + ","
                + tag.getAttribute(pointY).getValue();
        return createPoint(rawPoint);
    }

    /**
     * Crée un point SVG à partir d'une chaîne de coordonnées "x,y".
     * 
     * @param rawPoint la chaîne contenant les coordonnées séparées par une virgule
     * @return le point créé, ou null si le point n'est pas enregistré ou si la
     *         création échoue
     */
    private static ISVGShape createPoint(String rawPoint) {
        if (point == null || pointFieldX == null || pointFieldY == null) {
            return null;
        }

        try {
            ISVGShape pointShape = (ISVGShape) constructorCache.get(point).newInstance();
            String[] coords = rawPoint.split(",", 2);

            pointFieldX.set(pointShape, Double.parseDouble(coords[0]));
            pointFieldY.set(pointShape, Double.parseDouble(coords[1]));

            return pointShape;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Crée une liste de points SVG à partir d'une chaîne de coordonnées espacées.
     * Chaque paire "x,y" est séparée par un espace.
     * 
     * @param rawPoints la chaîne contenant les coordonnées espacées
     * @return la liste des points créés (peut être vide mais jamais null)
     */
    private static List<ISVGShape> createPointList(String rawPoints) {
        List<ISVGShape> points = new ArrayList<>();
        String[] rawPointsArray = rawPoints.split(" ");
        for (String rawPoint : rawPointsArray) {
            if (rawPoint.isEmpty()) {
                continue;
            }
            ISVGShape pointShape = createPoint(rawPoint);
            if (pointShape != null) {
                points.add(pointShape);
            }
        }
        return points;
    }

    /**
     * Charge une liste de formes SVG à partir d'un fichier SVG.
     * Analyse le fichier XML et reconstruit les formes enregistrées.
     * 
     * @param filename le chemin du fichier SVG à charger
     * @return la liste des formes chargées, ou une liste vide en cas d'erreur
     */
    public static List<ISVGShape> load(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            XMLParser parser = new XMLParser(br);

            XMLTag root = parser.parse();
            if (root == null || !root.getTagName().equals("svg")) {
                return new ArrayList<>();
            }

            List<ISVGShape> shapes = new ArrayList<>();
            for (XMLTag child : root.getChildren()) {
                ISVGShape shape = convert(child);
                if (shape != null) {
                    shapes.add(shape);
                }
            }
            return shapes;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Convertit une balise XML en une forme SVG.
     * La forme doit avoir été enregistrée au préalable avec
     * {@link #register(Class)}.
     * 
     * @param tag la balise XML contenant les données de la forme
     * @return la forme SVG recréée, ou null si la conversion a échoué
     */
    public static ISVGShape convert(XMLTag tag) {
        for (Class<? extends ISVGShape> shapeClass : shapes) {
            String tagName = shapeClass.getAnnotation(SVGTag.class).value();
            if (!tagName.equals(tag.getTagName())) {
                continue;
            }

            String className = shapeClass.getName();
            if (!tag.hasAttribute(SVGExport.DEFAULT_CLASS_TYPE_ATTR)
                    || !tag.getAttribute(SVGExport.DEFAULT_CLASS_TYPE_ATTR).getValue().equals(className)) {
                continue;
            }

            try {
                ISVGShape shape = (ISVGShape) constructorCache.get(shapeClass).newInstance();

                List<Field> fields = fieldCache.get(shapeClass);
                if (fields == null) {
                    fields = new ArrayList<>();
                    Class<?> currentClass = shapeClass;
                    while (currentClass != null) {
                        for (Field field : currentClass.getDeclaredFields()) {
                            if (field.getAnnotation(SVGField.class) != null
                                    || field.getAnnotation(SVGContent.class) != null) {
                                field.setAccessible(true);
                                fields.add(field);
                            }
                        }
                        currentClass = currentClass.getSuperclass();
                    }
                    fieldCache.put(shapeClass, fields);
                }

                for (Field shapeField : fields) {
                    SVGContent content = shapeField.getAnnotation(SVGContent.class);
                    if (content != null) {
                        shapeField.set(shape, tag.getTextContent().orElse(""));
                        continue;
                    }

                    SVGField attr = shapeField.getAnnotation(SVGField.class);

                    String attrName = attr.value().length > 0 ? attr.value()[0] : shapeField.getName();

                    Class<?> fieldType = shapeField.getType();
                    if (tag.hasAttribute(attrName)) {
                        String attrValue = tag.getAttribute(attrName).getValue();
                        if (attrValue == null) {
                            continue;
                        }

                        if (fieldType == String.class) {
                            shapeField.set(shape, attrValue);
                        } else if (fieldType == Integer.class || fieldType == int.class) {
                            shapeField.set(shape, Integer.parseInt(attrValue));
                        } else if (fieldType == Double.class || fieldType == double.class) {
                            shapeField.set(shape, Double.parseDouble(attrValue));
                        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                            shapeField.set(shape, Boolean.parseBoolean(attrValue));
                        } else if (fieldType.isEnum()) {
                            Object[] enumConstants = fieldType.getEnumConstants();
                            for (Object enumConstant : enumConstants) {
                                if (enumConstant.toString().equals(attrValue)) {
                                    shapeField.set(shape, enumConstant);
                                    break;
                                }
                            }
                        } else if (fieldType == Optional.class) {
                            Type[] genericTypes = ((ParameterizedType) shapeField.getGenericType())
                                    .getActualTypeArguments();
                            if (genericTypes.length == 0) {
                                continue;
                            }

                            Class<?> optionalValueType = (Class<?>) genericTypes[0];
                            if (optionalValueType == String.class) {
                                shapeField.set(shape, Optional.of(attrValue));
                            } else if (optionalValueType == Integer.class || optionalValueType == int.class) {
                                shapeField.set(shape, Optional.of(Integer.parseInt(attrValue)));
                            } else if (optionalValueType == Double.class || optionalValueType == double.class) {
                                shapeField.set(shape, Optional.of(Double.parseDouble(attrValue)));
                            } else if (optionalValueType == Boolean.class || optionalValueType == boolean.class) {
                                shapeField.set(shape, Optional.of(Boolean.parseBoolean(attrValue)));
                            } else if (optionalValueType.isEnum()) {
                                Object[] enumConstants = optionalValueType.getEnumConstants();
                                for (Object enumConstant : enumConstants) {
                                    if (enumConstant.toString().equals(attrValue)) {
                                        shapeField.set(shape, Optional.of(enumConstant));
                                        break;
                                    }
                                }
                            }
                        } else if (point != null && fieldType == point) {
                            shapeField.set(shape, createPoint(attr, tag));
                        } else if (point != null && fieldType == List.class) {
                            shapeField.set(shape, createPointList(attrValue));
                        } else if (ISVGAttribute.class.isAssignableFrom(fieldType)) {
                            Constructor<?> stringConstructor = null;
                            for (Constructor<?> constructor : fieldType.getConstructors()) {
                                if (constructor.getParameterCount() == 1
                                        && constructor.getParameterTypes()[0] == String.class) {
                                    stringConstructor = constructor;
                                    break;
                                }
                            }

                            if (stringConstructor != null) {
                                shapeField.set(shape, stringConstructor.newInstance(attrValue));
                            }
                        }
                    } else if (fieldType == List.class) {
                        List<ISVGShape> childrenShapes = new ArrayList<>();
                        for (XMLTag childTag : tag.getChildren()) {
                            if (!childTag.hasAttribute(SVGExport.DEFAULT_LIST_TYPE_ATTR)) {
                                continue;
                            }
                            if (!childTag.getAttribute(SVGExport.DEFAULT_LIST_TYPE_ATTR).getValue()
                                    .equals(shapeField.getName())) {
                                continue;
                            }

                            ISVGShape childShape = convert(childTag);
                            if (childShape != null) {
                                childrenShapes.add(childShape);
                            }
                        }
                        shapeField.set(shape, childrenShapes);
                    } else {
                        Constructor<?> emptyConstructor = null;
                        for (Constructor<?> constructor : fieldType.getConstructors()) {
                            if (constructor.getParameterCount() == 0) {
                                emptyConstructor = constructor;
                                break;
                            }
                        }
                        if (emptyConstructor != null) {
                            emptyConstructor.setAccessible(true);
                            for (XMLTag childTag : tag.getChildren()) {
                                if (!childTag.hasAttribute(SVGExport.DEFAULT_CLASS_TYPE_ATTR)) {
                                    continue;
                                }

                                if (childTag.getAttribute(SVGExport.DEFAULT_CLASS_TYPE_ATTR).getValue()
                                        .equals(fieldType.getName())) {
                                    ISVGShape convertedChildShape = convert(childTag);
                                    if (convertedChildShape != null) {
                                        shapeField.set(shape, convertedChildShape);
                                    }
                                    break;
                                }
                            }
                        }

                    }
                }
                return shape;
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }
}