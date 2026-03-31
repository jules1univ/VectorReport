package fr.univrennes.istic.l2gen.io.svg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.univrennes.istic.l2gen.io.xml.model.XMLAttribute;
import fr.univrennes.istic.l2gen.io.xml.model.XMLTag;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGAttribute;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGShape;
import fr.univrennes.istic.l2gen.svg.interfaces.content.SVGContent;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPoint;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPointX;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPointY;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

/**
 * Classe utilitaire pour exporter des formes SVG (ISVGShape) en structures XML.
 * Utilise la réflexion et les annotations pour convertir les objets Java en
 * balises XML.
 * Maintient des caches pour optimiser les performances.
 */
public final class SVGExport {
    public static final String DEFAULT_CLASS_TYPE_ATTR = "jclass-data";
    public static final String DEFAULT_LIST_TYPE_ATTR = "jlist-data";

    private static final Map<Class<?>, List<Field>> fieldCache = new HashMap<>();
    private static final Map<Class<?>, Field[]> pointFieldsCache = new HashMap<>();

    /**
     * Extrait les coordonnées X et Y d'un point SVG sous forme de chaîne "x,y".
     * Le point doit être annoté avec @SVGPoint et ses champs avec @SVGPointX et
     * @SVGPointY.
     * 
     * @param point l'objet point à convertir
     * @return les coordonnées formatées "x,y", ou null si le point est invalide
     */
    private static String getObjectPointValue(Object point) {
        if (point == null) {
            return null;
        }

        Class<?> pointClass = point.getClass();

        if (pointClass.getAnnotation(SVGPoint.class) == null) {
            return null;
        }

        Field[] pointFields = pointFieldsCache.get(pointClass);
        if (pointFields == null) {
            Field xField = null;
            Field yField = null;

            for (Field field : pointClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(SVGPointX.class)) {
                    field.setAccessible(true);
                    xField = field;
                } else if (field.isAnnotationPresent(SVGPointY.class)) {
                    field.setAccessible(true);
                    yField = field;
                }
            }

            if (xField != null && yField != null) {
                pointFields = new Field[] { xField, yField };
                pointFieldsCache.put(pointClass, pointFields);
            } else {
                return null;
            }
        }

        try {
            String xValue = pointFields[0].get(point).toString();
            String yValue = pointFields[1].get(point).toString();
            return xValue + "," + yValue;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convertit une liste de points SVG en une chaîne de coordonnées espacées.
     * Chaque point est converti en "x,y" et séparé par un espace.
     * 
     * @param pointsList la liste des points À convertir
     * @return la chaîne de coordonnées formatée, ou une chaîne vide si la liste
     *         est vide
     */
    private static String getObjectPoints(List<?> pointsList) {
        if (pointsList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Object point : pointsList) {
            String value = getObjectPointValue(point);
            if (value != null) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(value);
            }
        }
        return sb.toString();
    }

