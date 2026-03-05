# Partie 1 – Analyse fonctionnelle
## Application de calcul automatisé pour la broderie

---

## 1. Approche globale

### Decoupage du processus de traitement de l'image

```
[L'utilisateur fournis l'image]
         │
         ▼
┌─────────────────────┐
│  1. Import & valida │  ← Vérification format, résolution, taille
│     tion de l'image │
└─────────────────────┘
         │
         ▼
┌─────────────────────┐
│  2. Prétraitement   │  ← Réduction du bruit, normalisation des couleurs
│     de l'image      │     redimensionnement si nécessaire
└─────────────────────┘
         │
         ▼
┌─────────────────────┐
│  3. Analyse des     │  ← Détection et regroupement des couleurs
│     couleurs        │     
└─────────────────────┘
         │
         ▼
┌─────────────────────┐
│  4. Segmentation    │  ← Identification des zones par couleur
│     des zones       │     
└─────────────────────┘
         │
         ▼
┌─────────────────────┐
│  5. Calcul des      │  ← Comptage des pixels → conversion en surface réelle
│     surfaces        │     (application de l'unité choisie : mm2 ou cm2)
└─────────────────────┘
         │
         ▼
┌─────────────────────┐
│  6. Estimation      │  ← Calcul du fil (surface × densité de points)
│     fil & coût      │     Estimation du temps et du coût par couleur
└─────────────────────┘
         │
         ▼
┌─────────────────────┐
│  7. Restitution     │  ← Rapport visuel : image annotée + tableau récapitulatif des coûts et des travaux
│     à l'utilisateur │     
└─────────────────────┘
```

### Description des étapes

**Étape 1 – Import & validation**
L'utilisateur soumet une image (JPG, PNG, SVG…). Le système valide le format, la résolution minimale et le poids du fichier avant de procéder et stocker l'image pour le traiter. Mais si une condition n'est pas satisfaite, le système affiche un message d'erreur et demande à l'utilisateur de fournir une image valide.

Exemple de code de verification :
```javascript
function uploadImage(file) {
  if (!file) throw new Error("Aucune image fournie");
  if (file.size > MAX_SIZE) throw new Error("Image trop grande");

  const imageId = saveFile(file);
  return imageId;
}
```

**Étape 2 – Prétraitement**
Avant l'analyse, on normalise l'image : réduction du bruit (flou léger), normalisation des valeurs de couleur et redimensionnement si l'image est trop grande pour le traitement. Le but est de simplifier l'image.
Outil possible : OpenCV ( open source ).

**Étape 3 – Analyse des couleurs**
On identifie les couleurs dominantes de l'image. Deux approches possibles :

- **Quantisation de couleur** : réduit le nombre de couleurs à N couleurs représentatives.
- **Clustering k-means** sur l'espace RGB ou Lab pour regrouper les pixels par famille de couleur.
Avant de regrouper, on convertit les couleurs RGB en espace CIE Lab, car deux couleurs très proches en RGB ne semblent pas toujours proches à l'oeil humain.
Le but est que l'application utilise par exemple juste une dixaine de couleurs dominantes au lieu de 10.000 par exemple. Dans le but de reduire les couts et les depenses des deux cotes. 

**Étape 4 – Segmentation des zones**
Pour chaque couleur identifiée, on délimite les zones de pixels correspondantes. Chaque zone devient une surface à broder.

**Étape 5 – Calcul des surfaces**
Il y a deux méthodes : une plus simple et une qui est plus avancée mais plus précise.

1. Simple :

>`surface = nombre_pixels * surface_pixels`
Si on suppose que 1 pixel = 0,1mm² et qu'on a 5000 pixels rouges à broder.
Exemple : 
5000 pixels * 0.1mm² = 500mm² de rouger à broder
Cette méthode est simple mais approximative, car elle suppose que :
    - tous les pixels ont une taille connue
    - l'image a toujours la même échelle

Elle peut fonctionner si :
    - toutes les images viennent d’une même machine
    - la taille de l’image est standard

1. Avancée et recommandée :

Le nombre de pixels par couleur est converti en surface réelle grâce au DPI (dots per inch) de l'image ou à une échelle fournie par l'utilisateur.
Exemple :
72 DPI = 72 pixels par pouce

> Formule : `surface_cm² = (nb_pixels / DPI²) × 6.4516`  
> (1 pouce = 2.54 cm, donc 1 pouce² = 6.4516 cm²)

Exemple : 
Si l'image est en 300 DPI et qu'on a par exemple 80000 pixels de jaune
surface_cm² = (80000 / 300²) * 6.4516 = 5.73cm² de jaune à broder
Et cela se répétera jusqu'à ce que toutes les surfaces des couleurs déterminées seront calculées.

