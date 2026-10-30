package com.maroua.voip.Persistance;

import java.util.ArrayList;
import java.util.List;

import com.maroua.voip.model.ResultatEvaluation;

public class SauvegardeDonnees {
    
    List<ResultatEvaluation> resultats = new ArrayList<ResultatEvaluation>();

    public SauvegardeDonnees() {
    }
    public void sauvegarderResultat(ResultatEvaluation resultat) {
        resultats.add(resultat);
    }

    public List<ResultatEvaluation> getResultats() {
        return resultats;
    }
}
