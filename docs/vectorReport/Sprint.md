# Sprint 1 : PanelType

### 🏗️ TODO 1 : Affichage et Sélection de Filtres

| ID   | Titre              | Description (Carte)                                                                 | Complexité | Importance |
|------|--------------------|-----------------------------------------------------------------------------------|------------|------------|
| #101 | Liste des filtres  | En tant qu'utilisateur, je veux voir une liste de boutons de filtres disponibles afin d'identifier les options d'affichage. | 1/5        | 5/5        |
| #102 | Toggle de filtre   | En tant qu'utilisateur, je veux activer ou désactiver un filtre via une case à cocher afin de personnaliser mon graphique. | 2/5        | 5/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #101 : Au chargement d'un fichier, le panneau latéral génère dynamiquement des composants basés sur les colonnes du CSV.
- Pour #102 : Le clic sur une `JCheckBox` déclenche un événement qui recalcule immédiatement l'ensemble de données visible.

---

### ⚙️ TODO 2 : Gestion Avancée des Filtres

| ID   | Titre              | Description (Carte)                                                                                       | Complexité | Importance |
|------|--------------------|---------------------------------------------------------------------------------------------------------|------------|------------|
| #201 | Tri par Header     | En tant qu'utilisateur, je veux configurer le tri (croissant/décroissant) basé sur les en-têtes pour organiser les données. | 3/5        | 4/5        |
| #202 | Reset Global       | En tant qu'utilisateur, je veux réinitialiser tous les filtres en un clic afin de revenir rapidement à la vue initiale. | 1/5        | 3/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #201 : Des boutons fléchés ou un menu déroulant permettent de définir l'ordre de tri de la collection.
- Pour #202 : Le bouton "Reset" décoche toutes les cases et efface les saisies textuelles de filtrage.

---

### 🚀 TODO 3 : Expérience Utilisateur et Robustesse

| ID   | Titre              | Description (Carte)                                                                                      | Complexité | Importance |
|------|--------------------|--------------------------------------------------------------------------------------------------------|------------|------------|
| #301 | Filtres par plage  | En tant qu'utilisateur, je veux filtrer par plage de valeurs (ex: prix entre X et Y) afin d'isoler des données précises. | 4/5        | 5/5        |
| #302 | Alerte "No Result" | En tant qu'utilisateur, je veux être averti si une combinaison de filtres ne retourne aucun résultat. | 2/5        | 3/5        |
| #303 | Auto-update        | En tant qu'utilisateur, je veux que le graphique central s'actualise en temps réel à chaque modification de filtre. | 5/5        | 4/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #301 : L'interface propose des champs `JTextField` pour saisir des valeurs numériques minimales et maximales.
- Pour #302 : Si le résultat du filtrage est une liste vide, un message d'avertissement explicite apparaît en rouge.
- Pour #303 : L'architecture utilise le pattern **Observer** : le Filter Panel notifie le Controller, qui demande au Chart Panel de se redessiner.

---

- **Estimation totale** : ~15 heures de développement.
- **Architecture** : Séparation stricte entre le moteur de filtrage (Logique) et les composants Swing (Vue).

---

### Récapitulatif des TODOs

#### TODO 1 : Affichage et sélection de filtres
- Afficher dynamiquement les options de filtrage selon les données reçues.
- Utiliser des cases à cocher pour une sélection multiple simple.

#### TODO 2 : Gestion avancée des filtres
- Trier les données en fonction du header sélectionné.
- Proposer une fonction de remise à zéro rapide de l'état du panneau.

#### TODO 3 : Expérience utilisateur et robustesse
- Gérer les filtres complexes (numériques, catégoriels).
- Assurer une réactivité maximale de l'interface (fluidité du graphique).
- Prévenir l'utilisateur en cas de recherche infructueuse.

#####

# Sprint 2 : Panel BottomBar

### 🏗️ TODO 1 : Affichage des Données et Structure

| ID   | Titre              | Description (Carte)                                                                 | Complexité | Importance |
|------|--------------------|-----------------------------------------------------------------------------------|------------|------------|
| #101 | Grille de données  | En tant qu'utilisateur, je veux voir mes données CSV dans un tableau structuré afin de vérifier les valeurs brutes. | 2/5        | 5/5        |
| #102 | En-têtes dynamiques| En tant qu'utilisateur, je veux que les colonnes soient nommées selon le fichier source afin de comprendre la structure. | 1/5        | 5/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #101 : Utilisation d'un composant `JTable` intégré dans un `JScrollPane` pour permettre le défilement fluide.
- Pour #102 : La première ligne du fichier CSV est automatiquement extraite pour définir les noms des colonnes du `DefaultTableModel`.

---

### ⚙️ TODO 2 : Navigation et Performance

