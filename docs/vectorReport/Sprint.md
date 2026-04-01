# Sprint 1 : Filtering & Core Architecture ✅ COMPLÉTÉ

### 🏗️ TODO 1 : Système de Filtres Avancé ✅

| ID   | Titre              | Description (Carte)                                                                 | Complexité | Importance | Status |
|------|--------------------|-----------------------------------------------------------------------------------|------------|-----------|--------|
| #101 | Tri par Header     | En tant qu'utilisateur, je veux configurer le tri (croissant/décroissant) basé sur les en-têtes pour organiser les données. | 3/5        | 5/5        | ✅ FAIT |
| #102 | Filtres par plage  | En tant qu'utilisateur, je veux filtrer par plage de valeurs (ex: prix entre X et Y) afin d'isoler des données précises. | 4/5        | 5/5        | ✅ FAIT |
| #103 | Recherche textuelle| En tant qu'utilisateur, je veux rechercher du texte dans une colonne pour trouver des données spécifiques. | 2/5        | 4/5        | ✅ FAIT |
| #104 | Top/Bottom N       | En tant qu'utilisateur, je veux afficher les N premières ou dernières lignes (top/bottom N). | 3/5        | 3/5        | ✅ FAIT |
| #105 | Filtres vides      | En tant qu'utilisateur, je veux afficher ou masquer les lignes vides pour nettoyer les données. | 2/5        | 3/5        | ✅ FAIT |

