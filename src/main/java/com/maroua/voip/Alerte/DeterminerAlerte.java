package com.maroua.voip.Alerte;

import com.maroua.voip.model.TypeAlerte;
import com.maroua.voip.model.TypeIndicateur;
import com.maroua.voip.model.Sens;
import java.util.List;


public class DeterminerAlerte {
  
  
    List<RegleSeuil> regles = List.of(
        new RegleSeuil(Sens.SUPERIEUR, TypeIndicateur.Latence, 150.0, 100.0),
        new RegleSeuil(Sens.SUPERIEUR, TypeIndicateur.Jitter, 30.0, 20.0),
        new RegleSeuil(Sens.INFERIEUR, TypeIndicateur.Mos, 3.6, 4.0),
        new RegleSeuil(Sens.SUPERIEUR, TypeIndicateur.PertePaquets, 1.0, 0.5)
    );

    public TypeAlerte determinerAlerte(String nomServeur, TypeIndicateur indicateur, double valeurMoyenne) {
       
      
        for (RegleSeuil regle : regles) {
            if (regle.indicateur == indicateur) {
                if (regle.sens == Sens.SUPERIEUR) {
                    if (valeurMoyenne > regle.SeuilCritique) {
                        return TypeAlerte.CRITIQUE;
                    } else if (valeurMoyenne > regle.SeuilAvertissement) {
                        return TypeAlerte.AVERTISSEMENT;
                    }
                } else if (regle.sens == Sens.INFERIEUR) {
                    if (valeurMoyenne < regle.SeuilCritique) {
                        return TypeAlerte.CRITIQUE;
                    } else if (valeurMoyenne < regle.SeuilAvertissement) {
                        return TypeAlerte.AVERTISSEMENT;
                    }
                }
            }
        }
        return TypeAlerte.NORMAL;
    }

    }
