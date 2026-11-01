package com.maroua.voip.Traitement;

import com.maroua.voip.Persistance.SauvegardeDonnees;
import com.maroua.voip.model.Metriques;
import com.maroua.voip.model.ResultatEvaluation;
import com.maroua.voip.model.TypeAlerte;
import com.maroua.voip.model.TypeIndicateur;


import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.maroua.voip.Alerte.DeterminerAlerte;
import com.maroua.voip.Alerte.EnvoyerAlerte;
import com.maroua.voip.notifications.NotificationParEmail;
import com.maroua.voip.notifications.NotificationViaConsole;

public class Evaluation {

    public record EvaluationMetrique(
            String nomServeur,
            TypeIndicateur indicateur,
            double valeur,
            int nombreValeursCollectees) {
    }

    private final SauvegardeDonnees sauvegardeDonnees = new SauvegardeDonnees();
    DeterminerAlerte determinerAlerte = new DeterminerAlerte();
    EnvoyerAlerte envoyerAlerte = new EnvoyerAlerte();
   
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
                TypeAlerte typeAlerte = determinerAlerte.determinerAlerte(evaluation.nomServeur(), indicateur, valeurMoyenne);
                if (typeAlerte == TypeAlerte.CRITIQUE) {
                  
                   envoyerAlerte.envoyerAlerte(new NotificationParEmail(), "Alerte critique pour le serveur " + evaluation.nomServeur() + " sur l'indicateur " + indicateur);
                }
                else if (typeAlerte == TypeAlerte.AVERTISSEMENT) {
                    envoyerAlerte.envoyerAlerte(new NotificationViaConsole(), "Avertissement pour le serveur " + evaluation.nomServeur() + " sur l'indicateur " + indicateur);
                }
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

    
   

}
