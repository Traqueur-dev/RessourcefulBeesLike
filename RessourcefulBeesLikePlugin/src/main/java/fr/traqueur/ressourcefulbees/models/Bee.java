package fr.traqueur.ressourcefulbees.models;

import fr.traqueur.ressourcefulbees.api.models.IBee;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;

public record Bee(IBeeType type, boolean baby) implements IBee {
    @Override
    public IBeeType getBeeType() {
        return type;
    }

    @Override
    public boolean isBaby() {
        return baby;
    }
}
