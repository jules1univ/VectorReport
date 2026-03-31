# 📚 Guide des Annotations Java

> Un guide complet pour comprendre comment les annotations fonctionnent et comment elles sont utilisées dans le projet VectorReport

**Table of Contents**

- [Qu'est-ce qu'une annotation ?](#quest-ce-quune-annotation)
- [Analogie du monde réel](#analogie-du-monde-réel)
- [Syntaxe des annotations](#syntaxe-des-annotations)
- [Créer sa propre annotation](#créer-sa-propre-annotation)
- [La réflexion en Java](#la-réflexion-en-java)
- [Annotations du projet VectorReport](#annotations-du-projet-geometry)
- [Cas d'usage réel: Export SVG](#cas-dusage-réel-export-svg)
- [Cas d'usage réel: Import SVG](#cas-dusage-réel-import-svg)
- [Exercices pratiques](#exercices-pratiques)

## Qu'est-ce qu'une annotation ?

### Définition

Une **annotation** est une forme de **métadonnée** qu'on ajoute au code Java. Elle ne change pas directement le comportement du programme, mais elle fournit des informations supplémentaires que Java (ou d'autres outils) peuvent lire et exploiter.

**Métadonnée** = donnée qui décrit une autre donnée

### Analogie simple

Imaginez un livre :

- Le **contenu du livre** = votre code Java
- Les **post-it collés sur les pages** = les annotations

Les post-it ne changent pas l'histoire du livre, mais ils ajoutent des informations supplémentaires ("📌 Important", "❓ Question", "✏️ À réviser").

Les post-it peuvent être lus par quelqu'un d'autre (ou par du code) pour savoir quoi faire avec le contenu.

### Exemple classique: @Override

```java
class Animal {
    public void crier() {
        System.out.println("Bruit neutre");
    }
}

class Chien extends Animal {
    @Override  // ← L'annotation indique que cette méthode redéfinit la parent
    public void crier() {
        System.out.println("Wouf Wouf");
    }
}
```

**Que fait @Override ?**

- ✅ Elle informe Java que vous redéfinissez intentionnellement une méthode parente
- ✅ Le compilateur vérifie que la méthode existe bien dans la classe parente
- ✅ Si la méthode n'existe pas, une **erreur de compilation** est générée

```
❌ Si vous écrivez: @Override public void aboyer() { }
   Et que Animal n'a pas de méthode "aboyer()", le compilateur crie une erreur!
```

## Analogie du monde réel

### Postier qui livre du courrier

Imaginons que vous êtes un **postier** qui doit livrer des lettres:

```java
// Sans annotations (le postier ne sait pas quoi faire avec la lettre)
class Lettre {
    public String contenu;
    public String date;
    public String lieu;
}

// Avec annotations (des post-it sur la lettre)
class Lettre {
    @ExpediePar("Poste")  // ← Post-it: "Expédié par la Poste"
    public String contenu;

    @DateImportante       // ← Post-it: "Date importante"
    public String date;

    @Urgence(niveau = 3)  // ← Post-it: "Urgent! Niveau 3"
    public String lieu;
}
```

Le postier lit les post-it (annotations) pour savoir:

- Qui a envoyé la lettre
- Si c'est important
- Quel est le niveau d'urgence

De la même manière, du **code automatisé** peut lire vos annotations pour faire du travail à votre place!

## Syntaxe des annotations

### Utiliser une annotation existante

```java
class Exemple {
    @Override                      // Annotation sans paramètre
    public void maMethode() { }

    @Deprecated                    // Annotation sans paramètre
    public void ancienneMethode() { }

    @SuppressWarnings("unchecked") // Annotation avec paramètre
    public void maMethode2() { }
}
```

### Syntaxe générale

```java
@NomAnnotation                  // Sans paramètre

@NomAnnotation(param = "valeur") // Avec paramètres nommés

@NomAnnotation("valeur")        // Avec paramètre "value" (raccourci)

@NomAnnotation({"val1", "val2"}) // Avec tableau de valeurs
```

## Créer sa propre annotation

### Étape 1: Déclarer l'annotation

```java
@Retention(RetentionPolicy.RUNTIME)  // ← Garder l'annotation au runtime
@Target(ElementType.FIELD)           // ← S'applique uniquement aux champs
public @interface SVGField {
    String[] value() default {};     // ← Paramètre : tableau de strings vide par défaut
}
```

### Explication détaillée

#### `@Retention(RetentionPolicy.RUNTIME)`

Quand garder l'annotation ?

```
SOURCE        // ← Jetée après la compilation (méta-info uniquement)
CLASS         // ← Gardée dans le .class, perdue au runtime
RUNTIME       // ← Gardée au runtime (on peut la lire à l'exécution!)
```

**Pour le projet VectorReport**, on utilise `RUNTIME` car on veut **lire les annotations pendant l'exécution** du programme pour faire l'export/import SVG.

#### `@Target(ElementType.XXX)`

Sur quoi appliquer l'annotation ?

```java
ElementType.TYPE              // Classes, interfaces, enums
ElementType.FIELD             // Champs/attributs
ElementType.METHOD            // Méthodes
ElementType.PARAMETER         // Paramètres de méthodes
ElementType.LOCAL_VARIABLE    // Variables locales
ElementType.CONSTRUCTOR       // Constructeurs
ElementType.ANNOTATION_TYPE   // Autres annotations
```

**Pour le projet VectorReport** :

- `@SVGTag` s'applique aux **classes** (`ElementType.TYPE`)
- `@SVGField` s'applique aux **champs** (`ElementType.FIELD`)
- `@SVGPoint` s'applique aux **classes** (`ElementType.TYPE`)

#### `String[] value() default {}`

Le paramètre de l'annotation :

```java
// L'annotation déclare un paramètre "value"
public @interface SVGField {
    String[] value() default {};  // Tableau de strings, vide par défaut
}

// Façons d'utiliser:
@SVGField                                // Vide (tableau vide)
@SVGField("cx")                          // Raccourci pour ["cx"]
@SVGField({"cx", "cy"})                  // Deux valeurs
@SVGField(value = {"cx", "cy"})          // Nommé explicitement
```

### Étape 2: Utiliser l'annotation

```java
class Circle {
    @SVGField("r")              // Un paramètre
    private double radius;

    @SVGField({"cx", "cy"})     // Deux paramètres
    private Point center;

    @SVGField                   // Pas de paramètre (défaut)
    private SVGStyle style;
}
```

## La réflexion en Java

### Qu'est-ce que la réflexion ?

La **réflexion** est la capacité de **lire des informations sur votre code à l'exécution**.

C'est comme regarder dans un **miroir magique** qui vous dit tout sur votre classe:

- Quels sont les champs ?
- Quelles sont les méthodes ?
- Quelles sont les annotations ?
- Quels sont les constructeurs ?

### Exemple: Accéder à la classe d'un objet

```java
// 1. Créer une instance
Circle monCercle = new Circle(100, 100, 50);

// 2. Obtenir sa classe (le "miroir magique")
Class<?> classe = monCercle.getClass();  // Ou: Circle.class

// 3. Afficher le nom de la classe
System.out.println(classe.getName());    // Affiche: Circle

// 4. Récupérer les champs
Field[] champs = classe.getDeclaredFields();  // Tous les champs de la classe

// 5. Parcourir les champs
for (Field champ : champs) {
    System.out.println("Champ: " + champ.getName());
}
```

**Résultat** :

```
Champ: radius
Champ: center
Champ: style
```

### Lire les annotations avec la réflexion

```java
// On veut lire l'annotation @SVGField du champ "radius"

Circle monCercle = new Circle(100, 100, 50);
Class<?> classe = monCercle.getClass();

// Récupérer le champ "radius"
Field champRadius = classe.getDeclaredField("radius");  // Attention: throws Exception

// Obtenir l'annotation @SVGField sur ce champ
SVGField annotation = champRadius.getAnnotation(SVGField.class);

// Vérifier si l'annotation existe
if (annotation != null) {
    // Récupérer la valeur de l'annotation
    String[] values = annotation.value();

    System.out.println("L'annotation @SVGField a les valeurs: ");
    for (String val : values) {
        System.out.println("  - " + val);
    }
}
```

**Sortie** :

```
L'annotation @SVGField a les valeurs:
  - r
```

### Cas d'usage: Parcourir tous les champs annotés

```java
Class<?> classe = Circle.class;

// Récupérer tous les champs
Field[] champs = classe.getDeclaredFields();

// Parcourir chaque champ
for (Field champ : champs) {
    // Vérifier s'il a l'annotation @SVGField
    if (champ.isAnnotationPresent(SVGField.class)) {
        SVGField annotation = champ.getAnnotation(SVGField.class);
        System.out.println("Champ " + champ.getName() +
                         " a @SVGField avec valeurs: " +
                         Arrays.toString(annotation.value()));
    }
}
```

**Sortie** :

```
Champ radius a @SVGField avec valeurs: [r]
Champ center a @SVGField avec valeurs: [cx, cy]
Champ style a @SVGField avec valeurs: []
```

## Annotations du projet VectorReport

### 1️⃣ @SVGTag - Marquer une classe comme élément SVG

```java
/**
 * Déclare que cette classe représente un élément SVG.
 * Le paramètre est le nom du tag SVG.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SVGTag {
    String value();  // Obligatoire : le nom du tag SVG
}
```

**Utilisation** :

```java
@SVGTag("circle")
public class Circle { }  // Cette classe → élément <circle>

@SVGTag("rect")
public class Rectangle { }  // Cette classe → élément <rect>

@SVGTag("g")
public class Group { }  // Cette classe → élément <g>
```

### 2️⃣ @SVGField - Mapper un champ à un attribut SVG

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SVGField {
    String[] value() default {};  // Noms des attributs SVG
}
```

**Utilisation** :

```java
class Circle {
    @SVGField("r")            // Champ → attribut "r" du SVG
    private double radius;

    @SVGField({"cx", "cy"})   // Champ → deux attributs "cx" et "cy"
    private Point center;

    @SVGField                 // Pas de paramètre (utilise le nom du champ)
    private SVGStyle style;
}
```

**Export généré** :

```xml
<circle r="50" cx="100" cy="100" style="..."/>
           ↑         ↑  ↑      ↑
        vient de @SVGField
```

### 3️⃣ @SVGPoint - Marquer une classe comme point

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SVGPoint {
    // Pas de paramètre : juste un marqueur
}
```

**Utilisation** :

```java
@SVGPoint
public class Point {
    @SVGPointX  // ← Marque la coordonnée X
    private double x;

    @SVGPointY  // ← Marque la coordonnée Y
    private double y;
}
```

### 4️⃣ @SVGPointX et @SVGPointY - Coordonnées d'un point

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SVGPointX { }  // Marque le champ X

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SVGPointY { }  // Marque le champ Y
```

### 5️⃣ @SVGContent - Contenu textuel d'un élément

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SVGContent {
    // Pas de paramètre : juste un marqueur
}
```

**Utilisation** :

```java
@SVGTag("text")
public class Text {
    @SVGContent
    private String content;  // Le texte entre <text> et </text>

    @SVGField("x")
    private double x;
}
```

**Export généré** :

```xml
<text x="100">Contenu du texte ici</text>
      ↑         ↑
   @SVGField  @SVGContent
```

## Cas d'usage réel: Export SVG

### Comment SVGExport fonctionne

**SVGExport.java** utilise la **réflexion** pour lire les annotations et créer automatiquement du XML !

```java
public static XMLTag convert(ISVGShape shape) {
    Class<?> shapeClass = shape.getClass();

    // 1. Lire l'annotation @SVGTag
    SVGTag tagName = shapeClass.getAnnotation(SVGTag.class);
    String nom = tagName.value();  // Ex: "circle"

    // 2. Créer la balise XML avec ce nom
    XMLTag tag = new XMLTag(nom);

    // 3. Récupérer tous les champs
    Field[] champs = shapeClass.getDeclaredFields();

    // 4. Pour chaque champ...
    for (Field champ : champs) {
        // ... lire l'annotation @SVGField
        SVGField attr = champ.getAnnotation(SVGField.class);

        if (attr != null) {
            // Récupérer la valeur du champ
            champ.setAccessible(true);
            Object valeur = champ.get(shape);

            // Ajouter un attribut XML avec la valeur
            tag.addAttribute(new XMLAttribute(attr.value()[0], valeur.toString()));
        }
    }

    return tag;
}
```

### Exemple concret: Export d'un cercle

**Code Java** :

```java
Circle circle = new Circle(100, 100, 50);
circle.getStyle().fillColor(Color.RED);

SVGExport.export(circle, "output.svg", 500, 500);
```

**Processus interne** :

```
1. Lire la classe Circle
2. Trouver @SVGTag("circle") → nom du tag: "circle"
3. Créer <circle ... />
4. Parcourir les champs:
   - radius avec @SVGField("r") → ajouter r="50"
   - center avec @SVGField({"cx", "cy"}) → ajouter cx="100" cy="100"
   - style avec @SVGField → ajouter style="fill:#ff0000;..."
5. Créer le XML final
```

**Fichier SVG généré** :

```xml
<svg xmlns="..." width="500" height="500">
    <circle
        r="50"
        cx="100"
        cy="100"
        style="fill:#ff0000;"
        jclass-data="fr.univrennes.istic.l2gen.geometry.base.Circle"
    />
</svg>
```

## Cas d'usage réel: Import SVG

### Comment SVGImport fonctionne

**SVGImport.java** fait l'inverse: il lit du XML et crée des objets Java !

```java
public static ISVGShape convert(XMLTag tag) {
    // 1. Trouver la classe Java correspondant au tag XML
    for (Class<? extends ISVGShape> shapeClass : registeredShapes) {
        SVGTag tagAnnotation = shapeClass.getAnnotation(SVGTag.class);

        // Est-ce que le nom du tag correspond ?
        if (!tagAnnotation.value().equals(tag.getTagName())) {
            continue;
        }

        // 2. Créer une instance avec le constructeur vide
        ISVGShape shape = shapeClass.getConstructor().newInstance();

        // 3. Récupérer tous les champs
        Field[] champs = shapeClass.getDeclaredFields();

        // 4. Pour chaque attribut du XML...
        for (Field champ : champs) {
            SVGField attr = champ.getAnnotation(SVGField.class);

            if (attr != null) {
                // Récupérer la valeur de l'attribut XML
                String attrName = attr.value()[0];
                String attrValue = tag.getAttribute(attrName).getValue();

                // Injecter la valeur dans le champ Java
                champ.setAccessible(true);
                champ.set(shape, parseValue(attrValue, champ.getType()));
            }
        }

        return shape;
    }

    return null;
}
```

### Exemple concret: Import d'un SVG

**Fichier SVG** :

```xml
<circle r="50" cx="100" cy="100" style="fill:#ff0000;"/>
```

**Processus interne** :

```
1. Parser le XML → tag "circle" avec attributs
2. Trouver la classe Java avec @SVGTag("circle") → Circle.class
3. Créer une instance: Circle()
4. Lire les attributs:
   - r="50" → injecter dans le champ radius
   - cx="100" cy="100" → injecter dans le champ center
   - style="..." → injecter dans le champ style
5. Retourner l'objet Circle créé
```

**Résultat** :

```java
// L'objet Java créé depuis le SVG
Circle circle = ...;  // Un vrai objet Circle avec les bonnes valeurs!
System.out.println(circle.getRadius());  // Affiche: 50
System.out.println(circle.getCenter());  // Affiche: Point(100, 100)
```

## Résumé: Pourquoi les annotations sont utiles

### 1️⃣ Automatisation

Au lieu d'écrire manuellement:

```java
// ❌ Sans annotations: beaucoup de code répétitif
public static void exportCircle(Circle c, XMLTag tag) {
    tag.addAttribute("r", String.valueOf(c.getRadius()));
    tag.addAttribute("cx", String.valueOf(c.getCenter().getX()));
    tag.addAttribute("cy", String.valueOf(c.getCenter().getY()));
    tag.addAttribute("style", c.getStyle().getContent());
    // ... pour chaque forme!
}
```

Avec annotations:

```java
// ✅ Avec annotations: code générique pour TOUTES les formes!
public static XMLTag convert(ISVGShape shape) {
    // Fonction UNIVERSELLE qui fonctionne pour Circle, Rectangle, Triangle, etc.
}
```

### 2️⃣ Maintenabilité

Ajouter un nouveau champ à Circle:

```java
@SVGTag("circle")
public class Circle {
    @SVGField("r")
    private double radius;

    @SVGField({"cx", "cy"})
    private Point center;

    @SVGField  // ← Nouveau champ
    private SVGStyle style;

    @SVGField  // ← NOUVEAU: Nouveau champ ajouté
    private Optional<String> id;
}
```

**Automatiquement**, l'export et l'import le géreront sans changer une ligne de code!

### 3️⃣ Déclaratif

Les annotations décrivent le **"quoi"** (mapping SVG) au lieu du **"comment"** (réflexion).

C'est plus lisible et plus expressif.

## Exercices pratiques

### Exercice 1: Créer votre propre annotation

```java
// 1. Créez une annotation @Database avec un paramètre "name"
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CLASS)
public @interface Database {
    String name();  // Paramètre obligatoire
}

// 2. Utilisez-la
@Database(name = "users_db")
public class User {
}

// 3. Lisez-la avec la réflexion
Class<?> classe = User.class;
Database annotation = classe.getAnnotation(Database.class);
System.out.println("Base de données: " + annotation.name());
```

**Résultat attendu** :

```
Base de données: users_db
```

### Exercice 2: Parcourir tous les champs annotés

```java
@SVGTag("rect")
public class Rectangle {
    @SVGField("x")
    private double x;

    @SVGField("y")
    private double y;

    @SVGField("width")
    private double width;

    private String name;  // SANS annotation
}

// Afficher tous les champs avec @SVGField
Class<?> classe = Rectangle.class;
for (Field champ : classe.getDeclaredFields()) {
    if (champ.isAnnotationPresent(SVGField.class)) {
        SVGField ann = champ.getAnnotation(SVGField.class);
        System.out.println(champ.getName() + " → " + ann.value()[0]);
    }
}
```

**Résultat attendu** :

```
x → x
y → y
width → width
```

### Exercice 3: Créer un validateur d'annotations

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
    // Marque un champ comme obligatoire
}

@SVGTag("person")
public class Person {
    @Required
    @SVGField("name")
    private String name;

    @SVGField("age")
    private int age;  // Pas obligatoire
}

// Vérifier que tous les champs @Required ont une valeur
public static void validate(Object obj) {
    Class<?> classe = obj.getClass();
    for (Field champ : classe.getDeclaredFields()) {
        if (champ.isAnnotationPresent(Required.class)) {
            champ.setAccessible(true);
            try {
                Object valeur = champ.get(obj);
                if (valeur == null || (valeur instanceof String && ((String)valeur).isEmpty())) {
                    throw new IllegalArgumentException("Champ obligatoire manquant: " + champ.getName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    System.out.println("Tous les champs obligatoires sont présents! ✅");
}

// Utilisation
Person p = new Person();
p.name = "Alice";
p.age = 30;
validate(p);  // Affiche: "Tous les champs obligatoires sont présents! ✅"

p.name = "";
validate(p);  // Lance une IllegalArgumentException!
```

## Points clés à retenir

✅ **Les annotations fournissent des métadonnées** sur votre code

✅ **La réflexion vous permet de lire les annotations** à l'exécution

✅ **Les annotations avec réflexion = automatisation puissante**

✅ **Dans VectorReport**, elles mappent automatiquement Java → XML SVG

✅ **Exemple**: `@SVGField("r")` sur `radius` → export `r="50"` en XML

✅ **Elles rendent le code plus lisible et maintenable**

✅ **Vous pouvez créer vos propres annotations**

## Ressources supplémentaires

- 📖 [Java Annotations - Oracle Docs](https://docs.oracle.com/javase/tutorial/java/annotations/)
- 📖 [Java Reflection API](https://docs.oracle.com/javase/tutorial/reflect/)
- 📖 [Annotation Processing](https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/annotation/processing/Processor.html)
- 💡 [DOCUMENTATION.md](./DOCUMENTATION.md) - Architecture complète du projet
