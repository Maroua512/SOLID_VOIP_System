package com.maroua.voip.model;

import java.time.LocalDateTime;


public class ResultatEvaluation {
    String nom_serveur;
    TypeIndicateur type_indicateur;
    double valeur_moyenne;
    LocalDateTime date_debut;
    LocalDateTime date_fin;
    TypeAlerte type_alerte;
   
    public ResultatEvaluation(String nom_serveur, TypeIndicateur type_indicateur, double valeur_moyenne, LocalDateTime date_debut, LocalDateTime date_fin, TypeAlerte type_alerte) {
        this.nom_serveur = nom_serveur;
        this.type_indicateur = type_indicateur;
        this.valeur_moyenne = valeur_moyenne;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.type_alerte = type_alerte;
    }
   
}