**✅ Confirmation (Critères d'acceptation)**
- Les filtres sont implémentés via la classe `Filter` avec support de `SORT`, `RANGE`, `SEARCH`, `TOP_N`, `BOTTOM_N`, `EMPTY`, `NOT_EMPTY`.
- Les filtres sont appliqués dynamiquement via `DataTable.addFilter()` et chainables.
- Les en-têtes de colonne sont interactifs : clic gauche = sélection, clic droit = menu contextuel.
- L'interface rafraîchit automatiquement le tableau après chaque modification.

---

### ⚙️ TODO 2 : Gestion de l'État et Persistance ✅

| ID   | Titre              | Description (Carte)                                                                                       | Complexité | Importance | Status |
|------|--------------------|---------------------------------------------------------------------------------------------------------|-----------|-----------|--------|
| #201 | État de la table   | Maintenir l'état de pagination et des filtres actifs dans `TableViewState`.                             | 2/5        | 4/5        | ✅ FAIT |
| #202 | Cache de blocs     | Implémenter un cache LRU de `MAX_CACHED_BLOCKS` pour optimiser la mémoire avec des fichiers volumineux. | 3/5        | 3/5        | ✅ FAIT |

**✅ Confirmation (Critères d'acceptation)**
- L'état du tableau est géré via l'enum `TableViewState` (EMPTY, LIST, TABLE).
- Un `LinkedHashMap` implémente un cache LRU avec limite configurable.
- Le chargement de données se fait par blocs de `BLOCK_SIZE` (10 000 lignes).

---

### 🚀 TODO 3 : Système de Statistiques ✅

| ID   | Titre              | Description (Carte)                                                                                      | Complexité | Importance | Status |
|------|--------------------|--------------------------------------------------------------------------------------------------------|-----------|-----------|--------|
| #301 | Calculs statistiques | En tant qu'utilisateur, je veux voir min, max, moyenne, somme de chaque colonne numérique.             | 4/5        | 5/5        | ✅ FAIT |
| #302 | Analyses avancées  | Null rate, cardinality ratio, IQR, skewness, coefficient de variation, corrélation.                    | 5/5        | 4/5        | ✅ FAIT |
| #303 | Affichage temps réel | Les statistiques s'affichent dans la barre inférieure au clic sur une colonne (avec SwingWorker). | 3/5        | 4/5        | ✅ FAIT |

**✅ Confirmation (Critères d'acceptation)**
- La classe `StatisticService` expose des méthodes statiques `computeBase()`, `computeSummary()`, etc.
- Les calculs utilisent DuckDB via des requêtes SQL optimisées.
- Une fenêtre `StatisticsDialog` affiche les résultats détaillés.

---

- **Estimation totale** : ~15 heures ✅ RÉALISÉES
- **Architecture** : Séparation stricte entre le moteur de filtrage (Core) et les composants Swing (GUI).

---

### Récapitulatif des TODOs ✅ COMPLÉTÉS

#### TODO 1 : Système de filtres avancé ✅
- Tri multi-directionnel (croissant/décroissant) sur colonnes.
- Filtrage par plage numérique (min/max).
- Recherche textuelle sensible à la casse.
- Sélection Top-N ou Bottom-N.
- Affichage/masquage des lignes vides.
- Statistiques détaillées (min, max, moyenne, somme, IQR, skewness, etc.).

#### TODO 2 : Gestion de l'état et persistance ✅
- État de pagination et filtres actifs dans `TableViewState`.
- Cache LRU de blocs pour optimiser la mémoire.
- Chargement par blocs pour fichiers volumineux.

#### TODO 3 : Système de statistiques ✅
- Calculs SQL optimisés via DuckDB (min, max, avg, sum).
- Analyses statistiques avancées (correlation, null rate, etc.).
- Affichage temps réel dans la barre inférieure.

#####

# Sprint 2 : Data Table & Visualization ✅ PARTIELLEMENT COMPLÉTÉ

### 🏗️ TODO 1 : Affichage des Données et Structure ✅

| ID   | Titre              | Description (Carte)                                                                 | Complexité | Importance | Status |
|------|--------------------|-----------------------------------------------------------------------------------|-----------|-----------|--------|
| #101 | Grille de données  | En tant qu'utilisateur, je veux voir mes données dans un tableau structuré afin de vérifier les valeurs brutes. | 2/5        | 5/5        | ✅ FAIT |
| #102 | En-têtes dynamiques| En tant qu'utilisateur, je veux que les colonnes soient nommées selon le fichier source afin de comprendre la structure. | 1/5        | 5/5        | ✅ FAIT |

**✅ Confirmation (Critères d'acceptation)**
- Utilisation d'un composant `JTable` intégré dans un `JScrollPane` avec défilement fluide.
- Les colonnes sont extraites automatiquement du fichier Parquet via DuckDB.
- Affichage des types de données avec icônes dans les en-têtes (TEXT, INTEGER, DOUBLE, DATE).

---

### ⚙️ TODO 2 : Navigation et Performance ✅

| ID   | Titre              | Description (Carte)                                                                                       | Complexité | Importance | Status |
|------|--------------------|---------------------------------------------------------------------------------------------------------|-----------|-----------|--------|
| #201 | Pagination         | En tant qu'utilisateur, je veux naviguer par pages (First/Next/Prev/Last) afin de gérer de gros volumes. | 3/5        | 4/5        | ✅ FAIT |
| #202 | Compteur de lignes | En tant qu'utilisateur, je veux voir le nombre total de lignes et colonnes afin de juger la taille du jeu de données. | 1/5        | 3/5        | ✅ FAIT |

**✅ Confirmation (Critères d'acceptation)**
- Boutons de pagination (First/Prev/Next/Last) sous le tableau pour changer de page.
- Barre d'état en bas affichant "Rows: X | Cols: Y" et le temps de parsing.
- Chargement par blocs de 10 000 lignes pour économiser la mémoire.

---

### 🚀 TODO 3 : Interaction et Synchronisation ✅

| ID   | Titre              | Description (Carte)                                                                                      | Complexité | Importance | Status |
|------|--------------------|--------------------------------------------------------------------------------------------------------|-----------|-----------|--------|
| #301 | Masquage de colonnes| En tant qu'utilisateur, je veux masquer ou afficher des colonnes spécifiques afin de simplifier ma vue. | 3/5        | 4/5        | ✅ FAIT |
| #302 | Contexte colonne   | En tant qu'utilisateur, je veux un menu contextuel (clic droit) pour accéder à des actions sur les colonnes. | 2/5        | 4/5        | ✅ FAIT |
| #303 | Synchro Filtres    | En tant qu'utilisateur, je veux que le tableau se mette à jour dès qu'un filtre est appliqué. | 4/5        | 5/5        | ✅ FAIT |

**✅ Confirmation (Critères d'acceptation)**
- Clic droit sur un en-tête affiche un menu contextuel avec options (masquer, statistiques, etc.).
- Les colonnes masquées sont retirées du modèle via `TableColumnModel.removeColumn()`.
- Le tableau écoute les changements de `DataTable` et rafraîchit automatiquement via `fireTableDataChanged()`.

---

- **Estimation totale** : ~12 heures ✅ RÉALISÉES
- **Architecture** : Modèle personnalisé `TableModel extends AbstractTableModel` pour gérer les blocs de données.

---

### Récapitulatif des TODOs ✅ COMPLÉTÉS

#### TODO 1 : Affichage des données et structure ✅
- Visualiser les données dans une grille `JTable` avec colonnes dynamiques.
- Adapter les colonnes aux en-têtes du fichier chargé.
- Afficher les types de données avec des icônes.

#### TODO 2 : Navigation et performance ✅
- Navigation par pages (First/Prev/Next/Last).
- Affichage des statistiques de chargement (nombre de lignes, temps).
- Cache LRU pour optimiser la mémoire.

#### TODO 3 : Interaction et synchronisation ✅
- Menu contextuel sur les colonnes (masquer, statistiques, tri).
- Masquage/affichage des colonnes.
- Synchronisation en temps réel avec les filtres.

#####

# Sprint 3 : Report & Visualization Settings 🚧 EN COURS

### 🏗️ TODO 1 : Configuration et Type de Rendu 🟡 PARTIELLEMENT

| ID   | Titre               | Description (Carte)                                                                 | Complexité | Importance | Status |
|------|---------------------|-----------------------------------------------------------------------------------|-----------|-----------|--------|
| #101 | Sélection du type   | En tant qu'utilisateur, je veux choisir entre différents types de graphiques (Pie, Bar, Columns). | 2/5        | 5/5        | 🟡 PARTIELLEMENT |
| #102 | Titrage dynamique   | En tant qu'utilisateur, je veux saisir un titre personnalisé afin de nommer mon rapport. | 1/5        | 4/5        | 🟡 PARTIELLEMENT |

**🟡 État actuel**
- L'interface `SettingView` propose un dropdown pour sélectionner le type (Pie, Bar, Columns).
- Champs `JTextField` et spinners pour titre, largeur et hauteur.
- **À faire** : Implémenter le rendu graphique réel dans `NoteBook` et le lier aux changements de `SettingView`.

---

### ⚙️ TODO 2 : Paramètres de Données et Axes 🟡 PARTIELLEMENT

| ID   | Titre               | Description (Carte)                                                                                       | Complexité | Importance | Status |
|------|---------------------|---------------------------------------------------------------------------------------------------------|-----------|-----------|--------|
| #201 | Agrégation des données | En tant qu'utilisateur, je veux choisir la méthode de calcul (Somme, Moyenne, Comptage). | 4/5        | 5/5        | 🟡 PARTIELLEMENT |
| #202 | Gestion des Axes    | En tant qu'utilisateur, je veux activer/désactiver les axes et modifier leurs labels. | 3/5        | 4/5        | 🟡 PARTIELLEMENT |

**🟡 État actuel**
- Dropdown pour l'agrégation (Sum, Average, Count, Max, Min).
- Cases à cocher pour "Show Legend", "Show X Axis", "Show Y Axis".
- Champs pour les labels des axes.
- **À faire** : Connecter ces paramètres au moteur de rendu graphique.

---

### 🚀 TODO 3 : Personnalisation Visuelle et Export 🟡 PARTIELLEMENT

| ID   | Titre               | Description (Carte)                                                                                      | Complexité | Importance | Status |
|------|---------------------|--------------------------------------------------------------------------------------------------------|-----------|-----------|--------|
| #301 | Style et Couleurs   | En tant qu'utilisateur, je veux modifier les couleurs (Fill/Stroke) et l'opacité afin de personnaliser l'esthétique. | 3/5        | 4/5        | 🟡 PARTIELLEMENT |
| #302 | Redimensionnement   | En tant qu'utilisateur, je veux définir la largeur et la hauteur du rendu afin de préparer mon export. | 2/5        | 3/5        | 🟡 PARTIELLEMENT |
| #303 | Export Vectoriel    | En tant qu'utilisateur, je veux exporter le graphique final en format SVG pour l'utiliser ailleurs. | 4/5        | 5/5        | 🟠 À FAIRE |

**🟡 État actuel**
- Sélecteurs de couleur pour Fill et Stroke via `JColorChooser`.
- Slider pour l'opacité (0-100%).
- Spinners pour Width et Height.
- Dropdown pour la police et sa taille.
- **À faire** : Implémenter le rendu graphique et l'export SVG.

---

- **Estimation totale** : ~18 heures
- **En cours** : Architecture du moteur de rendu avec pattern Strategy.

---

### Récapitulatif des TODOs

#### TODO 1 : Configuration et type de rendu 🟡
- Interface prête avec dropdown pour le type de graphique.
- **À faire** : Implémenter le rendu (PieChart, BarChart, ColumnChart).

#### TODO 2 : Paramètres de données et axes 🟡
- Paramètres collectés dans `SettingView`.
- **À faire** : Connecter au moteur de rendu et recalculer à chaque changement.

#### TODO 3 : Personnalisation visuelle et export 🟡
- Panneau de personnalisation visuelle prêt.
- **À faire** : Implémenter le rendu et l'export SVG.

#####

# Sprint 4 : Top Menu Bar ✅ PARTIELLEMENT COMPLÉTÉ

### 🏗️ TODO 1 : Gestion des Fichiers et Persistance ✅

| ID   | Titre              | Description (Carte)                                                                 | Complexité | Importance | Status |
|------|--------------------|-----------------------------------------------------------------------------------|-----------|-----------|--------|
| #101 | Menu Open/Load     | En tant qu'utilisateur, je veux un menu "File > Open" afin de sélectionner des fichiers CSV/Parquet. | 1/5        | 5/5        | ✅ FAIT |
| #102 | Open from URL      | En tant qu'utilisateur, je veux ouvrir des fichiers depuis une URL pour charger des données distantes. | 2/5        | 3/5        | ✅ FAIT |

**✅ Confirmation (Critères d'acceptation)**
- Menu `File > Open` affiche un `SystemFileChooser` (file chooser natif).
- Menu `File > Open URL` demande l'URL via `JOptionPane`.
- Chargement asynchrone via `SwingWorker` avec barre de progression.
- Support du format Parquet via DuckDB.

---

### ⚙️ TODO 2 : Contrôle de l'Espace de Travail 🟠 À FAIRE

| ID   | Titre              | Description (Carte)                                                                                       | Complexité | Importance | Status |
|------|--------------------|---------------------------------------------------------------------------------------------------------|-----------|-----------|--------|
| #201 | Toggle des Panels  | En tant qu'utilisateur, je veux afficher ou masquer les différents panneaux (Table, Chart). | 3/5        | 4/5        | 🟠 À FAIRE |
| #202 | Thèmes             | En tant qu'utilisateur, je veux changer l'apparence visuelle (FlatLaf).                               | 1/5        | 2/5        | 🟠 À FAIRE |

**🟠 État actuel**
- Menu `View` existe avec un submenu `Panels` vide.
- **À faire** : Ajouter des `JCheckBoxMenuItem` pour afficher/masquer les panneaux.
- **À faire** : Implémenter un menu de sélection de thème.

---

### 🚀 TODO 3 : Aide et Documentation ✅

| ID   | Titre              | Description (Carte)                                                                                      | Complexité | Importance | Status |
|------|--------------------|--------------------------------------------------------------------------------------------------------|-----------|-----------|--------|
| #301 | Menu d'aide        | En tant qu'utilisateur, je veux accéder à un manuel ou une aide rapide afin de comprendre les fonctions. | 1/5        | 3/5        | ✅ FAIT |
| #302 | À propos (About)   | En tant qu'utilisateur, je veux voir la version du logiciel et les crédits afin de savoir si je suis à jour. | 1/5        | 2/5        | ✅ FAIT |

**✅ Confirmation (Critères d'acceptation)**
- Menu `Help > Documentation` ouvre le fichier `docs/DOCUMENTATION.md` avec l'application par défaut.
- Menu `Help > About` affiche une fenêtre avec version 1.0.0 et crédits des contributeurs.

---

- **Estimation totale** : ~6 heures ✅ PARTIELLEMENT RÉALISÉES
- **Architecture** : Classe `TopBar extends JMenuBar` avec actions liées au controller.

---

### Récapitulatif des TODOs

#### TODO 1 : Gestion des fichiers et persistance ✅
- Menu "File > Open" avec file chooser natif.
- Chargement depuis URL.
- Support Parquet.

#### TODO 2 : Contrôle de l'espace de travail 🟠
- **À faire** : Menu "View > Panels" avec toggle des composants.
- **À faire** : Menu "View > Themes" avec sélection de thème.

#### TODO 3 : Aide et documentation ✅
- Documentation accessible via le menu Help.
- Fenêtre About avec version et crédits.