package fr.traqueur.ressourcefulbees.entity.goals;

import fr.traqueur.ressourcefulbees.entity.RessourcefulBeeEntity;
import fr.traqueur.ressourcefulbees.entity.RessourcefulBeeGoal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class RessourcefulBeeGoToKnownFlowerGoal extends RessourcefulBeeGoal {

    private int travellingTicks;
    
    public RessourcefulBeeGoToKnownFlowerGoal(RessourcefulBeeEntity bee) {
        super(bee);
        this.travellingTicks = bee.random.nextInt(10);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }
    
    public boolean canBeeUse() {
        return bee.getSavedFlowerPos() != null && !bee.hasRestriction() && this.wantsToGoToKnownFlower() && bee.isFlowerValid(bee.getSavedFlowerPos()) && !bee.getSavedFlowerPos().closerThan(bee.blockPosition(), 2);
    }
    
    public boolean canBeeContinueToUse() {
        return this.canBeeUse();
    }

    @Override
    public void start() {
        this.travellingTicks = 0;
        super.start();
    }

    @Override
    public void stop() {
        this.travellingTicks = 0;
        bee.getNavigation().stop();
        bee.getNavigation().resetMaxVisitedNodesMultiplier();
    }

    @Override
    public void tick() {
        if (bee.getSavedFlowerPos() != null) {
            ++this.travellingTicks;
            if (this.travellingTicks > this.adjustedTickDelay(600)) {
                bee.setSavedFlowerPos(null);
            } else if (!bee.getNavigation().isInProgress()) {
                if (!bee.blockPosition().closerThan(bee.getSavedFlowerPos(), 32)) {
                    bee.setSavedFlowerPos(null);
                } else {
                    bee.pathfindRandomlyTowards(bee.getSavedFlowerPos());
                }
            }
        }
    }

    private boolean wantsToGoToKnownFlower() {
        return bee.ticksWithoutNectarSinceExitingHive > 2400;
    }

    public int getTravellingTicks() {
        return travellingTicks;
    }
}
