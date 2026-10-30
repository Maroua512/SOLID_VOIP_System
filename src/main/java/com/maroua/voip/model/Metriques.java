package com.maroua.voip.model;

import java.sql.Timestamp;

public class Metriques {
    String nom_serveur;
    public double latence ;
    double jitter ;
    double perte_paquets ;
    double mos ;
    Timestamp time;

    public Metriques(String nom_serveur, double latence, double jitter, double perte_paquets, double mos) {
        this(nom_serveur, latence, jitter, perte_paquets, mos, new Timestamp(System.currentTimeMillis()));
    }

    public Metriques(String nom_serveur, double latence, double jitter, double perte_paquets, double mos,
            Timestamp time) {
        this.nom_serveur = nom_serveur;
        this.latence = latence;
        this.jitter = jitter;
        this.perte_paquets = perte_paquets;
        this.mos = mos;
        this.time = time;
    }

    public String getNomServeur() {
        return nom_serveur;
    }
    public double getLatence() {
        return latence;
    }
    public double getJitter() {
        return jitter;
    }
    public double getPertePaquets() {
        return perte_paquets;
    }
    public double getMos() {
        return mos;
    }

    public Timestamp getTime() {
        return time;
    }
}
