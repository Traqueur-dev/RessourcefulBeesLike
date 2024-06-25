package fr.traqueur.ressourcefulbees.models;

import fr.traqueur.ressourcefulbees.api.models.BeeType;
import org.bukkit.Material;

public record RessourcefulBeeType(String type, Material food) implements BeeType {

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Material getFood() {
        return this.food;
    }
}
