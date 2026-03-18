# Backlog Produit : VectorReport

---

## Gestion des Données

### US 01 : Importation de fichier CSV
**En tant qu'** analyste de données,  
**je veux** pouvoir charger un fichier CSV depuis mon ordinateur,  
**afin de** visualiser les données brutes dans le tableau de l'application.  
* **Critères d'acceptation :**
    * Le bouton "Load a table" ouvre l'explorateur de fichiers.
    * Les données s'affichent correctement dans la grille inférieure.
    * Le nombre de lignes et de colonnes est mis à jour dans la barre d'état.

### US 02 : Filtrage dynamique
**En tant qu'** utilisateur,  
**je veux** pouvoir appliquer des filtres logiques (égal, contient, supérieur à...),  
**afin d'** isoler uniquement les données qui m'intéressent pour mon rapport.  
* **Critères d'acceptation :**
    * Accès via le bouton "Filters".
    * Possibilité de combiner des conditions avec AND/OR.
    * Le tableau se met à jour instantanément après avoir cliqué sur "Apply".

---

## Visualisation Graphique

### US 03 : Création de graphique
**En tant que** responsable d'équipe,  
**je veux** générer un graphique de type "Pie","Bar","Diagramme de Bar",  
**afin de** voir la répartition proportionnelle de mes données (ex: effectifs par métier).  
* **Critères d'acceptation :**
    * Sélection du type dans le panneau latéral.
    * Possibilité de donner un titre personnalisé au graphique.
    * Affichage d'une légende claire si l'option est cochée.

### US 04 : Personnalisation esthétique
**En tant qu'** utilisateur soucieux du design,  
**je veux** pouvoir modifier les couleurs, l'opacité et la police du graphique,  
**afin de** l'adapter à la charte graphique de mon entreprise.  
* **Critères d'acceptation :**
    * Accès aux sélecteurs de couleur (Fill/Stroke).
    * Modification de la taille de la police et de l'épaisseur des traits.
    * Prévisualisation en temps réel dans la zone centrale.

---

## Expérience Utilisateur (UX)

### US 05 : Gestion de l'espace de travail
**En tant qu'** utilisateur avec un petit écran,  
**je veux** pouvoir masquer ou détacher les panneaux (Tableau, Paramètres),  
**afin de** maximiser la zone de visualisation du graphique.  
* **Critères d'acceptation :**
    * Menu "View > Panels" fonctionnel.
    * Les panneaux se détachent dans des fenêtres indépendantes.

### US 06 : Navigation dans les grands jeux de données
**En tant qu'** utilisateur manipulant des fichiers volumineux,  
**je veux** pouvoir naviguer par pages dans le tableau,  
**afin de** ne pas ralentir l'application et garder une lecture fluide.  
* **Critères d'acceptation :**
    * Boutons First/Previous/Next/Last fonctionnels.
    * Indication du numéro de page actuelle (ex: Page 1/1).

# DOCUMENTATION & QUALITÉ (FINALISATION)

---

## 🛠️ Optimisation du Code Source

### US 07 : Optimisation et mise au propre du code
**En tant que** développeur,  
**je veux** nettoyer le code source et supprimer les traces de debug,  
**afin de** garantir une base de code saine et maintenable pour le futur.  
* **Critères d'acceptation :**
    * Suppression des `System.out.println` et des blocs de commentaires de test.
    * Ajout de la JavaDoc sur les méthodes clés de calcul (`ReportService`).
    * Respect des conventions de nommage Java (CamelCase) sur l'ensemble des classes et variables.

---

## 📖 Documentation Utilisateur

### US 08 : Création du manuel d'utilisation PDF
**En tant qu'** utilisateur final,  
**je veux** disposer d'un guide complet au format PDF avec un sommaire interactif,  
**afin de** prendre en main l'application de manière autonome et rapide.  
* **Critères d'acceptation :**
    * Sommaire cliquable fonctionnel vers les différentes sections du document.
    * Présence de captures d'écran annotées pour chaque panneau (Filter, Table, Chart).
    * Mise en page professionnelle incluant un fil d'ariane pour faciliter la navigation.

---

## 📈 Gestion de Projet (Agile)

### US 09 : Structuration du Backlog et des Sprints
**En tant que** responsable de projet,  
**je veux** mettre en forme l'ensemble des User Stories et des Sprints en Markdown,  
**afin de** présenter un dossier de gestion de projet professionnel et parfaitement lisible.  
* **Critères d'acceptation :**
    * Toutes les US sont regroupées par catégories logiques (UI, Data, Finalisation).
    * Utilisation rigoureuse de la syntaxe Markdown (tableaux, listes à puces, mise en gras).
    * Cohérence visuelle entre les tâches décrites et les fonctionnalités finales de l'application.

---