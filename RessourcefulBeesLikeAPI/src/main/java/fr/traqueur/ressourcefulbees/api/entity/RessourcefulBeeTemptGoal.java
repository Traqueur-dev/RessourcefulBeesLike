package fr.traqueur.ressourcefulbees.api.entity;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;

public class RessourcefulBeeTemptGoal extends TemptGoal {

    public RessourcefulBeeTemptGoal(PathfinderMob entity, double speed, Ingredient food, boolean canBeScared) {
        super(entity, speed, food, canBeScared);
    }

}
