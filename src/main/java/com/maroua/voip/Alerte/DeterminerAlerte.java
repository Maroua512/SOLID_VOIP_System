package com.maroua.voip.Alerte;
import java.util.Map;
import java.util.HashMap;
import com.maroua.voip.model.TypeAlerte;
import com.maroua.voip.model.TypeIndicateur;


public class DeterminerAlerte {
  
    Map<TypeIndicateur,Map<Double,Boolean>> regles = new HashMap<>();
   
     { regles.put(TypeIndicateur.Latence, Map.of(150.0,true));
      regles.put(TypeIndicateur.Jitter, Map.of(30.0,true));
      regles.put(TypeIndicateur.Mos, Map.of(3.6,false));
      regles.put(TypeIndicateur.PertePaquets, Map.of(1.0,true));}

    public TypeAlerte determinerAlerte(String nomServeur, TypeIndicateur indicateur, double valeurMoyenne) {
       
        Map<Double, Boolean> seuils = regles.get(indicateur);
        if (seuils != null) {
            for (Map.Entry<Double, Boolean> entry : seuils.entrySet()) {
                double seuil = entry.getKey();
                boolean sens = entry.getValue();
                if ((sens && valeurMoyenne > seuil) || (!sens && valeurMoyenne < seuil)) {
                    return TypeAlerte.CRITIQUE;
                }
            }
        }
        return TypeAlerte.NORMAL;
    }

    }
