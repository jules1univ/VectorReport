# 📚 Documentation du Projet VectorReport

> Guide complet avec explications de chaque module

**Table des matières**

- [Architecture générale](#architecture-générale)
- [Module: geometry](#module-geometry)
- [Module SVG](#module-svg)
- [Module IO](#module-io)
- [Module Visustats](#module-visustats)
- [Module Application](#module-application)
- [Diagramme de dépendances](#diagramme-de-dépendances)
- [Flux de travail typique](#flux-de-travail-typique)

## Architecture générale

Le projet est structuré en couches, avec une séparation claire des responsabilités :

```
┌─────────────────────────────────────────────────┐
│          APPLICATION (vide pour le moment)      │
│               Desktop UI et un CLI              │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│    IO => Input Output (SVG/CSV/XML)             │
│  Sérialisation/désérialisation des formes       │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│  GEOMETRY (formes, opérations géométriques)    │
│  - Point, Circle, Rectangle, Polygon, etc.     │
│  - Transformations (move, rotate, resize)      │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│   SVG (spécification SVG + annotations)         │
│  - Attributes (style, transform, path, color)  │
│  - Animations et visualisations                │
│  - Interfaces et annotations                   │
└─────────────────────────────────────────────────┘
```

**Règle d'or : Les dépendances vont d'haut en bas UNIQUEMENT.**

### Conventions de nommage

- **Packages** : `fr.univrennes.istic.l2gen.<module>.<package>`
- **Classes** : `CamelCase` (ex: `SVGStyle`, `Circle`)
- **Méthodes/Variables** : `camelCase` (ex: `getWidth()`, `fillColor`)
- **Annotations** : `@SVGField`, `@SVGTag`, `@SVGPoint`, etc.

**Voir le [guide de contribution](CONTRIBUTING.md)** pour plus de détails sur les conventions de code, les tests et les messages de commit.

## Module: geometry

### Emplacement

```
src/fr/univrennes/istic/l2gen/geometry/
├── AbstractShape.java    # Classe abstraite pour toutes les formes
├── IShape.java          # Interface principale des formes
├── Point.java           # Point 2D (x, y)
├── Path.java            # Forme basée sur un chemin SVG
├── Group.java           # Conteneur de formes (groupe)
├── base/
│   ├── Circle.java      # Cercle
│   ├── Ellipse.java     # Ellipse
│   ├── Line.java        # Ligne (2 points)
│   ├── Polygon.java     # Polygone (n points)
│   ├── PolyLine.java    # Polyligne
│   ├── Rectangle.java   # Rectangle
│   ├── Text.java        # Texte
│   └── Triangle.java    # Triangle
```

### Responsabilité

Définir le **modèle métier** des formes géométriques avec :

- **Formes** : Point, Circle, Rectangle, Triangle, Polygon, Ellipse, PolyLine, Text, Path
- **Opérations** : move, resize, rotate, copy
- **Propriétés** : getWidth(), getHeight(), getCenter()
- **Conteneurs** : Group (liste de formes)

### Classe clé: `IShape` (Interface)

```java
public interface IShape extends ISVGShape {
    // Dimensions
    double getWidth();      // Largeur de la forme
    double getHeight();     // Hauteur de la forme
    Point getCenter();      // Centre géométrique

    // Transformations
    void move(double dx, double dy);        // Déplacement
    void resize(double px, double py);      // Mise à l'échelle
    void rotate(double deg);                // Rotation

    // Autres
    IShape copy();                  // Copie la forme
    String getDescription(int indent);  // Description textuelle

    // Propriétés SVG (héritées de ISVGShape)
    SVGStyle getStyle();            // Couleur, trait, police
    SVGTransform getTransform();    // Transformations géométriques
}
```

### Classe clé: `AbstractShape` (Classe abstraite)

```java
public abstract class AbstractShape implements IShape {
    @SVGField
    protected final SVGStyle style = new SVGStyle();

    @SVGField
    protected final SVGTransform transform = new SVGTransform();

    // Implémentations communes
    // Chaque forme concrète (Circle, Rectangle, etc.) complète cette classe
}
```

### Exemple: Créer une forme

```java
// Créer un rectangle rouge
Rectangle rect = new Rectangle(0, 0, 100, 50);
rect.getStyle().fillColor(Color.RED);
rect.getStyle().strokeColor(Color.BLACK);
rect.getStyle().strokeWidth(2.0);

// Transformer
rect.move(10, 20);           // Déplacer
rect.rotate(45);             // Tourner
rect.resize(1.5, 1.5);       // Agrandir

// Récupérer propriétés
Point center = rect.getCenter();
double width = rect.getWidth();
String desc = rect.getDescription(0);  // Affichage textuel
```

### Tests

```
test/fr/univrennes/istic/l2gen/geometry/
├── AbstractShapeTest.java   # Classe abstraite pour les tests
├── PointTest.java           # Tests du Point
├── GroupTest.java           # Tests du Group
├── PathTest.java            # Tests du Path
└── base/
    ├── CircleTest.java
    ├── LineTest.java
    ├── RectangleTest.java
    └── ...
```

## Module SVG

### Emplacement

```
src/fr/univrennes/istic/l2gen/svg/
├── interfaces/              # Annotations et interfaces clés
│   ├── ISVGAttribute.java   # Interface pour attributs SVG
│   ├── ISVGShape.java       # Interface base des éléments SVG
│   ├── tag/
│   │   ├── @SVGTag         # Annotation définissant le tag XML
│   │   └── SVGTagProcessor.java # Pré-processeur pour les tags (Java MATE-INF)
│   ├── field/
│   │   ├── @SVGField       # Annotation mappant un champ à un attribut
│   │   └── SVGField.java
│   ├── point/
│   │   ├── @SVGPoint       # Annotation marquant une classe comme point
│   │   ├── @SVGPointX      # Annotation pour coordonnée X
│   │   ├── @SVGPointY      # Annotation pour coordonnée Y
│   │   └── ...
│   └── content/
│       ├── @SVGContent     # Annotation pour contenu textuel
│       └── SVGContentProcessor.java # Pré-processeur pour le contenu (Java MATE-INF)
├── attributes/
│   ├── style/
│   │   └── SVGStyle.java           # Couleur, trait, police
│   ├── transform/
│   │   └── SVGTransform.java       # Rotation, translation, scale
│   ├── path/
│   │   ├── SVGPath.java            # Chemin SVG
│   │   ├── ParseCommands.java
│   │   ├── BoundingBox.java
│   │   └── commands/               # Commands SVG (Arc, Bezier, etc.)
│   └── ...
├── color/
│   └── Color.java                  # Classe pour gérer les couleurs
├── animations/
│   ├── AbstractAnimate.java        # Classe de base des animations
│   ├── SVGAnimate.java             # Animation d'attribut
│   ├── SVGAnimateTransform.java    # Animation de transformation
│   ├── SVGAnimateMotion.java       # Animation de mouvement
│   └── Animation*.java             # Propriétés d'animation
└── ...
```

**Voir le [guide d'annotation](./ANNOTATIONS.md)** pour une explication détaillée du système d'annotations et de son fonctionnement.

**Voir le fonctionnement des [pre-processeurs](https://www.baeldung.com/java-annotation-processing-builder)** pour comprendre comment les annotations sont traitées à la compilation.

### Responsabilité

Fournir **la spécification SVG et les outils d'annotation** :

- **Annotations** : système déclaratif pour mapper Python → XML
- **Attributs** : style, transform, path, color, etc.
- **Animations** : animate, animateTransform, animateMotion
- **Système de sérialisation** : export Java → XML SVG

### Annotations (le cœur du système)

#### `@SVGTag(String value)`

Marque une classe comme élément SVG avec son nom de tag.

```java
@SVGTag("circle")
public class Circle extends AbstractShape {
    // La classe sera exportée en <circle>
}
```

#### `@SVGField(String[] value)`

Mappe un champ Java à un attribut SVG.

```java
@SVGField("cx")          // Simple
private double centerX;

@SVGField({"x1", "y1"})  // Point en 2 attributs
private Point start;
```

#### `@SVGPoint`, `@SVGPointX`, `@SVGPointY`

Marque une classe comme point et ses champs de coordonnées.

```java
@SVGPoint
public class Point {
    @SVGPointX
    private double x;

    @SVGPointY
    private double y;
}
```

#### `@SVGContent`

Marque un champ comme contenu textuel (au lieu d'attribut).

```java
@SVGContent
private String text;  // Export: <text>contenu textuel</text>
```

### Classe: `SVGStyle` (Attributs visuels)

```java
SVGStyle style = new SVGStyle();

// Couleurs
style.fillColor(Color.RED);        // Couleur de remplissage
style.strokeColor(Color.BLACK);    // Couleur du trait

// Traits
style.strokeWidth(2.0);            // Épaisseur
style.strokeDashArray(5, 10, 5);   // Motif en pointillés

// Police
style.fontSize(14.0);              // Taille
style.fontFamily("Arial");         // Famille
style.textAnchor("middle");        // Ancrage du texte

// Export: style="fill:#ff0000;stroke:#000000;stroke-width:2.0;..."
```

### Classe: `SVGTransform` (Transformations)

```java
SVGTransform transform = new SVGTransform();

// Transformations (toutes avec chaînage)
transform.translate(10, 20);       // Déplacer
transform.scale(2.0, 1.5);         // Mettre à l'échelle
transform.rotate(45);              // Tourner (sans pivot)
transform.rotate(45, 100, 100);    // Tourner (avec pivot)
transform.skew(10, 0);             // Incliner

// Export: transform="translate(10,20) scale(2.0,1.5) rotate(45)..."
String content = transform.getContent();
```

### Classe: `Color` (Gestion des couleurs)

```java
// Constantes
Color.RED;      // #ff0000
Color.BLACK;    // #000000

// Création
Color.hex("#ff0000");           // Depuis hex
Color.rgb(255, 0, 0);           // Depuis RGB
Color.rgba(255, 0, 0, 128);     // Depuis RGBA
Color.random();                 // Aléatoire

// Parsing
Color.raw("rgb(255,0,0)");      // Parse SVG CSS
Color.raw("rgba(255,0,0,128)");
Color.raw("#ff0000");
```

### Classe: `SVGPath` (Chemins SVG)

```java
SVGPath path = new SVGPath();

// Ajouter des commandes
path.moveTo(0, 0);           // M 0 0
path.lineTo(100, 100);       // L 100 100
path.curveTo(150, 50, 200, 100);  // C (Bézier cubique)
path.arc(...);               // A (Arc circulaire)
path.close();                // Z

// Export: d="M 0 0 L 100 100 C 150 50 200 100 Z"
```

### Animations

```java
SVGAnimate animate = new SVGAnimate();
animate.attributeName("cx");          // Quelle propriété animer
animate.from("50");                   // Valeur initiale
animate.to("200");                    // Valeur finale
animate.duration(2000);               // Durée en ms

// Export: <animate attributeName="cx" from="50" to="200" dur="2s"/>
```

### 📝 Exemple: Emploi du système d'annotations

```java
@SVGTag("myCustomShape")
public class MyShape implements ISVGShape {

    @SVGField("cx")
    private double centerX;

    @SVGField("cy")
    private double centerY;

    @SVGField("r")
    private double radius;

    @SVGField
    private SVGStyle style;  // Export automatique de l'attribut "style"

    @SVGField
    private SVGTransform transform;  // Export automatique

    @SVGContent
    private String label;           // Contenu textuel

    // Getters...
}

// À l'export, devient:
// <myCustomShape cx="..." cy="..." r="..." style="..." transform="...">label</myCustomShape>
```

## Module IO

### Emplacement

```
src/fr/univrennes/istic/l2gen/io/
├── svg/
│   ├── SVGExport.java       # Exporte Java objects → fichier SVG
│   └── SVGImport.java       # Parse SVG → Java objects
├── xml/
│   ├── model/
│   │   ├── XMLAttribute.java
│   │   └── XMLTag.java      # Modèle XML générique
│   └── parser/
│       ├── XMLParser.java   # Parsing XML
│       └── XMLParseException.java
└── csv/
    ├── model/
    │   ├── CSVRow.java
    │   └── CSVTable.java    # Modèle CSV générique
    └── parser/
        ├── CSVParser.java   # Parsing CSV
        └── CSVParseException.java
```

### Responsabilité

**Sérialisation et désérialisation** :

- **SVG** : export formes Java → XML SVG, import XML SVG → Java
- **XML** : parser et modèle générique pour manipuler du XML
- **CSV** : parser et modèle générique pour manipuler du CSV

### Classe: `SVGExport`

Convertit des objets Java (`ISVGShape`) en fichiers SVG.

**Fonctionnement** :

1. Scanne les champs annotés `@SVGField` et `@SVGContent`
2. Utilise la réflexion pour extraire les valeurs
3. Crée une structure XML équivalente
4. Écrit le SVG dans un fichier

```java
// Export une liste de formes
List<IShape> shapes = ...;
SVGExport.export(shapes, "output.svg", 1000, 1000);

// Export une forme unique
Circle circle = new Circle(0, 0, 50);
SVGExport.export(circle, "output.svg", 500, 500);

// Résultat SVG généré automatiquement :
// <svg xmlns="..." width="1000" height="1000">
//   <circle cx="0" cy="0" r="50" jclass-data="..."/>
// </svg>
```

**Détail du processus** :

```java
// 1. Récupère le tag SVG de la classe
@SVGTag("circle")
public class Circle { ... }

// 2. Mappe les champs aux attributs
@SVGField("cx")
private double centerX;

// 3. Exporte les ISVGAttribute (style, transform)
@SVGField
private SVGStyle style;

// 4. Crée la balise XML équivalente
<circle cx="100" cy="50" style="stroke:...;fill:...;" transform="..."/>
```

### Classe: `SVGImport`

Parse un fichier SVG et reconstruit les objets Java.

**Fonctionnement** :

1. Les formes doivent être **enregistrées au préalable** avec `SVGImport.register(Class)`
2. Parse le XML
3. Pour chaque balise XML, trouve la classe Java correspondante (via `@SVGTag`)
4. Instancie l'objet avec le constructeur par défaut
5. Inject les attributs via réflexion

```java
// Enregistrement (obligatoire global en startup)
SVGImport.register(Point.class);
SVGImport.register(Circle.class);
SVGImport.register(Line.class);
// ...

// Chargement
List<ISVGShape> shapes = SVGImport.load("output.svg");
```

**Points importants** :

- ✅ Chaque classe doit avoir un **constructeur sans paramères**
- ✅ Chaque classe doit avoir l'annotation **`@SVGTag("...")`**
- ❌ Ne réimporte pas les formes non-enregistrées

### Classe: `XMLTag` (Modèle XML)

Classe générique pour manipuler du XML en mémoire.

```java
XMLTag svg = new XMLTag("svg");
svg.addAttribute("xmlns", "http://www.w3.org/2000/svg");
svg.addAttribute("width", "1000");
svg.addAttribute("height", "1000");

XMLTag circle = new XMLTag("circle");
circle.addAttribute("cx", "100");
circle.addAttribute("cy", "50");
circle.addAttribute("r", "25");

svg.appendChild(circle);

// Export
System.out.println(svg.toString());
// <svg xmlns="..." width="1000" height="1000">
//   <circle cx="100" cy="50" r="25"/>
// </svg>
```

### Classe: `CSVParser` (Parsing CSV)

Parse des fichiers ou chaînes CSV.

```java
CSVParser parser = new CSVParser();
parser.withDelimiter(',');           // Séparateur (défaut: ,)
parser.withQuoteChar('"');           // Guillemet (défaut: ")
parser.withHeaders(true);            // Première ligne = headers
parser.withTrimWhitespace(true);     // Enlever espaces

// Depuis fichier
CSVTable table = parser.parse(new File("data.csv"));

// Depuis string
String csv = "name,age\nAlice,30\nBob,25";
CSVTable table = parser.parse(csv);

// Accès aux données
List<CSVRow> rows = table.getRows();
List<String> headers = table.getHeaders();
```

## Module Visustats

### Emplacement

```
src/fr/univrennes/istic/l2gen/visustats/
├── data/
│   ├── DataSet.java         # Enregistrement d'une série de données
│   ├── DataGroup.java       # Groupe de séries
│   ├── Label.java           # Label associé à une valeur
│   └── Value.java           # Valeur + couleur pour une donnée
└── view/
    ├── datagroup/
    │   ├── IDataGroupView.java
    │   └── AbstractDataGroupView.java  # Vue de l'ensemble des séries
    └── dataset/
        ├── IDataSetView.java
        ├── AbstractDataSetView.java
        ├── BarDataSetView.java      # Vue: diagramme en barres
        ├── ColumnsDataSetView.java  # Vue: diagramme en colonnes
        └── PieDataSetView.java      # Vue: diagramme circulaire
```

### Responsabilité

**Visualisation de données** :

- **Modèle** : `DataSet`, `DataGroup` pour représenter les données
- **Vues** : `BarDataSetView`, `ColumnsDataSetView`, `PieDataSetView` pour les convertir en formes géométriques
- Export automatique en SVG via le système d'annotations

### Classe: `DataSet`

Record contenant une série de données.

```java
DataSet dataset = new DataSet();
dataset.values().add(new Value(10.0, Color.RED));
dataset.values().add(new Value(20.0, Color.GREEN));
dataset.values().add(new Value(15.0, Color.BLUE));

// Statistiques
double sum = dataset.sum();      // 45.0
double max = dataset.max();      // 20.0
double min = dataset.min();      // 10.0
Color color = dataset.getColor(0);  // RED
```

### Classe: `BarDataSetView`

Convertit un `DataSet` en diagramme en barres.

```java
DataSet data = new DataSet(...);
BarDataSetView barChart = new BarDataSetView(new Point(200, 200));
barChart.setData(data);

// Cette vue crée automatiquement des formes `IShape` (rectangles)
// qui peuvent être exportées en SVG
```

### Workflow typique

```
CSV (données brutes)
  ↓
CSVParser
  ↓
DataSet/DataGroup (modèle métier)
  ↓
BarDataSetView/PieDataSetView (conversion géométrique)
  ↓
List<IShape> (formes géométriques)
  ↓
SVGExport
  ↓
SVG (fichier final)
```

## Module Application

### Emplacement

```
src/fr/univrennes/istic/l2gen/application/
└── App.java     # Point d'entrée du programme
# Plus tard : UI desktop & CLI
```

### Responsabilité

**Démos (pour le moment)** :

### Classe: `App`

```java
public class App {
    static {
        // Enregistrement global (obligatoire au startup)
        SVGImport.register(Point.class);
        SVGImport.register(Circle.class);
        SVGImport.register(Rectangle.class);
        // ... toutes les formes
    }

    public static void main(String[] args) throws Exception {
        // Créer une fractale
        IShape fractal = new Fractal().draw(
            new Triangle(...),
            5  // profondeur
        );

        // Créer un arrière-plan blanc
        IShape background = new Rectangle(0, 0, 1000, 1000);
        background.getStyle().fillColor(Color.WHITE);

        // Exporter en SVG
        SVGExport.export(
            List.of(background, fractal),
            "output/fractal.svg",
            1000,  // width
            1000   // height
        );

        // Réimporter pour validation
        List<ISVGShape> imported = SVGImport.load("output/fractal.svg");
    }
}
```

## Diagramme de dépendances

```
SVG (annotations, attributs, animations)
  ↑
  │ depends on
  │
GEOMETRY (formes, opérations)
  ↑
  │ depends on
  │
IO (import/export, parsing)
  ↑
  │ depends on
  │
VISUSTATS (visualisation de données)
  ↑
  │ depends on
  │
APPLICATION (point d'entrée, démos)
```

## Flux de travail typique

### 1️⃣ Créer et transformer des formes

```java
// Géométrie
Circle circle = new Circle(100, 100, 50);
circle.getStyle().fillColor(Color.BLUE);
circle.getStyle().strokeColor(Color.BLACK);
circle.getStyle().strokeWidth(2.0);

// Transformation
circle.move(50, 50);
circle.rotate(45);
```

### 2️⃣ Grouper et organiser

```java
Group group = new Group();
group.add(circle);
group.add(rectangle);
group.add(line);

group.move(100, 100);  // Déplacer toutes les formes
```

### 3️⃣ Styliser

```java
SVGStyle style = shape.getStyle();
style.fillColor(Color.RED);          // Remplissage
style.strokeColor(Color.BLACK);      // Contour
style.strokeWidth(2.0);              // Épaisseur
style.strokeDashArray(5, 10);        // Pointillés
style.fontSize(14.0);                // Police
```

### 4️⃣ Animer (optionnel)

```java
// (Nécessite d'ajouter la forme à l'export avec children)
SVGAnimate animate = new SVGAnimate();
animate.attributeName("cx");
animate.from("0");
animate.to("100");
animate.duration(2000);
```

### 5️⃣ Exporter en SVG

```java
List<IShape> shapes = List.of(background, circle, group);
SVGExport.export(shapes, "output.svg", 1000, 1000);
```

### 6️⃣ Réimporter (optionnel, pour validation)

```java
List<ISVGShape> imported = SVGImport.load("output.svg");
```

## Points importants

### ✅ Do's

- **Utiliser les annotations** : `@SVGTag`, `@SVGField` pour automatiser l'export
- **Chaîner les méthodes** : `style.fillColor(...).strokeWidth(...).fontSize(...)`
- **Enregistrer les formes** : `SVGImport.register(MyShape.class)` en startup
- **Documenter le code** : ajouter des Javadoc et commentaires
- **Tester** : ajouter des tests unitaires avec JUnit

### ❌ Don'ts

- **Ne pas modifier l'importateur directement** : utiliser le système de réflexion
- **Ne pas exporter de formes non-annotées** : ajouter `@SVGTag` et `@SVGField`
- **Ne pas oublier les constructeurs vides** : obligatoire pour l'import
- **Ne pas utiliser de chemins absolus** : utiliser des chemins relatifs
- **Ne pas committer du code non-formaté** : utiliser le formatage automatique

## Exemple complet

```java
// 1. Créer des formes
Rectangle background = new Rectangle(0, 0, 500, 500);
background.getStyle().fillColor(Color.WHITE);

Circle circle = new Circle(250, 250, 100);
circle.getStyle().fillColor(Color.RED);
circle.getStyle().strokeColor(Color.BLACK);
circle.getStyle().strokeWidth(2.0);

Text label = new Text("Hello World");
label.getStyle().fontSize(20.0);
label.getStyle().fillColor(Color.BLACK);

// 2. Grouper
Group group = new Group();
group.add(circle);
group.add(label);

// 3. Exporter
SVGExport.export(
    List.of(background, group),
    "output/example.svg",
    500, 500
);

// 4. Réimporter
List<ISVGShape> shapes = SVGImport.load("output/example.svg");
```

## Ressources supplémentaires

- 📖 [RFC SVG 1.1](https://www.w3.org/TR/SVG11/)
- 📖 [MDN SVG Reference](https://developer.mozilla.org/en-US/docs/Web/SVG)
- 📖 [SVG Paths](https://www.w3schools.com/graphics/svg_path.asp)
