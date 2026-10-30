package com.maroua.voip.Traitement;

import com.maroua.voip.model.Metriques;
import com.maroua.voip.notifications.Notification;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.maroua.voip.model.Serveur;
import com.maroua.voip.model.ResultatEvaluation;
import com.maroua.voip.model.TypeIndicateur;
import com.maroua.voip.Persistance.SauvegardeDonnees;
import com.maroua.voip.model.TypeAlerte;
public class Evaluation {

    public record EvaluationMetrique(
            String nomServeur,
            TypeIndicateur indicateur,
            double valeur,
            int nombreValeursCollectees) {
    }

    Notification notification = new Notification();
    ResultatEvaluation resultatEvaluation;
    List<EvaluationMetrique> evaluations = new ArrayList<>();
    TypeAlerte typeAlerte = TypeAlerte.NORMAL;
    SauvegardeDonnees sauvegardeDonnees = new SauvegardeDonnees();

    public Map<String, Map<TypeIndicateur, Double>> calculerMoyennesParServeur(
            List<Metriques> metriquesList) {
        Map<String, Map<TypeIndicateur, double[]>> sommes = new LinkedHashMap<>();

        for (Metriques metrique : metriquesList) {
            Map<TypeIndicateur, double[]> sommesParIndicateur = sommes.computeIfAbsent(
                    metrique.getNomServeur(), nom -> new EnumMap<>(TypeIndicateur.class));

            ajouterSomme(sommesParIndicateur, TypeIndicateur.Latence, metrique.getLatence());
            ajouterSomme(sommesParIndicateur, TypeIndicateur.Jitter, metrique.getJitter());
            ajouterSomme(sommesParIndicateur, TypeIndicateur.PertePaquets, metrique.getPertePaquets());
            ajouterSomme(sommesParIndicateur, TypeIndicateur.Mos, metrique.getMos());
        }

        Map<String, Map<TypeIndicateur, Double>> moyennesParServeur = new LinkedHashMap<>();
        for (Map.Entry<String, Map<TypeIndicateur, double[]>> serveur : sommes.entrySet()) {
            Map<TypeIndicateur, Double> moyennes = new EnumMap<>(TypeIndicateur.class);
            for (Map.Entry<TypeIndicateur, double[]> indicateur : serveur.getValue().entrySet()) {
                double[] sommeEtNombre = indicateur.getValue();
                moyennes.put(indicateur.getKey(), sommeEtNombre[0] / sommeEtNombre[1]);
            }
            moyennesParServeur.put(serveur.getKey(), moyennes);
        }
        return moyennesParServeur;
    }

    private void ajouterSomme(
            Map<TypeIndicateur, double[]> sommes,
            TypeIndicateur indicateur,
            double valeur) {
        double[] sommeEtNombre = sommes.computeIfAbsent(indicateur, cle -> new double[2]);
        sommeEtNombre[0] += valeur;
        sommeEtNombre[1]++;
    }

