# Android Meteo App

## Description
L'application Android Meteo App est une application de prévisions météorologiques moderne et intuitive, développée en Kotlin avec Jetpack Compose. Elle permet aux utilisateurs de rechercher les prévisions météo pour différentes villes, d'ajouter des villes à leurs favoris, et d'obtenir la météo de leur position actuelle. L'application est conçue pour être robuste, avec une gestion efficace du cache pour un fonctionnement hors ligne et une gestion complète des erreurs.

## Fonctionnalités

*   **Écran d'accueil intuitif** : Affiche une barre de recherche et un résumé météorologique des villes ajoutées en favoris.
*   **Recherche de ville avancée** : Permet de rechercher des villes par nom en utilisant l'API de géocodage `open-meteo.com`.
*   **Météo par géolocalisation** : Option pour afficher les prévisions météo de la position actuelle de l'utilisateur, avec gestion des permissions et des erreurs (GPS désactivé).
*   **Écran de détails complet** : Affiche les prévisions météorologiques détaillées pour une ville sélectionnée, incluant :
    *   Température actuelle
    *   Conditions météorologiques (ensoleillé, nuageux, pluie)
    *   Températures minimale et maximale
    *   Vitesse du vent
    *   Prévisions horaires
*   **Gestion des favoris** : L'utilisateur peut facilement ajouter et supprimer des villes de sa liste de favoris, qui sont persistées localement.
*   **Mode hors connexion** : Mise en cache des résultats de requêtes et des favoris, permettant à l'application de fonctionner sans connexion internet.
*   **Gestion robuste des erreurs** : Interception et affichage de messages clairs pour les erreurs API, réseau et de permissions.
*   **Adaptation à la rotation de l'écran** : L'interface utilisateur s'adapte fluidement aux changements d'orientation de l'appareil.

## Technologies Utilisées

*   **Langage** : Kotlin
*   **UI Toolkit** : Jetpack Compose
*   **Architecture** : MVVM (Model-View-ViewModel)
*   **Injection de Dépendances** : Hilt (Dagger 2)
*   **Networking** : Retrofit avec Kotlinx Serialization Converter
*   **JSON Parsing** : Kotlinx Serialization
*   **Persistance des données** : Room Database
*   **Asynchronisme** : Kotlin Coroutines & Flow
*   **Tests** : JUnit 4, MockK, Kotlinx Coroutines Test, AndroidX Arch Core Testing
*   **API Météo** : [Open-Meteo.com](https://open-meteo.com/) (avec le modèle `meteofrance_seamless`)
*   **Géolocalisation** : Google Play Services Location

## Architecture

L'application suit une architecture MVVM propre et modulaire, divisée en trois couches principales :

*   **`data`** : Contient les sources de données (API, base de données Room), les DTOs (Data Transfer Objects) et les implémentations des repositories.
*   **`domain`** : Définit les modèles de données métier (City, WeatherInfo), les interfaces des repositories et les cas d'utilisation (use cases) pour la logique métier.
*   **`ui`** : Comprend les Composables (vues), les ViewModels et la logique de navigation.

Hilt est utilisé pour injecter les dépendances à travers ces couches, assurant une grande testabilité et maintenabilité.

## Comment exécuter le projet

1.  **Cloner le dépôt Git** :
    ```bash
    git clone [URL_DE_VOTRE_DEPOT]
    cd AndroidmeteoApp
    ```
2.  **Ouvrir dans Android Studio** :
    Ouvrez le projet dans Android Studio (version Flamingo ou supérieure recommandée).
3.  **Synchroniser Gradle** :
    Laissez Gradle synchroniser les dépendances. Si des problèmes surviennent, essayez de reconstruire le projet (`Build > Rebuild Project`).
4.  **Lancer l'application** :
    Sélectionnez un émulateur Android ou connectez un appareil physique et cliquez sur le bouton `Run 'app'` (▶️).

## Tests

Des tests unitaires ont été implémentés pour le `HomeViewModel` afin de garantir la fiabilité des fonctionnalités de recherche et de gestion de la localisation. Pour exécuter les tests :

1.  Dans Android Studio, naviguez vers la fenêtre `Build Variants` et assurez-vous que `Unit Tests` est sélectionné.
2.  Faites un clic droit sur le dossier `app/src/test/java` et sélectionnez `Run 'Tests in 'java''`.
    Alternativement, vous pouvez exécuter la commande Gradle :
    ```bash
    ./gradlew test
    ```

## Auteur
[Nicolas Robles]
