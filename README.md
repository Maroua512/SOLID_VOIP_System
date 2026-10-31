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
        -Notification notification
        -SauvegardeDonnees sauvegardeDonnees
        +evaluer(List~Metriques~ metriquesList) void
        -ajouterValeur(Map evaluations, String nomServeur, TypeIndicateur indicateur, double valeur) void
        -determinerAlerte(String nomServeur, TypeIndicateur indicateur, double valeurMoyenne) TypeAlerte
        -envoyerAlerte(String message, boolean viaConsole) void
    }

    class EvaluationMetrique {
        <<record>>
        +String nomServeur
        +TypeIndicateur indicateur
        +double valeur
        +int nombreValeursCollectees
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
        +envoyerNotificationParEmail(String message, String destinataire) void
        +envoyerNotificationViaConsole(String message, String destinataire) void
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
    Evaluation *-- Notification : possède
    Evaluation *-- SauvegardeDonnees : possède
    Evaluation ..> Metriques : évalue
    Evaluation ..> EvaluationMetrique : agrège
    Evaluation ..> ResultatEvaluation : crée
    Evaluation ..> TypeIndicateur : utilise
    Evaluation ..> TypeAlerte : détermine
    SauvegardeDonnees o-- ResultatEvaluation : stocke
    EvaluationMetrique --> TypeIndicateur
    ResultatEvaluation --> TypeIndicateur
    ResultatEvaluation --> TypeAlerte
```

Le diagramme représente les dépendances entre les classes et les deux énumérations.
Les maps utilisées temporairement par `evaluer` sont des variables locales et ne
sont donc pas des attributs de `Evaluation`.
