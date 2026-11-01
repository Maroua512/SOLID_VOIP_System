package com.maroua.voip.Alerte;
import com.maroua.voip.model.TypeIndicateur;
import com.maroua.voip.model.Sens;

public class RegleSeuil {
    
    Sens sens ; 
    TypeIndicateur indicateur ;
    double SeuilCritique ;
    double SeuilAvertissement ;

    public RegleSeuil(Sens sens, TypeIndicateur indicateur, double seuilCritique, double seuilAvertissement) {
        this.sens = sens;
        this.indicateur = indicateur;
        SeuilCritique = seuilCritique;
        SeuilAvertissement = seuilAvertissement;
    }
    

}
