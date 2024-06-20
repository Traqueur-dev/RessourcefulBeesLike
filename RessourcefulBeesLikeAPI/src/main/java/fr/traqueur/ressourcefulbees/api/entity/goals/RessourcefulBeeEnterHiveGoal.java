package fr.traqueur.ressourcefulbees.api.entity.goals;

import fr.traqueur.ressourcefulbees.api.entity.RessourcefulBeeEntity;
import fr.traqueur.ressourcefulbees.api.entity.RessourcefulBeeGoal;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RessourcefulBeeEnterHiveGoal extends RessourcefulBeeGoal {

        public RessourcefulBeeEnterHiveGoal(RessourcefulBeeEntity bee) {
            super(bee);
        }

        @Override
        public boolean canBeeUse() {
            if (this.bee.hasHive() && this.bee.wantsToEnterHive() && this.bee.hivePos.closerToCenterThan(this.bee.position(), 2.0D)) {
                if (!this.bee.level().isLoadedAndInBounds(this.bee.hivePos)) return false; // Paper - Do not allow bees to load chunks for beehives
                BlockEntity tileentity = this.bee.level().getBlockEntity(this.bee.hivePos);

                if (tileentity instanceof BeehiveBlockEntity) {
                    BeehiveBlockEntity tileentitybeehive = (BeehiveBlockEntity) tileentity;

                    if (!tileentitybeehive.isFull()) {
                        return true;
                    }

                    this.bee.hivePos = null;
                }
            }

            return false;
        }

        @Override
        public boolean canBeeContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            if (!this.bee.level().isLoadedAndInBounds(this.bee.hivePos)) return; // Paper - Do not allow bees to load chunks for beehives
            BlockEntity tileentity = this.bee.level().getBlockEntity(this.bee.hivePos);

            if (tileentity instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity tileentitybeehive = (BeehiveBlockEntity) tileentity;

                tileentitybeehive.addOccupant(this.bee, this.bee.hasNectar());
            }

        }
    }