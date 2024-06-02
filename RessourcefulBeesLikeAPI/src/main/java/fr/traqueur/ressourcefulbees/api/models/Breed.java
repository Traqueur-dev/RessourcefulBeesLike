package fr.traqueur.ressourcefulbees.api.models;

import fr.traqueur.ressourcefulbees.api.Tuple;

public interface Breed {

    Tuple<BeeType, BeeType> getParents();

    double getPercent();

    BeeType getChild();

}
