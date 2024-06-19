package fr.traqueur.ressourcefulbees.models;

import fr.traqueur.ressourcefulbees.api.models.Bee;
import fr.traqueur.ressourcefulbees.api.models.BeeType;

public record RessourcefulBee(BeeType type, boolean baby, boolean nectar) implements Bee {
    @Override
    public BeeType getBeeType() {
        return type;
    }

    @Override
    public boolean isBaby() {
        return baby;
    }

    @Override
    public boolean hasNectar() {
        return nectar;
    }
}