**Étape 6 – Estimation du fil et du coût**
À partir de la surface et de la densité de points (points/cm²), on calcule la longueur de fil nécessaire par couleur. Avec un prix au mètre, on obtient le coût estimé.

- estimation de la longueur du fil :
    >`longueur_fil = surface * densite_points`
    Ici densité point est une paramètre à déterminer par l'utilisateur ou par la configuration.

- estimation du temps :
    >`temps = nombre_points / vitesse_machine`

- estimation du coût :
    >`coût = longueur_fil × prix_fil`

**Étape 7 – Restitution**
L'utilisateur reçoit un rapport clair : image annotée avec les zones colorées, la liste des couleurs, surface par couleurs, estimation du fil, temps de broderie. Interface possible dans un tableau ou un graphique que le client pourra exporter.

---

## 2. Modèle de données

### Entités principales et leurs champs

---

#### Entité : `User` (Utilisateur)

| Champ | Type | Rôle |
|---|---|---|
| `id` | UUID | id unique de l'utilisateur |
| `name` | String | Nom utilisateur |
| `email` | String | Email utilisateur |
| `password` | String | Mot de passe utilisateur |
| `created_at` | DateTime | Date de création du compte |

---

#### Entité : `Commande` (Projet de broderie)

| Champ | Type | Rôle |
|---|---|---|
| `id` | UUID | id unique du projet |
| `name` | String | Nom donné au projet par l'utilisateur |
| `user_id` | UUID (FK) | Référence user.id |
| `created_at` | DateTime | Date de création | ( de base dateNow() )
| `image_id` | UUID (FK) | Référence vers l'image.id |
| `config_id` | UUID (FK) | Référence vers la configuration.id |
| `status` | Enum | `pending`, `processing`, `done`, `error` |
 
**Règles métier :**
- Un projet est lié à exactement un utilisateur, une image et une configuration.
- Le statut `done` n'est atteint que si toutes les couleurs ont une surface calculée.

---

#### Entité : `Image` (Image importée)

| Champ | Type | Rôle |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `filename` | String | Nom du fichier original |
| `file_path` | String | Chemin de stockage sur le serveur |
| `format` | Enum | `PNG`, `JPG`, `SVG`, `BMP` |
| `width_px` | Integer | Largeur en pixels |
| `height_px` | Integer | Hauteur en pixels |
| `dpi` | Integer | Résolution (dots per inch)|
| `uploaded_at` | DateTime | Horodatage d'importation |

**Règles métier :**
- Le format doit être parmi les formats supportés (PNG, JPG recommandés).
- `width_px` et `height_px` doivent être > 0.

---

#### Entité : `DetectedColor` (Couleur détectée)

| Champ | Type | Rôle |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `project_id` | UUID (FK) | Projet parent |
| `rgb_value` | String | Code couleur RGB ex. `(34, 139, 34)` |
| `hex_code` | String | Code hexadécimal ex. `#FFFFFF` |
| `lab_value` | String | Valeur CIE Lab |
| `label` | String | Nom lisible ex. "Vert forêt" (optionnel) |
| `thread_ref` | String | Référence fil correspondante |
| `pixel_count` | Integer | Nombre de pixels de cette couleur |

