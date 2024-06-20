package fr.traqueur.ressourcefulbees.entity;

import net.minecraft.world.entity.ai.goal.Goal;

public abstract class RessourcefulBeeGoal extends Goal {

    protected final RessourcefulBeeEntity bee;

    public RessourcefulBeeGoal(RessourcefulBeeEntity bee) {
        this.bee = bee;
    }

    public abstract boolean canBeeUse();

    public abstract boolean canBeeContinueToUse();

    @Override
    public boolean canUse() {
        return this.canBeeUse() && !bee.isAngry();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canBeeContinueToUse() && !bee.isAngry();
    }

}
