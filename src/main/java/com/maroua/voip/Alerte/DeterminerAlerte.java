package com.maroua.voip.Alerte;

import com.maroua.voip.model.TypeAlerte;
import com.maroua.voip.model.TypeIndicateur;
import com.maroua.voip.notifications.NotificationParEmail;
import com.maroua.voip.notifications.NotificationViaConsole;

public class DeterminerAlerte {
    private EnvoyerAlerte ea = new EnvoyerAlerte();

     public  TypeAlerte determinerAlerte(
            String nomServeur,  TypeIndicateur indicateur, double valeurMoyenne) {

        if (indicateur == TypeIndicateur.Latence && valeurMoyenne > 100) {
           ea.envoyerAlerte(new NotificationParEmail(),
                    "La latence du serveur " + nomServeur + " est élevée : " + valeurMoyenne + " ms");
            ea.envoyerAlerte(new NotificationViaConsole(),
                    "La latence du serveur " + nomServeur + " est élevée : " + valeurMoyenne + " ms");
            return TypeAlerte.CRITIQUE;
        }
        if (indicateur == TypeIndicateur.Jitter && valeurMoyenne > 10) {
            ea.envoyerAlerte(new NotificationParEmail(),
                    "Le jitter du serveur " + nomServeur + " est élevé : " + valeurMoyenne + " ms");
            return TypeAlerte.AVERTISSEMENT;
        }
        if (indicateur == TypeIndicateur.PertePaquets && valeurMoyenne > 5) {
            ea.envoyerAlerte(new NotificationParEmail(),
                    "Le pertes du packets dans  " + nomServeur + " est eleve : " + valeurMoyenne);
                     return TypeAlerte.AVERTISSEMENT;
        }
        if (indicateur == TypeIndicateur.Mos && valeurMoyenne < 3) {
            ea.envoyerAlerte(new NotificationParEmail(),
                    "Le MOS du serveur " + nomServeur + " est faible : " + valeurMoyenne);
                     ea.envoyerAlerte(new NotificationViaConsole(),
                    "Le MOS du serveur " + nomServeur + " est faible : " + valeurMoyenne);

                   return TypeAlerte.CRITIQUE;
        }
        return TypeAlerte.NORMAL;
    }

}