    /**
     * Convertit une forme SVG (implémentant ISVGShape) en une balise XML.
     * Utilise les annotations @SVGTag, @SVGField et @SVGContent pour guider la
     * conversion.
     * 
     * @param shape la forme SVG à exporter
     * @return une balise XML représentant la forme
     * @throws IllegalArgumentException si la classe ne porte pas
     *                                  l'annotation @SVGTag
     */
    public static XMLTag convert(ISVGShape shape) {
        Class<?> shapeClass = shape.getClass();
        SVGTag tagName = shapeClass.getAnnotation(SVGTag.class);
        if (tagName == null) {
            throw new IllegalArgumentException(
                    "Class " + shapeClass.getSimpleName() + " must be annotated with @SVGTag");
        }

        XMLTag tag = new XMLTag(tagName.value());
        tag.addAttribute(new XMLAttribute(DEFAULT_CLASS_TYPE_ATTR, shapeClass.getName()));

        List<Field> fields = fieldCache.get(shapeClass);
        if (fields == null) {
            fields = new ArrayList<>();
            Class<?> currentClass = shapeClass;
            while (currentClass != null) {
                for (Field field : currentClass.getDeclaredFields()) {
                    if (field.getAnnotation(SVGField.class) != null || field.getAnnotation(SVGContent.class) != null) {
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
                Object value;
                try {
                    value = shapeField.get(shape);
                    if (value != null) {
                        tag.setTextContent(value.toString());
                    }
                } catch (Exception e) {
                }
                continue;
            }

            SVGField attr = shapeField.getAnnotation(SVGField.class);
            Object value;
            try {
                value = shapeField.get(shape);
                if (value == null) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }

            String attrName = attr.value().length > 0 ? attr.value()[0] : shapeField.getName();
            if (value instanceof List<?> listObj) {
                if (listObj.isEmpty()) {
                    continue;
                }

                Class<?> listValueType = listObj.get(0).getClass();
                if (listValueType.getAnnotation(SVGPoint.class) != null) {
                    String pointsValue = getObjectPoints(listObj);
                    if (!pointsValue.isEmpty()) {
                        tag.addAttribute(new XMLAttribute(attrName, pointsValue));
                    }
                } else if (ISVGShape.class.isAssignableFrom(listValueType)) {
                    for (Object childShape : listObj) {
                        if (childShape instanceof ISVGShape svgShape) {
                            XMLTag childTag = convert(svgShape);
                            childTag.addAttribute(new XMLAttribute(DEFAULT_LIST_TYPE_ATTR, shapeField.getName()));
                            tag.appendChild(childTag);
                        }
                    }
                }

            } else if (value.getClass().getAnnotation(SVGPoint.class) != null && attr.value().length == 2) {
                String pointValue = getObjectPointValue(value);
                if (pointValue != null) {
                    int commaIndex = pointValue.indexOf(',');
                    if (commaIndex != -1) {
                        tag.addAttribute(new XMLAttribute(attr.value()[0], pointValue.substring(0, commaIndex)));
                        tag.addAttribute(new XMLAttribute(attr.value()[1], pointValue.substring(commaIndex + 1)));
                    }
                }
            } else if (value instanceof ISVGAttribute svgAttr) {
                if (svgAttr.hasContent()) {
                    tag.addAttribute(attrName, svgAttr.getContent());
                }
            } else if (value instanceof Optional<?> optValue) {
                if (optValue.isPresent()) {
                    Object optVal = optValue.get();
                    if (optVal instanceof ISVGAttribute svgOptAttr) {
                        if (svgOptAttr.hasContent()) {
                            tag.addAttribute(attrName, svgOptAttr.getContent());
                        }
                    } else {
                        tag.addAttribute(attrName, optValue.get().toString());
                    }
                }
            } else if (value instanceof ISVGShape svgShape) {
                tag.appendChild(convert(svgShape));
            } else {
                tag.addAttribute(new XMLAttribute(attrName, value.toString()));
            }
        }

        return tag;
    }

    /**
     * Exporte une seule forme SVG dans un fichier SVG.
     * 
     * @param shape    la forme à exporter
     * @param filename le chemin du fichier destination
     * @param width    la largeur du document SVG
     * @param height   la hauteur du document SVG
     * @return true si l'export réussit, false sinon
     */
    public static boolean export(ISVGShape shape, String filename, double width, double height) {
        return export(List.of(shape), filename, width, height);
    }

    /**
     * Exporte une liste de formes SVG dans un fichier SVG.
     * Crée un élément SVG racine avec les dimensions spécifiées.
     * 
     * @param shapes   la liste des formes à exporter
     * @param filename le chemin du fichier destination
     * @param width    la largeur du document SVG
     * @param height   la hauteur du document SVG
     * @return true si l'export réussit, false sinon
     */
    public static boolean export(List<ISVGShape> shapes, String filename, double width, double height) {
        XMLTag svg = new XMLTag("svg");
        svg.addAttribute(new XMLAttribute("xmlns", "http://www.w3.org/2000/svg"));
        svg.addAttribute(new XMLAttribute("version", "1.1"));
        svg.addAttribute(new XMLAttribute("width", String.valueOf(width)));
        svg.addAttribute(new XMLAttribute("height", String.valueOf(height)));

        for (ISVGShape shape : shapes) {
            svg.appendChild(convert(shape));
        }

        return export(svg, filename);
    }

    /**
     * Exporte une structure XML dans un fichier.
     * 
     * @param root     la balise racine de la structure XML
     * @param filename le chemin du fichier destination
     * @return true si l'export réussit, false sinon
     */
    public static boolean export(XMLTag root, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(root.toString());
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}