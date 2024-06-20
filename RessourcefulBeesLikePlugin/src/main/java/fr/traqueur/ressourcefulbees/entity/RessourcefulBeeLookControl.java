package fr.traqueur.ressourcefulbees.entity;

import net.minecraft.world.entity.ai.control.LookControl;

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
