package fr.traqueur.ressourcefulbees.models;

import fr.traqueur.ressourcefulbees.api.Tuple;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.Breed;

public class RessourcefulBreed implements Breed {

    private final Tuple<BeeType, BeeType> parents;
    private final double percent;
    private final BeeType child;

    public RessourcefulBreed(BeeType parent1, BeeType parent2, double percent, BeeType child) {
        this.parents = new Tuple<>(parent1, parent2);
        this.percent = percent;
        this.child = child;
    }

    @Override
    public Tuple<BeeType, BeeType> getParents() {
        return this.parents;
    }

    @Override
    public double getPercent() {
        return this.percent;
    }

    @Override
    public BeeType getChild() {
        return this.child;
    }
}
