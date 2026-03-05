## Partie 2 – Backend (Java)

J'ai implémenté la solution en Java, organisée en **3 fichiers séparés** :

- `Task.java` — la classe métier avec ses attributs et sa validation
- `TaskService.java` — la logique de filtrage et tri
- `Partie2Backend.java` — le point d'entrée avec les exemples d'utilisation

**Fonction de filtrage et tri** (`getBlockedTasksSorted`) : j'utilise l'API Stream de Java pour filtrer les tâches bloquées, puis les trier par priorité croissante et, à égalité, par ordre alphabétique.

**Méthode `setBlocked` avec validation** : si `isBlocked` est `true` et que `blockReason` est nulle ou vide, une `IllegalArgumentException` est levée. Ce choix est intentionnel — c'est une erreur d'usage, pas un cas métier normal. Quand on débloque une tâche, la raison est automatiquement remise à `null`.

---