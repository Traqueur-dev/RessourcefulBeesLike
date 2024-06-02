package fr.traqueur.ressourcefulbees.models;

import fr.traqueur.ressourcefulbees.api.models.Bee;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.Mutation;
import org.bukkit.Material;

public record RessourcefulMutation(BeeType parent, Material block, BeeType child) implements Mutation {

    public RessourcefulMutation {
        if(!block.isBlock()) {
            throw new IllegalArgumentException("Material must be a block");
        }
    }

    @Override
    public BeeType getParent() {
        return this.parent;
    }

    @Override
    public Material getBlock() {
        return this.block;
    }

    @Override
    public BeeType getChild() {
        return this.child;
    }
}