    public void evaluer(List<Metriques> metriquesList) {
        if (metriquesList.isEmpty()) {
            System.out.println("Aucune métrique à évaluer.");
            return;
        }

        LocalDateTime dateDebut = metriquesList.stream()
                .map(Metriques::getTime)
                .map(java.sql.Timestamp::toLocalDateTime)
                .min(LocalDateTime::compareTo)
                .orElseThrow();
        LocalDateTime dateFin = metriquesList.stream()
                .map(Metriques::getTime)
                .map(java.sql.Timestamp::toLocalDateTime)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        for (Metriques metrique : metriquesList) {
            String nomServeur = metrique.getNomServeur();
            double latence = metrique.getLatence();
            double jitter = metrique.getJitter();
            double pertePaquets = metrique.getPertePaquets();
            double mos = metrique.getMos();

            ajouterValeur(nomServeur, TypeIndicateur.Latence, latence);
            ajouterValeur(nomServeur, TypeIndicateur.Jitter, jitter);
            ajouterValeur(nomServeur, TypeIndicateur.PertePaquets, pertePaquets);
            ajouterValeur(nomServeur, TypeIndicateur.Mos, mos);
        }

        //Evaluation des métriques et envoi de notifications si nécessaire
        for (EvaluationMetrique evaluation : evaluations) {
            if (evaluation.nombreValeursCollectees() == 0) {
                continue;
            }
            double valeurMoyenne = evaluation.valeur() / evaluation.nombreValeursCollectees();
            TypeIndicateur indicateur = evaluation.indicateur();

            // Vérification des seuils et envoi de notifications si nécessaire
            if (indicateur == TypeIndicateur.Latence && valeurMoyenne > 100) {
                notification.envoyerNotificationParEmail("La latence du serveur " + evaluation.nomServeur() + " est élevée : " + valeurMoyenne + " ms", "@adminVoip.com");
                notification.envoyerNotificationViaConsole("La latence du serveur " + evaluation.nomServeur() + " est élevée : " + valeurMoyenne + " ms", "@adminVoip.com");
                typeAlerte = TypeAlerte.CRITIQUE;
            }
            if (indicateur == TypeIndicateur.Jitter && valeurMoyenne > 10) {
                notification.envoyerNotificationParEmail("Le jitter du serveur " + evaluation.nomServeur() + " est élevé : " + valeurMoyenne + " ms", "@adminVoip.com");
                 typeAlerte = TypeAlerte.AVERTISSEMENT;
            }
            if (indicateur == TypeIndicateur.PertePaquets && valeurMoyenne > 5) {
                notification.envoyerNotificationParEmail("La perte de paquets du serveur " + evaluation.nomServeur() + " est élevée : " + valeurMoyenne + " %", "@adminVoip.com");
            }
            if (indicateur == TypeIndicateur.Mos && valeurMoyenne < 3) {
                notification.envoyerNotificationParEmail("Le MOS du serveur " + evaluation.nomServeur() + " est faible : " + valeurMoyenne, "@adminVoip.com");
                notification.envoyerNotificationViaConsole("Le MOS du serveur " + evaluation.nomServeur() + " est faible : " + valeurMoyenne, "@adminVoip.com");
            }
           sauvegardeDonnees.sauvegarderResultat(new ResultatEvaluation(
                   evaluation.nomServeur(), indicateur, valeurMoyenne, dateDebut, dateFin, typeAlerte));
                   ////System.out.println("Résultat d'évaluation sauvegardé pour le serveur " + evaluation.nomServeur() + " et l'indicateur " + indicateur + " avec la valeur moyenne : " + valeurMoyenne);
        }
        //System.out.println("Évaluation terminée pour " + sauvegardeDonnees.getResultats().size() + " mesures.");
    }

    // Sauvegarde des résultats d'évaluation dans la base de données
    public void intialiserEvaluations(List<Serveur> serveursList) {
        for (Serveur serveur : serveursList) {
            for (TypeIndicateur indicateur : TypeIndicateur.values()) {
                evaluations.add(new EvaluationMetrique(
                        serveur.getNomServeur(), indicateur, 0.0, 0));
            }
                  
        }
    }

    private void ajouterValeur(String nomServeur, TypeIndicateur indicateur, double valeur) {
       
    for (int index = 0; index < evaluations.size(); index++) {
        EvaluationMetrique evaluation = evaluations.get(index);

        if (Objects.equals(evaluation.nomServeur(), nomServeur)
                && evaluation.indicateur() == indicateur) {
          //  System.out.println("Ajout de la valeur " + valeur + " pour le serveur " + nomServeur + " et l'indicateur " + indicateur);
            evaluations.set(index, new EvaluationMetrique(
                    nomServeur,
                    indicateur,
                    evaluation.valeur() + valeur,
                    evaluation.nombreValeursCollectees() + 1));
            return;
        }
    }

  
}
}
