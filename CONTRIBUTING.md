# 📖 Guide de Contribution

> Guide complet pour contribuer au projet VectorReport en respectant les standards de qualité du codebase

**Table of Contents**

- [Règles générales](#règles-générales)
- [Configuration de l'environnement](#configuration-de-lenvironnement)
- [Conventions de code Java](#conventions-de-code-java)
- [Système d'annotations SVG](#système-dannotations-svg)
- [Formatage et style](#formatage-et-style)
- [Documentation et Javadoc](#documentation-et-javadoc)
- [Tests unitaires](#tests-unitaires)
- [Messages de commit](#messages-de-commit)
- [Workflow de contribution](#workflow-de-contribution)
- [Checklist avant de committer](#checklist-avant-de-committer)
- [Ressources utiles](#ressources-utiles)

## Règles générales

### Responsabilité des tâches

- **Ne travaillez pas simultanément** sur les mêmes fichiers que d'autres contributeurs
- **Répartissez-vous les tâches** par domaines/modules (geometry, svg, io, etc.)
- **Consultez la liste des TODOs** dans [docs/TODOS.md](docs/TODOS.md) avant de commencer
- **Une tâche = une personne** jusqu'à sa finalisation

### Gestion du code

- **Ne créez pas de branches** autres que `main` (pour simplifier)
- **Pullez régulièrement** les changements : `git pull`
- **Commitez fréquemment** avec des messages clairs (voir [Messages de commit](#messages-de-commit))
- **N'oubliez pas** : `git push` à la fin de votre session de travail

## Configuration de l'environnement

### Prérequis

- **Java JDK 21** : `java --version` doit afficher 21.x
- **VS Code** : avec l'extension "Extension Pack for Java" (Microsoft)
- **Git** : version récente

### Installation initiale

```bash
# 1. Cloner le dépôt
git clone https://github.com/jules1univ/VectorReport.git
cd VectorReport

# 2. Vérifier le JDK
java --version

# 3. Ouvrir dans VS Code
code .

# 4. L'extension Java détecte automatiquement le projet
# Attendez que la première synchronisation se termine
```

### Extensions recommandées

Installer les extensions VS Code listées dans [.vscode/extensions.json](.vscode/extensions.json) :

```bash
# Automatiquement installées si vous cliquez sur "Install Recommended Extensions"
```

## Conventions de code Java

### Structure générale

#### 1️⃣ Package et imports

```java
package fr.univrennes.istic.l2gen.geometry.base;

// Imports triés et organisés
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.univrennes.istic.l2gen.geometry.AbstractShape;
import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
```

**Règles** :

- ✅ Imports de `java.*` d'abord, puis `fr.univrennes.*`
- ✅ Organisés alphabétiquement par classe
- ❌ N'oubliez pas les imports (VS Code les organise automatiquement)

#### 2️⃣ Déclaration de classe

```java
/**
 * Représente un cercle implémentant l'interface IShape.
 * Un cercle est défini par un centre et un rayon.
 *
 * @see AbstractShape
 * @see IShape
 */
@SVGTag("circle")
public final class Circle extends AbstractShape {
    // Corps de la classe
}
```

**Règles** :

- ✅ Classes `final` si non destinées à être surchargées
- ✅ Javadoc obligatoire (voir [Documentation et Javadoc](#documentation-et-javadoc))
- ✅ Annotations `@SVGTag` pour les formes géométriques
- ✅ Une classe par fichier

#### 3️⃣ Indentation et formatage

```java
public final class Circle extends AbstractShape {
    // 4 espaces par niveau d'indentation
    @SVGField("r")
    private double radius;

    @SVGField({"cx", "cy"})
    private Point center;

    // Constructeurs
    public Circle() {
        this.radius = 0;
        this.center = new Point(0, 0);
    }

    // Méthodes
    public double getRadius() {
        return this.radius;
    }
}
```

**Règles** :

- ✅ Indentation : **4 espaces** (jamais de tabs)
- ✅ Accolades sur la même ligne : `public void method() {`
- ✅ Espace après `if`, `for`, `while` : `if (condition) {`
- ✅ Pas d'espace avant parenthèses de méthodes : `method()` et non ` method()`

### Nommage

#### Classes

```java
// ✅ Bon : PascalCase, nom significatif
public class Circle extends AbstractShape { }
public class SVGStyle implements ISVGAttribute { }
public class ColorParseException extends Exception { }

// ❌ Mauvais
public class circle { }          // minuscule
public class C { }               // trop court
public class MyClass123 { }      // chiffres inutiles
```

#### Constantes

```java
// ✅ Bon : UPPER_CASE, static final
public static final Color RED = new Color("#ff0000");
public static final Color BLACK = new Color("#000000");
public static final int MAX_RETRIES = 3;

// ❌ Mauvais
public static Color red;         // pas de final
private static int maxRetries;   // pas UPPER_CASE
```

#### Méthodes et variables

```java
// ✅ Bon : camelCase, noms génériques
public void move(double dx, double dy) { }
public double getWidth() { }
public Optional<Double> strokeWidth() { }
private int calculateArea() { }
private List<Point> points = new ArrayList<>();

// ❌ Mauvais
public void MOVE() { }           // UPPER_CASE
public void m(double d1) { }     // trop court
private int max_width;           // snake_case
```

#### Getters/Setters de style fluent

```java
// ✅ Bon : méthode retourne `this` pour le chaînage
public SVGStyle fillColor(Color color) {
    this.fillColor = Optional.of(color);
    return this;  // Permet: style.fillColor(...).strokeWidth(...)
}

public Optional<Color> fillColor() {
    return fillColor;
}

// Utilisation
SVGStyle style = new SVGStyle();
style.fillColor(Color.RED)
     .strokeColor(Color.BLACK)
     .strokeWidth(2.0);  // Chaînage fluide
```

### Optionals au lieu de null

```java
// ✅ Bon : utiliser Optional pour les valeurs optionnelles
private Optional<Double> fontSize = Optional.empty();

public SVGStyle fontSize(double size) {
    this.fontSize = Optional.of(size);
    return this;
}

public Optional<Double> fontSize() {
    return fontSize;
}

// Utilisation
Optional<Double> size = style.fontSize();
if (size.isPresent()) {
    System.out.println("Size: " + size.get());
}

// Ou (plus moderne)
size.ifPresent(s -> System.out.println("Size: " + s));

// ❌ Mauvais
private Double fontSize = null;  // null au lieu d'Optional
if (fontSize != null) { }        // Vérifie null instead of Optional
```

### Constructeurs

```java
public class Circle extends AbstractShape {
    private double radius;
    private Point center;

    // ✅ Constructeur par défaut (obligatoire pour l'import SVG)
    public Circle() {
        this.radius = 0;
        this.center = new Point(0, 0);
    }

    // ✅ Constructeur avec paramètres tous nécessaires
    public Circle(double x, double y, double radius) {
        this.radius = radius;
        this.center = new Point(x, y);
    }

    // ✅ Constructeur avec Point
    public Circle(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }
}
```

**Règles** :

- ✅ Toujours inclure un constructeur **sans paramètres** (obligatoire pour `SVGImport`)
- ✅ Plusieurs constructeurs = flexibilité pour les usagers
- ✅ Valider les inputs (voir [Validation](#validation))

### Validation des inputs

```java
// ✅ Bon : valide les inputs
public Circle(double x, double y, double radius) {
    if (radius < 0) {
        throw new IllegalArgumentException("Radius cannot be negative: " + radius);
    }
    this.radius = radius;
    this.center = new Point(x, y);
}

// ✅ Bon : null checks
public void addChild(IShape child) {
    if (child == null) {
        throw new IllegalArgumentException("Child cannot be null");
    }
    this.children.add(child);
}

// ❌ Mauvais : pas de validation
public Circle(double x, double y, double radius) {
    this.radius = radius;  // Pas de vérification si négatif
    this.center = new Point(x, y);
}
```

## Système d'annotations SVG

### Annotations clés

#### `@SVGTag(String value)`

Marque une classe comme élément SVG exportable.

```java
@SVGTag("circle")       // Exporte en <circle>
public class Circle { }

@SVGTag("rect")         // Exporte en <rect>
public class Rectangle { }

@SVGTag("g")            // Exporte en <g> (groupement)
public class Group { }
```

**Règles** :

- ✅ Obligatoire pour toutes les classes implémentant `ISVGShape`
- ✅ Le nom doit correspondre à un tag SVG valide
- ❌ Les noms invalides génèrent une erreur de compilation

#### `@SVGField(String[] value)`

Mappe un champ Java à un attribut SVG.

```java
// Simple : un champ = un attribut
@SVGField("r")
private double radius;  // Export: r="50"

@SVGField("cx")
private double centerX; // Export: cx="100"

// Point : un champ = deux attributs
@SVGField({"cx", "cy"})
private Point center;   // Export: cx="100" cy="50"

// Conteneur : un champ = liste d'enfants
@SVGField
private List<IShape> children;  // Export: chaque enfant est un sous-élément
```

**Règles** :

- ✅ Utilisez un tableau pour les points (2 attributs)
- ✅ Utilisez un nom générique pour les attributs `ISVGAttribute` (style, transform)
- ✅ Pas d'annotation = pas d'export

#### `@SVGPoint`, `@SVGPointX`, `@SVGPointY`

Marque une classe comme point et ses coordonnées.

```java
@SVGPoint
public class Point implements IShape {
    @SVGPointX
    private double x;

    @SVGPointY
    private double y;
}
```

**Règles** :

- ✅ Obligatoire pour la classe `Point`
- ✅ Une seule classe peut avoir `@SVGPoint`

#### `@SVGContent`

Marque un champ comme contenu textuel (au lieu d'attribut).

```java
@SVGTag("text")
public class Text extends AbstractShape {
    @SVGContent
    private String content;  // Export: <text>content</text>

    @SVGField("x")
    private double x;
}
```

**Règles** :

- ✅ Permet le contenu textuel d'une balise
- ❌ Une seule annotation `@SVGContent` par classe

## Formatage et style

### Formatage automatique

**Activez le formatage lors de la sauvegarde** :

1. Ouvrez `Settings` (Ctrl+`,` ou Cmd+`,`)
2. Cherchez "Format On Save"
3. Cochez `Editor: Format On Save`

Ou manuellement : `Shift+Alt+F` (format le fichier courant)

### Longueur des lignes

```java
// ✅ Bon : lisible, pas trop long
public String getDescription(int indent) {
    StringBuilder sb = new StringBuilder();
    sb.append(super.getDescription(indent));
    sb.append(" R=").append(this.radius);
    return sb.toString();
}

// ❌ Mauvais : ligne trop longue
public String getDescription(int indent) { return " ".repeat(Math.max(0, indent)) + this.getClass().getSimpleName() + " R=" + this.radius; }
```

**Règles** :

- ✅ Maximum **120 caractères** par ligne (si possible)
- ✅ Cassez les lignes logiquement

### Espaces et blank lines

```java
public class Circle extends AbstractShape {
    // Blank line après les déclarations de champs
    private double radius;
    private Point center;

    // Blank line avant les constructeurs
    public Circle() {
        this.radius = 0;
    }

    // Blank line entre les méthodes publiques
    @Override
    public double getWidth() {
        return 2 * this.radius;
    }

    // Blank line avant les méthodes privées
    private void validate() {
        // ...
    }
}
```

## Documentation et Javadoc

### Javadoc de classe

```java
/**
 * Représente un cercle implémentant l'interface IShape.
 * Un cercle est défini par un centre (x, y) et un rayon.
 *
 * Les propriétés SVG (style, transformation) sont héritées de AbstractShape.
 *
 * @see AbstractShape
 * @see IShape
 * @see Point
 */
@SVGTag("circle")
public final class Circle extends AbstractShape {
    // ...
}
```

**Règles** :

- ✅ Première ligne = description courte (une phrase)
- ✅ Lignes suivantes = détails supplémentaires
- ✅ Utilisez `@see` pour les références
- ✅ Mentionnez les invariants (ex: "un rayon >= 0")

### Javadoc de méthode

```java
/**
 * Définit la largeur du trait (stroke-width).
 *
 * @param width la largeur du trait (doit être >= 0)
 * @return cette instance pour enchaînage de méthodes
 */
public SVGStyle strokeWidth(double width) {
    if (width < 0) {
        throw new IllegalArgumentException("Width cannot be negative: " + width);
    }
    this.strokeWidth = Optional.of(width);
    return this;
}
```

**Règles** :

- ✅ Première ligne = description courte
- ✅ `@param` pour chaque paramètre
- ✅ `@return` pour le type retourné (non-void)
- ✅ `@throws` pour les exceptions
- ✅ Mentionnez le comportement de chaînage si applicable

### Javadoc de field

```java
/**
 * La largeur du trait (stroke-width) en unités SVG.
 * Stockée en tant qu'Optional pour indiquer la présence.
 * Une valeur vide signifie que la propriété n'a pas été définie.
 */
private Optional<Double> strokeWidth = Optional.empty();
```

### Commentaires en ligne

```java
// ✅ Bon : utile et court
@Override
public void resize(double px, double py) {
    // Ignorer le deuxième argument pour un cercle (invariant)
    this.radius = this.radius * px;
}

// ❌ Mauvais : commentaire évident ou trop court
@Override
public void resize(double px, double py) {
    // multiplie le rayon
    this.radius = this.radius * px;  // += px???
}
```

## Tests unitaires

### Structure des tests

```
test/fr/univrennes/istic/l2gen/geometry/
├── PointTest.java
├── AbstractShapeTest.java
└── base/
    ├── CircleTest.java
    ├── LineTest.java
    └── RectangleTest.java
```

**Règles** :

- ✅ Même structure de packages que `src`
- ✅ Noms : `OriginalClass` → `OriginalClassTest`

### Exemple de test

```java
import org.junit.Test;
import org.junit.Assert.*;

public class CircleTest extends AbstractShapeTest<Circle> {

    @Override
    public Circle create() {
        // Crée une instance pour les tests
        return new Circle(100, 100, 50);
    }

    @Test
    public void testCenter() {
        Circle circle = create();
        Point center = circle.getCenter();

        assertEquals(100, center.getX(), 0.001);
        assertEquals(100, center.getY(), 0.001);
    }

    @Test
    public void testRadiusValidation() {
        // Vérifie que rayon négatif lève une exception
        assertThrows(
            IllegalArgumentException.class,
            () -> new Circle(0, 0, -5)
        );
    }
}
```

**Règles** :

- ✅ Une méthode de test par comportement testé
- ✅ Noms explicites : `test<Behavior>` (ex: `testRadiusValidation`)
- ✅ Arrange-Act-Assert (AAA pattern)
- ✅ Testez les cas d'erreur aussi (exceptions, limites)

### Avant de committer

```bash
# 1. Vérifier que le projet compile
# VS Code compile automatiquement, mais vérifiez qu'il y a pas d'erreurs

# 2. Exécuter tous les tests
# Cliquez sur "Run All Tests" dans VS Code
# Ou utilisez le terminal (dépend de votre setup)

# 3. Vérifier que tous les tests passent
# ✅ Pas de tests rouge
```

## Messages de commit

### Format type

```
<type>: <description courte et claire>

<détails optionnels (si besoin)>
```

### Types de commit

| Type       | Utilisation                         | Exemple                                    |
| ---------- | ----------------------------------- | ------------------------------------------ |
| `feat`     | Nouvelle fonctionnalité             | `feat: add gradient support to SVG export` |
| `fix`      | Bugfix                              | `fix: circle rotation should be no-op`     |
| `refactor` | Refactoring ou amélioration de code | `refactor: simplify Color parsing logic`   |
| `docs`     | Documentation, commentaires         | `docs: add Javadoc to Point class`         |
| `test`     | Tests                               | `test: add validation tests for Circle`    |

### Exemples valides

```
feat: add blur filter support with feGaussianBlur
```

```
fix: prevent negative radius in Circle constructor
```

```
docs: improve Javadoc for SVGStyle class
```

### Règles de dénomination

- ✅ **Anglais obligatoire** (même si la documentation est en français)
- ✅ **Impératif** : "add" et non "added" ou "adds"
- ✅ **Court** : première ligne ≤ 50 caractères
- ❌ Pas de points (.) ni d'exclamations (!)
- ❌ Pas de majuscule au début (sauf Types Java)

## Workflow de contribution

### 1️⃣ Récupérer les derniers changements

```bash
git pull
```

### 2️⃣ Créer votre tâche

- Créez une nouvelle classe ou modifiez une existante
- Respectez les conventions de ce guide

### 3️⃣ Tester votre code

```bash
# VS Code teste automatiquement à la sauvegarde
# Mais exécutez aussi manuellement:
# - Cliquez "Run Tests" sur la classe de test
# - Ou utilisez Ctrl+Shift+D pour le débogage
```

### 4️⃣ Formater avant de committer

```bash
# VS Code formate automatiquement (si "Format On Save" activé)
# Sinon, manuellement: Shift+Alt+F
```

### 5️⃣ Committer

```bash
# Vérifier les changements
git status

# Ajouter les changements
git add .

# Committer avec un message clair
git commit -m "feat: add circle resize validation"
```

### 6️⃣ Pousser

```bash
git push
```

## Checklist avant de committer

- [ ] Le code **compile** (pas d'erreurs en rouge)
- [ ] Tous les **tests passent** (verts)
- [ ] **Javadoc complète** sur classes et méthodes publiques
- [ ] Pas d'imports inutilisés (VS Code nettoie à la sauvegarde)
- [ ] **Formatage appliqué** (`Shift+Alt+F`)
- [ ] **Pas de TODO** hérités (ou commentez pourquoi)
- [ ] **Noms explicites** (variables, méthodes, classes)
- [ ] **Validation des inputs** (exceptions si besoin)
- [ ] **Message de commit clair** en anglais
- [ ] **Annotations SVG** ajoutées (`@SVGTag`, `@SVGField` si applicable)

## Ressources utiles

### Documentation du projet

- 📚 [DOCUMENTATION.md](docs/DOCUMENTATION.md) - Architecture complète
- 👥 [MEMBERS.md](docs/MEMBERS.md) - Liste des contributeurs
- 📋 [TODOS.md](docs/TODOS.md) - Tâches en cours
- 📖 [README.md](README.md) - Vue d'ensemble
- 📐 [UML](uml/src.svg) - Diagramme de classes UML (mis à jour régulièrement)

### Standards et références

- 📖 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- 📖 [Oracle Java Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-135161.html)
- 📖 [Javadoc Guide](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- 📖 [SVG Spec](https://www.w3.org/TR/SVG11/) - Pour le système d'annotations

### Git

- 📘 [Git Cheat Sheet](https://git-scm.com/cheat-sheet)
- 📘 [VS Code Git Guide](https://code.visualstudio.com/docs/sourcecontrol/quickstart)
- 📘 [Conventional Commits](https://www.conventionalcommits.org/) - Standard pour les messages
