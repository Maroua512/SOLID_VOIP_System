package com.maroua.voip;

import java.sql.Timestamp;
import java.util.List;

import com.maroua.voip.Traitement.Evaluation;
import com.maroua.voip.model.Metriques;
import com.maroua.voip.model.Serveur;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        List<Serveur> serveurs = List.of(
                new Serveur("serveur-paris"),
                new Serveur("serveur-lyon"));

        Evaluation evaluation = new Evaluation();
        evaluation.intialiserEvaluations(serveurs);

        List<Metriques> mesures = List.of(
                new Metriques("serveur-paris", 120.0, 12.0, 2.0, 3.8,
                        Timestamp.valueOf("2026-10-30 14:00:00")),
                new Metriques("serveur-paris", 180.0, 80.0, 1.0, 4.2,
                        Timestamp.valueOf("2026-10-30 14:05:00")),
                new Metriques("serveur-lyon", 45.0, 4.0, 0.5, 4.5,
                        Timestamp.valueOf("2026-10-30 14:02:00")),
                new Metriques("serveur-lyon", 55.0, 6.0, 1.0, 4.1,
                        Timestamp.valueOf("2026-10-30 14:07:00")));

        evaluation.calculerMoyennesParServeur(mesures).forEach((serveur, moyennes) ->
                System.out.println("Moyennes du serveur " + serveur + " : " + moyennes));
        evaluation.evaluer(mesures);
       // System.out.println("Évaluation terminée pour " + mesures.size() + " mesures.");
    }
}