| ID   | Titre              | Description (Carte)                                                                                       | Complexité | Importance |
|------|--------------------|---------------------------------------------------------------------------------------------------------|------------|------------|
| #201 | Pagination         | En tant qu'utilisateur, je veux naviguer par pages (First/Next/Prev/Last) afin de gérer de gros volumes sans ralentissement. | 3/5        | 4/5        |
| #202 | Compteur de lignes | En tant qu'utilisateur, je veux voir le nombre total de lignes et le temps de chargement afin de juger de la taille du jeu de données. | 1/5        | 3/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #201 : Des boutons sous le tableau permettent de changer de page ; le tableau ne charge qu'un bloc de données à la fois pour économiser la mémoire.
- Pour #202 : Une barre d'état (Status Bar) affiche "Rows: X | Cols: Y" et le temps de parsing en secondes/millisecondes en bas à gauche.

---

### 🚀 TODO 3 : Interaction et Synchronisation

| ID   | Titre              | Description (Carte)                                                                                      | Complexité | Importance |
|------|--------------------|--------------------------------------------------------------------------------------------------------|------------|------------|
| #301 | Masquage de colonnes| En tant qu'utilisateur, je veux pouvoir masquer ou afficher des colonnes spécifiques afin de simplifier ma vue d'analyse. | 3/5        | 4/5        |
| #302 | Exportation Rapide | En tant qu'utilisateur, je veux un bouton de sauvegarde pour exporter l'état actuel de ma table filtrée. | 2/5        | 3/5        |
| #303 | Synchro Filtres    | En tant qu'utilisateur, je veux que le tableau se mette à jour dès qu'un filtre est appliqué dans le panneau latéral. | 4/5        | 5/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #301 : Un bouton "Show all columns" réinitialise la visibilité via le `TableColumnModel`.
- Pour #302 : Le bouton "Save" ouvre un `JFileChooser` pour exporter les données visibles en format CSV.
- Pour #303 : Le composant écoute les changements du modèle de données (Pattern Observer) et se rafraîchit automatiquement.

---

- **Estimation totale** : ~12 heures de développement.
- **Architecture** : Utilisation d'un `AbstractTableModel` personnalisé pour séparer les données brutes de l'affichage Swing.

---

### Récapitulatif des TODOs

#### TODO 1 : Affichage des données et structure
- Visualiser les données dans une grille `JTable`.
- Adapter automatiquement les colonnes aux en-têtes du fichier chargé.

