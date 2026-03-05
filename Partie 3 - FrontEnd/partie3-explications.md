## Partie 3 – Frontend (HTML / CSS / JS)

**Affichage** : le tableau est généré dynamiquement via JavaScript à partir des données JSON.

**Filtres combinés** :
- Un champ texte pour rechercher par nom de tâche et nom de projet
- Un `<select>` généré automatiquement à partir des projets présents dans les données
- Les deux filtres s'appliquent simultanément en temps réel

**Recherche insensible aux accents** : j'ai utilisé `normalize("NFD")` pour décomposer les caractères accentués, permettant par exemple de taper `e` et de trouver `é`.