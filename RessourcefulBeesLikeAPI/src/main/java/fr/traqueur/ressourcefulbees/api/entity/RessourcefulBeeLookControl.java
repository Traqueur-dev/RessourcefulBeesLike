package fr.traqueur.ressourcefulbees.api.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.animal.Bee;

public class RessourcefulBeeLookControl extends LookControl {

    private final RessourcefulBeeEntity bee;

    public RessourcefulBeeLookControl(RessourcefulBeeEntity entity) {
        super(entity);
        this.bee = entity;
    }

    @Override
    public void tick() {
        if (!bee.isAngry()) {
            super.tick();
        }
    }

    @Override
    protected boolean resetXRotOnTick() {
        return !bee.getPollinateGoal().isPollinating();
    }
}