#### TODO 2 : Navigation et performance
- Naviguer entre les pages pour éviter de figer l'interface avec des fichiers volumineux.
- Afficher les statistiques de chargement (nombre de lignes, temps d'exécution).

#### TODO 3 : Interaction et synchronisation
- Masquer les colonnes inutiles pour l'analyse en cours.
- Sauvegarder les données affichées.
- Réagir en temps réel aux actions effectuées dans le **Filter Panel (PanelType)**.

#####

# Sprint 3 : MainPanel

### 🏗️ TODO 1 : Configuration et Type de Rendu

| ID   | Titre               | Description (Carte)                                                                 | Complexité | Importance |
|------|---------------------|-----------------------------------------------------------------------------------|------------|------------|
| #101 | Sélection du type   | En tant qu'utilisateur, je veux choisir entre différents types de graphiques (Pie, Bar) afin d'adapter la vue à mes données. | 2/5        | 5/5        |
| #102 | Titrage dynamique   | En tant qu'utilisateur, je veux saisir un titre personnalisé afin de nommer mon rapport de manière explicite. | 1/5        | 4/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #101 : Un menu déroulant (`JComboBox`) permet de basculer instantanément entre les modes de rendu.
- Pour #102 : Le texte saisi dans le `JTextField` "Title" s'affiche en temps réel au sommet de la zone de dessin.

---

### ⚙️ TODO 2 : Paramètres de Données et Axes

| ID   | Titre               | Description (Carte)                                                                                       | Complexité | Importance |
|------|---------------------|---------------------------------------------------------------------------------------------------------|------------|------------|
| #201 | Agrégation des données | En tant qu'utilisateur, je veux choisir la méthode de calcul (Somme, Moyenne, Comptage) afin d'analyser mes indicateurs. | 4/5        | 5/5        |
| #202 | Gestion des Axes    | En tant qu'utilisateur, je veux activer/désactiver les axes et modifier leurs labels pour améliorer la lisibilité. | 3/5        | 4/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #201 : Le moteur de rendu recalcule les portions ou les barres en fonction de la fonction statistique choisie.
- Pour #202 : Pour les graphiques de type "Bar", les labels X et Y sont mis à jour dynamiquement selon les sélections de colonnes.

---

### 🚀 TODO 3 : Personnalisation Visuelle et Export

| ID   | Titre               | Description (Carte)                                                                                      | Complexité | Importance |
|------|---------------------|--------------------------------------------------------------------------------------------------------|------------|------------|
| #301 | Style et Couleurs   | En tant qu'utilisateur, je veux modifier les couleurs (Fill/Stroke) et l'opacité afin de personnaliser l'esthétique. | 3/5        | 4/5        |
| #302 | Redimensionnement   | En tant qu'utilisateur, je veux définir la largeur et la hauteur du rendu afin de préparer mon export. | 2/5        | 3/5        |
| #303 | Export Vectoriel    | En tant qu'utilisateur, je veux exporter le graphique final en format SVG ou PNG pour l'utiliser ailleurs. | 4/5        | 5/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #301 : L'utilisation de `JColorChooser` permet de modifier les propriétés graphiques des objets `Shape` du rendu.
- Pour #302 : Les champs numériques Width/Height forcent le redimensionnement de la zone de dessin (Canvas).
- Pour #303 : Le menu `File > Export` génère un fichier image fidèle à ce qui est affiché à l'écran.

---

- **Estimation totale** : ~18 heures de développement.
- **Architecture** : Pattern Strategy pour les différents types de graphiques (chaque type de graphique est une stratégie de rendu différente).

---

### Récapitulatif des TODOs

#### TODO 1 : Configuration et type de rendu
- Choisir entre Pie Chart, Bar Chart ou Line Chart.
- Ajouter un titre principal au graphique.

#### TODO 2 : Paramètres de données et axes
- Appliquer des fonctions mathématiques sur les données (Somme, Moyenne).
- Configurer les légendes et les étiquettes des axes.

#### TODO 3 : Personnalisation visuelle et export
- Choisir les couleurs de remplissage et de contour via une palette.
- Régler la transparence et la taille du graphique.
- Exporter le résultat final en haute qualité.

####

# Sprint 4 : Panel TopBar

### 🏗️ TODO 1 : Gestion des Fichiers et Persistance

| ID   | Titre              | Description (Carte)                                                                 | Complexité | Importance |
|------|--------------------|-----------------------------------------------------------------------------------|------------|------------|
| #101 | Menu Open/Load     | En tant qu'utilisateur, je veux un menu "File > Open" afin de sélectionner un fichier CSV sur mon disque dur. | 1/5        | 5/5        |
| #102 | Menu Save Project  | En tant qu'utilisateur, je veux pouvoir sauvegarder ma configuration actuelle via le menu afin de la retrouver plus tard. | 2/5        | 4/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #101 : L'action déclenche un `JFileChooser` filtré sur l'extension `.csv`.
- Pour #102 : Le menu propose "Save" et "Save As..." avec les raccourcis standards (`Ctrl+S`).

---

### ⚙️ TODO 2 : Contrôle de l'Espace de Travail (Affichage)

| ID   | Titre              | Description (Carte)                                                                                       | Complexité | Importance |
|------|--------------------|---------------------------------------------------------------------------------------------------------|------------|------------|
| #201 | Toggle des Panels  | En tant qu'utilisateur, je veux afficher ou masquer les différents panneaux (Table, Chart, Filter) pour libérer de l'espace. | 3/5        | 4/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #201 : Le menu `View > Panels` contient des `JCheckBoxMenuItem` synchronisés avec la visibilité des composants.
- Pour #202 : L'application recharge le `LookAndFeel` (FlatLaf) sans redémarrer le programme.

---

### 🚀 TODO 3 : Aide et Documentation

| ID   | Titre              | Description (Carte)                                                                                      | Complexité | Importance |
|------|--------------------|--------------------------------------------------------------------------------------------------------|------------|------------|
| #301 | Menu d'aide        | En tant qu'utilisateur, je veux accéder à un manuel ou une aide rapide afin de comprendre les fonctions complexes. | 1/5        | 3/5        |
| #302 | À propos (About)   | En tant qu'utilisateur, je veux voir la version du logiciel et les crédits afin de savoir si je suis à jour. | 1/5        | 2/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #301 : Le menu `Help > User Guide` ouvre le fichier PDF ou Markdown du manuel.
- Pour #302 : Une fenêtre `JOptionPane` s'affiche avec le logo, la version 1.0 et les noms des contributeurs.

---

- **Estimation totale** : ~6 heures de développement.
- **Architecture** : Utilisation de la classe `JMenuBar` avec des `Action` réutilisables pour lier les menus aux boutons de l'interface (ex: le bouton "Save" du tableau fait la même chose que "File > Save").

---

### Récapitulatif des TODOs

#### TODO 1 : Gestion des fichiers et persistance
- Ouvrir un explorateur de fichiers pour charger des données.
- Gérer l'enregistrement et l'exportation via le menu "File".

#### TODO 2 : Contrôle de l'espace de travail
- Personnaliser l'affichage en masquant les outils inutiles.
- Modifier l'apparence visuelle (thèmes).

#### TODO 3 : Aide et documentation
- Accéder aux informations sur le projet.
- Ouvrir l'aide contextuelle.