**Règles métier :**
- `pixel_count` doit être > 0 (une couleur sans pixel n'existe pas).
- Deux couleurs très proches (distance ΔE < seuil) peuvent être fusionnées.
- NB : Le matching se fait au moment du calcul : on compare le lab_value de DetectedColor avec tous les lab_value de ThreadReference et on prend celui avec le ΔE le plus faible.

---
 
#### Entité : `ColorSurface` (Surface associée à une couleur)

| Champ | Type | Rôle |
|---|---|---|
| `id` | UUID | Identifiant unique |
| `color_id` | UUID (FK) | DetectedColor.id |
| `project_id` | UUID (FK) | Projet parent |
| `area_px` | Integer | Surface en pixels |
| `area_cm2` | Float | Surface convertie en cm² |
| `thread_length_m` | Float | Longueur de fil estimée (mètres) |
| `estimated_cost` | Float | Coût estimé (selon prix au mètre) |
| `zone_count` | Integer | Nombre de zones distinctes pour cette couleur |

**Règles métier :**
- `area_cm2` doit être > 0.
- `thread_length_m` = `area_cm2` × `densite_points` (de la config) × `longueur_fil_par_point`.`/100`
- `estimated_cost` ne peut être calculé que si le prix du fil est renseigné. 

---

#### Entité : `Configuration` (Configuration)

| Champ | Type | Rôle |
|---|---|---| 
| `id` | UUID | Identifiant unique |
| `thread_type` | String | Type de fil ex. "coton", "soie", "polyester" |
| `stitch_density` | Float | Densité de points (points/cm²) |
| `thread_price_per_m` | Float | Prix du fil par mètre |
| `thread_per_stitch` | Float | Longueur de fil par point (cm) |

**Règles métier :**
- `stitch_density` doit être > 0.
- `thread_price_per_m` est optionnel ; si absent, le coût ne sera pas calculé.

---

#### Entité : `Thread` (Configuration)

| Champ | Type | Rôle |
|---|---|---| 
| `id` | UUID | Identifiant unique |
| `brand` | String | Marque |
| `reference` | String | ex : DMC 69 |
| `color_hex` | String | Couleur du fil en hex #RRGGBB |
| `color_lab` | String | Valeur CIE Lab du fil (pour matching ΔE) |
| `color_name` | String | Nom de la couleur |
| `thread_price_per_m` | Float | Prix du fil par mètre |
 
**Règles métier :**
- `thread_price_per_m` doit être > 0.
- `thread_price_per_m` : si absent, le coût ne sera pas calculé.

---

### Schéma des relations

```
User
 │
 └── 1:N ── Commande
               │
               ├── 1:1 ── Image
               │
               ├── 1:1 ── Configuration
               │
               └── 1:N ── DetectedColor
                               │
                               └── 1:1 ── ColorSurface
                                              │
                                              └── N:1 ── ThreadReference
```
> Un utilisateur peut faire plusieurs commandes.
> Une image appartient à un projet.  
> Une commande peut avoir plusieurs couleurs détectées.  
> Chaque couleur détectée possède exactement une surface calculée.

---

## 3. Obstacles potentiels et risques

### Risque 1 – Qualité ou résolution insuffisante de l'image

**Problème :** Une image floue, compressée (artefacts JPEG) ou de faible résolution fausse la détection des couleurs et la précision des surfaces.

**Pistes de résolution :**
- Définir une résolution minimale requise (ex. 150 DPI minimum) et afficher une alerte si elle est inférieure.
- Informer clairement l'utilisateur que les résultats seront approximatifs si la qualité est faible.

---

### Risque 2 – Couleurs très proches ou dégradés

**Problème :** Une image naturelle (comme des feuilles) contient des dizaines de nuances de vert très proches. L'algorithme peut créer trop de couleurs distinctes, rendant le résultat inexploitable.

**Pistes de résolution :**
- Utiliser un **espace colorimétrique perceptuel** (CIE Lab) plutôt que RGB pour mieux regrouper les couleurs proches visuellement.
- Appliquer un **clustering k-means** avec un nombre k paramétrable par l'utilisateur (ex. "Je veux 8 couleurs maximum").
- Proposer un **seuil de fusion** : deux couleurs dont la distance ΔE < 10 sont automatiquement fusionnées.

---

### Risque 3 – Performances sur des images grandes

**Problème :** Une image haute résolution (ex. 4000×3000 px) peut contenir 12 millions de pixels. L'analyse pixel par pixel peut être lente.

**Pistes de résolution :**
- **Redimensionner** l'image en entrée à une résolution de travail (ex. max 1500px de large) puis remonter le ratio pour la surface finale.
- Utiliser des bibliothèques optimisées comme **NumPy / scikit-learn** (Python) ou **OpenCV** qui opèrent sur des tableaux vectorisés, beaucoup plus rapides que des boucles pixel par pixel.

---

### Risque 4 – Précision du calcul des surfaces

**Problème :** Si le DPI de l'image est inconnu ou incorrect, la conversion pixels → cm² sera fausse, entraînant de mauvaises estimations de fil.

**Pistes de résolution :**
- Permettre à l'utilisateur de **saisir manuellement les dimensions réelles** du motif (ex. "ce motif mesure 20cm × 15cm"), ce qui permet de recalculer le DPI effectif.
- Lire les métadonnées EXIF de l'image pour extraire le DPI automatiquement.
- Afficher une **marge d'erreur estimée** dans les résultats (ex. ±5%) pour que l'utilisateur soit conscient de l'approximation.

---

### Risque 5 – Correspondance couleur ↔ fil de broderie

**Problème :** La couleur détectée en RGB dans l'image peut ne pas correspondre exactement à une référence de fil commerciale (ex. DMC, Anchor).

**Pistes de résolution :**
- Maintenir une **base de données de correspondances** couleur RGB ↔ référence fil (ex. table DMC avec 500+ couleurs).
- Utiliser la **distance colorimétrique ΔE** pour trouver la référence la plus proche.
- Laisser l'utilisateur **valider ou modifier** manuellement la correspondance proposée avant de lancer le calcul final.

---
