package com.maroua.voip.Traitement;

import com.maroua.voip.Persistance.SauvegardeDonnees;
import com.maroua.voip.model.Metriques;
import com.maroua.voip.model.ResultatEvaluation;
import com.maroua.voip.model.TypeAlerte;
import com.maroua.voip.model.TypeIndicateur;
import com.maroua.voip.notifications.Notification;
import com.maroua.voip.notifications.NotificationParEmail;
import com.maroua.voip.notifications.NotificationViaConsole;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Evaluation {

    public record EvaluationMetrique(
            String nomServeur,
            TypeIndicateur indicateur,
            double valeur,
            int nombreValeursCollectees) {
    }

    private final SauvegardeDonnees sauvegardeDonnees = new SauvegardeDonnees();
    
   
    public void evaluer(List<Metriques> metriquesList) {
     Map<String, Map<TypeIndicateur, EvaluationMetrique>> evaluationsParServeur =  new LinkedHashMap<>();
     Map<String, LocalDateTime[]> periodesParServeur = new LinkedHashMap<>();
   
        if (metriquesList.isEmpty()) {
            System.out.println("Aucune métrique à évaluer.");
            return;
        }

        for (Metriques metrique : metriquesList) {
            String nomServeur = metrique.getNomServeur();
            Map<TypeIndicateur, EvaluationMetrique> evaluations = evaluationsParServeur
                    .computeIfAbsent(nomServeur, nom -> new EnumMap<>(TypeIndicateur.class));

            ajouterValeur(evaluations, nomServeur, TypeIndicateur.Latence, metrique.getLatence());
            ajouterValeur(evaluations, nomServeur, TypeIndicateur.Jitter, metrique.getJitter());
            ajouterValeur(evaluations, nomServeur, TypeIndicateur.PertePaquets, metrique.getPertePaquets());
            ajouterValeur(evaluations, nomServeur, TypeIndicateur.Mos, metrique.getMos());

            LocalDateTime date = metrique.getTime().toLocalDateTime();
            LocalDateTime[] periode = periodesParServeur.computeIfAbsent(
                    nomServeur, nom -> new LocalDateTime[] { date, date });
            if (date.isBefore(periode[0])) {
                periode[0] = date;
            }
            if (date.isAfter(periode[1])) {
                periode[1] = date;
            }
        }

        for (Map.Entry<String, Map<TypeIndicateur, EvaluationMetrique>> serveur
                : evaluationsParServeur.entrySet()) {
            LocalDateTime[] periode = periodesParServeur.get(serveur.getKey());
            for (EvaluationMetrique evaluation : serveur.getValue().values()) {
                double valeurMoyenne = evaluation.valeur() / evaluation.nombreValeursCollectees();
                TypeIndicateur indicateur = evaluation.indicateur();
                TypeAlerte typeAlerte = determinerAlerte(evaluation.nomServeur(), indicateur, valeurMoyenne);

                sauvegardeDonnees.sauvegarderResultat(new ResultatEvaluation(
                        evaluation.nomServeur(),
                        indicateur,
                        valeurMoyenne,
                        periode[0],
                        periode[1],
                        typeAlerte));
            }
        }
    }

    private void ajouterValeur(
            Map<TypeIndicateur, EvaluationMetrique> evaluations,
            String nomServeur,
            TypeIndicateur indicateur,
            double valeur) {
        EvaluationMetrique evaluation = evaluations.get(indicateur);
        if (evaluation == null) {
            evaluations.put(indicateur, new EvaluationMetrique(nomServeur, indicateur, valeur, 1));
            return;
        }

        evaluations.put(indicateur, new EvaluationMetrique(
                evaluation.nomServeur(),
                indicateur,
                evaluation.valeur() + valeur,
                evaluation.nombreValeursCollectees() + 1));
    }

    private TypeAlerte determinerAlerte(
            String nomServeur,
            TypeIndicateur indicateur,
            double valeurMoyenne) {
        if (indicateur == TypeIndicateur.Latence && valeurMoyenne > 100) {
            envoyerAlerte(new NotificationParEmail(),
                    "La latence du serveur " + nomServeur + " est élevée : " + valeurMoyenne + " ms");
            envoyerAlerte(new NotificationViaConsole(),
                    "La latence du serveur " + nomServeur + " est élevée : " + valeurMoyenne + " ms");
            return TypeAlerte.CRITIQUE;
        }
        if (indicateur == TypeIndicateur.Jitter && valeurMoyenne > 10) {
            envoyerAlerte(new NotificationParEmail(),
                    "Le jitter du serveur " + nomServeur + " est élevé : " + valeurMoyenne + " ms");
            return TypeAlerte.AVERTISSEMENT;
        }
        if (indicateur == TypeIndicateur.PertePaquets && valeurMoyenne > 5) {
            envoyerAlerte(new NotificationParEmail(),
                    "Le pertes du packets dans  " + nomServeur + " est eleve : " + valeurMoyenne);
                     return TypeAlerte.AVERTISSEMENT;
        }
        if (indicateur == TypeIndicateur.Mos && valeurMoyenne < 3) {
            envoyerAlerte(new NotificationParEmail(),
                    "Le MOS du serveur " + nomServeur + " est faible : " + valeurMoyenne);
                     envoyerAlerte(new NotificationViaConsole(),
                    "Le MOS du serveur " + nomServeur + " est faible : " + valeurMoyenne);

                   return TypeAlerte.CRITIQUE;
        }
        return TypeAlerte.NORMAL;
    }

    private void envoyerAlerte(Notification notification, String message) {
        notification.envoyerNotification(message, "@adminVoip.com");     
       
    }

}
