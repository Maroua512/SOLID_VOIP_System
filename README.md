# SOLID VOIP System

Projet Java Maven de base pour le système VOIP.

## Prérequis

- Java 17 ou version supérieure
- Maven 3.9 ou version supérieure

## Commandes

Compiler et exécuter les tests :

```powershell
mvn test
```

Lancer l'application :

```powershell
mvn exec:java
```

## Diagramme UML

```mermaid
classDiagram
    direction LR

    class App {
        +main(String[] args)
    }

    class Evaluation {
        -SauvegardeDonnees sauvegardeDonnees
        ~DeterminerAlerte determinerAlerte
        ~EnvoyerAlerte envoyerAlerte
        +evaluer(List~Metriques~ metriquesList) void
        -ajouterValeur(Map evaluations, String nomServeur, TypeIndicateur indicateur, double valeur) void
    }

    class EvaluationMetrique {
        <<record>>
        +nomServeur : String
        +indicateur : TypeIndicateur
        +valeur : double
        +nombreValeursCollectees : int
    }

    class Metriques {
        -String nom_serveur
        -double latence
        -double jitter
        -double perte_paquets
        -double mos
        -Timestamp time
        +getNomServeur() String
        +getLatence() double
        +getJitter() double
        +getPertePaquets() double
        +getMos() double
        +getTime() Timestamp
    }

    class ResultatEvaluation {
        -String nom_serveur
        -TypeIndicateur type_indicateur
        -double valeur_moyenne
        -LocalDateTime date_debut
        -LocalDateTime date_fin
        -TypeAlerte type_alerte
    }

    class SauvegardeDonnees {
        -List~ResultatEvaluation~ resultats
        +sauvegarderResultat(ResultatEvaluation resultat) void
        +getResultats() List~ResultatEvaluation~
    }

    class Notification {
        <<interface>>
        +envoyerNotification(String message, String destinataire) void
    }

    class NotificationParEmail {
        +envoyerNotification(String message, String destinataire) void
    }

    class NotificationViaConsole {
        +envoyerNotification(String message, String destinataire) void
    }

    class DeterminerAlerte {
        ~List~RegleSeuil~ regles
        +determinerAlerte(String nomServeur, TypeIndicateur indicateur, double valeurMoyenne) TypeAlerte
    }

    class RegleSeuil {
        ~Sens sens
        ~TypeIndicateur indicateur
        ~double SeuilCritique
        ~double SeuilAvertissement
        +RegleSeuil(Sens sens, TypeIndicateur indicateur, double seuilCritique, double seuilAvertissement)
    }

    class Sens {
        <<enumeration>>
        SUPERIEUR
        INFERIEUR
    }

    class EnvoyerAlerte {
        +envoyerAlerte(Notification notification, String message) void
    }

    class Serveur {
        -String nom_serveur
        +getNomServeur() String
    }

    class TypeIndicateur {
        <<enumeration>>
        Latence
        Jitter
        PertePaquets
        Mos
    }

    class TypeAlerte {
        <<enumeration>>
        CRITIQUE
        AVERTISSEMENT
        NORMAL
    }

    App ..> Evaluation : utilise
    App ..> Metriques : crée
    Evaluation *-- SauvegardeDonnees : possède
    Evaluation *-- DeterminerAlerte : possède
    Evaluation *-- EnvoyerAlerte : possède
    Evaluation ..> Metriques : évalue
    Evaluation ..> EvaluationMetrique : agrège
    Evaluation ..> ResultatEvaluation : crée
    DeterminerAlerte o-- RegleSeuil : contient
    DeterminerAlerte ..> TypeIndicateur : utilise
    DeterminerAlerte ..> TypeAlerte : retourne
    RegleSeuil --> Sens
    RegleSeuil --> TypeIndicateur
    EnvoyerAlerte ..> Notification : utilise
    NotificationParEmail ..|> Notification
    NotificationViaConsole ..|> Notification
    SauvegardeDonnees o-- ResultatEvaluation : stocke
    EvaluationMetrique --> TypeIndicateur
    ResultatEvaluation --> TypeIndicateur
    ResultatEvaluation --> TypeAlerte
```

Le diagramme représente la conception actuelle :

- `Evaluation` utilise `DeterminerAlerte` pour appliquer les règles d’alerte ;
- `DeterminerAlerte` conserve une liste de règles `RegleSeuil` ;
- `RegleSeuil` définit le sens de comparaison et les seuils critique et avertissement ;
- `EnvoyerAlerte` dépend de l’interface `Notification` ;
- `NotificationParEmail` et `NotificationViaConsole` implémentent cette interface ;
- `SauvegardeDonnees` conserve les résultats d’évaluation ;
- les maps utilisées temporairement par `evaluer` sont des variables locales.
