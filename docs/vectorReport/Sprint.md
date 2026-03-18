# FILTER PANEL

## User Stories – Interface de gestion des filtres (Java Swing)

📋 **Backlog Produit - Interface de Gestion des Filtres (Java Swing)**

Ce document contient les User Stories rédigées selon le format du cours (Carte, Conversation, Confirmation).

---

### 🏗️ TODO 1 : Affichage et Sélection de Filtres

| ID   | Titre              | Description (Carte)                                                                 | Complexité | Importance |
|------|--------------------|-----------------------------------------------------------------------------------|------------|------------|
| #101 | Liste des filtres  | En tant qu'utilisateur, je veux voir une liste de boutons de filtres disponibles afin d'identifier les options d'affichage. | 1/5        | 5/5        |
| #102 | Toggle de filtre   | En tant qu'utilisateur, je veux activer ou désactiver un filtre via une case à cocher afin de personnaliser mon graphique. | 2/5        | 5/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #101 : Au chargement, le panneau latéral affiche les libellés issus du fichier source.
- Pour #102 : Le clic sur la checkbox déclenche immédiatement un événement de filtrage sur la collection de données.

---

### ⚙️ TODO 2 : Gestion Avancée des Filtres

| ID   | Titre         | Description (Carte)                                                                                       | Complexité | Importance |
|------|---------------|---------------------------------------------------------------------------------------------------------|------------|------------|
| #201 | Tri par Header| En tant qu'utilisateur, je veux configurer le tri (croissant/décroissant) basé sur le header du SVG pour organiser les données. | 3/5        | 4/5        |
| #202 | Reset Global  | En tant qu'utilisateur, je veux réinitialiser tous les filtres en un clic afin de revenir rapidement à la vue initiale. | 1/5        | 3/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #201 : Le programme utilise des métadonnées pour proposer les colonnes de tri disponibles.
- Pour #202 : Le bouton "Reset" remet toutes les JCheckBox à l'état false et vide les filtres actifs.

---

### 🚀 TODO 3 : Expérience Utilisateur et Robustesse

| ID   | Titre           | Description (Carte)                                                                                      | Complexité | Importance |
|------|-----------------|--------------------------------------------------------------------------------------------------------|------------|------------|
| #301 | Filtres avancés | En tant qu'utilisateur, je veux filtrer par plage (ex: prix) ou par type (ex: ville) afin d'isoler des données précises. | 4/5        | 5/5        |
| #302 | Alerte "No Result" | En tant qu'utilisateur, je veux être averti si aucun résultat n'est trouvé afin d'ajuster mes critères. | 2/5        | 3/5        |
| #303 | Auto-update     | En tant qu'utilisateur, je veux que le graphique s'actualise automatiquement à chaque clic pour une interface réactive. | 5/5        | 4/5        |

**✅ Confirmation (Critères d'acceptation)**
- Pour #301 : L'interface propose des champs JTextField pour les valeurs min/max et des groupes de boutons pour les catégories.
- Pour #302 : Si la liste filtrée est vide, un label rouge "Aucune donnée correspondante" apparaît sur le panel.
- Pour #303 : Utilisation d'un pattern Observer/Observable pour notifier le composant graphique dès qu'un filtre change.

---

- **Estimation totale** : ~15 heures de développement.
- **Architecture** : Séparation claire entre la logique de filtrage (Controleur) et l'affichage (Vue).
---

### Récapitulatif des TODOs

#### TODO 1 : Affichage et sélection de filtres
- Pouvoir voir une liste de boutons disponibles.
- Pouvoir activer ou désactiver un filtre via une case à cocher ou un bouton pour personnaliser l’affichage du graphique.

#### TODO 2 : Gestion avancée des filtres
- Récupérer le header du SVG.
- Pouvoir configurer les paramètres d’un filtre en fonction du header (croissant/décroissant)
- Pouvoir réinitialiser tous les filtres d’un simple clic.

#### TODO 3 : Expérience utilisateur et robustesse
- Filtre en fonction du header (par exemple prix entre 10 et 15 €)
- Filtrage en fonction du type de donnée reçu (par exemple regrouper toutes les villes entre elles)
- Être averti si une combinaison de filtres ne retourne aucun résultat.
- Voir le graphique se mettre à jour automatiquement lorsque je modifie les filtres.
- Interface intuitive et réactive, même avec de nombreux filtres.

