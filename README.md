# Precensia - Gestion de Présence Scolaire

Application Android native développée en Kotlin pour la gestion quotidienne des présences dans une école.

## Fonctionnalités

- **Système de rôles** : Professeur, Délégué et Élève.
- **Gestion quotidienne** : La liste de présence est renouvelée chaque jour.
- **Flux de travail** :
  - Le **Délégué** remplit la fiche de présence (Présent, Absent, Retard) et l'envoie au professeur.
  - Le **Professeur** vérifie la fiche, peut apporter des corrections si nécessaire, et valide l'enregistrement final en base de données.
  - L'**Élève** peut consulter la fiche de présence du jour actuel en mode lecture seule.
- **Base de données** : Intégration avec Firebase Firestore pour la synchronisation en temps réel.

## Architecture

- Langage : Kotlin
- UI : XML avec ViewBinding
- Backend : Firebase Auth & Firestore
- Architecture : Model-View-Activity (Pattern standard Android)

## Configuration

Pour faire fonctionner l'application, vous devez ajouter votre fichier `google-services.json` dans le dossier `app/`.
