package fr.traqueur.ressourcefulbees.entity;

import fr.traqueur.ressourcefulbees.entity.goals.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

import java.util.List;

public class RessourcefulBeeEntity extends Bee {

    private final org.bukkit.inventory.ItemStack food;
    private final RessourcefulBeeGoToKnownFlowerGoal goToKnownFlowerGoal;
    private final RessourcefulBeePollinateGoal pollinateGoal;
    private final RessourcefulBeeGoToHiveGoal goToHiveGoal;

    public int remainingCooldownBeforeLocatingNewFlower;
    public int remainingCooldownBeforeLocatingNewHive;

    public RessourcefulBeeEntity(World world, org.bukkit.inventory.ItemStack food) {
        super(EntityType.BEE, ((CraftWorld) world).getHandle());
        this.food = food;
        this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(this.random, 20, 60);
        Ingredient ingredient = Ingredient.of(ItemStack.fromBukkitCopy(food));
        this.goToKnownFlowerGoal = new RessourcefulBeeGoToKnownFlowerGoal(this);
        this.pollinateGoal = new RessourcefulBeePollinateGoal(this);
        this.goToHiveGoal = new RessourcefulBeeGoToHiveGoal(this);

        this.goalSelector.addGoal(1, new RessourcefulBeeEnterHiveGoal(this));
        this.goalSelector.addGoal(3, new RessourcefulBeeTemptGoal(this, 1.25D, ingredient, false));
        this.goalSelector.addGoal(5, new RessourcefulBeeLocateHiveGoal(this));
        this.goalSelector.addGoal(4, this.pollinateGoal);
        this.goalSelector.addGoal(5, this.goToHiveGoal);
        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);

        this.lookControl = new RessourcefulBeeLookControl(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getGoal() instanceof TemptGoal && !(wrappedGoal.getGoal() instanceof RessourcefulBeeTemptGoal));
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getGoal() instanceof Bee.BeeGoToKnownFlowerGoal);
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getPriority() == 4 && !(wrappedGoal.getGoal() instanceof RessourcefulBeePollinateGoal)); //pollinateGoal is only goal with 4 priority
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getGoal() instanceof Bee.BeeGoToHiveGoal);
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> {
            Goal goal = wrappedGoal.getGoal();
            return wrappedGoal.getPriority() == 5 && !(goal instanceof FollowParentGoal) && !(goal instanceof RessourcefulBeeGoToHiveGoal);
        }); // remove LocateHiveGoal because is private so we can't access it
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getPriority() == 1 && !(wrappedGoal.getGoal() instanceof RessourcefulBeeEnterHiveGoal));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.asBukkitCopy().isSimilar(food);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.stayOutOfHiveCountdown > 0) {
                --this.stayOutOfHiveCountdown;
            }

            if (this.remainingCooldownBeforeLocatingNewHive > 0) {
                --this.remainingCooldownBeforeLocatingNewHive;
            }

            if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
                --this.remainingCooldownBeforeLocatingNewFlower;
            }

            boolean flag = this.isAngry() && !this.hasStung() && this.getTarget() != null && this.getTarget().distanceToSqr((Entity) this) < 4.0D;

            this.setRolling(flag);
            if (this.tickCount % 20 == 0 && !this.isHiveValid()) {
                this.hivePos = null;
            }
        }
    }

    @Override
    public int getTravellingTicks() {
        return Math.max(this.goToHiveGoal.getTravellingTicks(), this.goToKnownFlowerGoal.getTravellingTicks());
    }

    @Override
    public List<BlockPos> getBlacklistedHives() {
        return this.goToHiveGoal.getBlacklistedTargets();
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigationflying = new FlyingPathNavigation(this, world) {
            @Override
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }

            @Override
            public void tick() {
                if (!RessourcefulBeeEntity.this.pollinateGoal.isPollinating()) {
                    super.tick();
                }
            }
        };

        navigationflying.setCanOpenDoors(false);
        navigationflying.setCanFloat(false);
        navigationflying.setCanPassDoors(true);
        return navigationflying;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            boolean result = super.hurt(source, amount);
            if (result && !this.level().isClientSide) {
                this.pollinateGoal.stopPollinating();
            }

            return result;
        }
    }

    public boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        } else if (!this.blockPosition().closerThan(this.hivePos, 32)) {
            return false;
        } else {
            if (this.level().getChunkIfLoadedImmediately(this.hivePos.getX() >> 4, this.hivePos.getZ() >> 4) == null) return true; // Paper - just assume the hive is still there, no need to load the chunk(s)
            BlockEntity tileentity = this.level().getBlockEntity(this.hivePos);

            return tileentity != null && tileentity.getType() == BlockEntityType.BEEHIVE;
        }
    }

    private boolean isHiveNearFire() {
        if (this.hivePos == null) {
            return false;
        } else {
            if (!this.level().isLoadedAndInBounds(this.hivePos)) return false;
            BlockEntity tileentity = this.level().getBlockEntity(this.hivePos);

            return tileentity instanceof BeehiveBlockEntity && ((BeehiveBlockEntity) tileentity).isFireNearby();
        }
    }

    public boolean doesHiveHaveSpace(BlockPos pos) {
        if (!this.level().isLoadedAndInBounds(pos)) return false; // Paper - Do not allow bees to load chunks for beehives
        BlockEntity tileentity = this.level().getBlockEntity(pos);

        return tileentity instanceof BeehiveBlockEntity && !((BeehiveBlockEntity) tileentity).isFull();
    }

    public boolean isFlowerValid(BlockPos pos) {
        return this.level().isLoaded(pos) && this.level().getBlockState(pos).getBukkitMaterial() == food.getType();
    }

    public boolean wantsToEnterHive() {
        if (this.stayOutOfHiveCountdown <= 0 && !this.pollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
            boolean flag = this.ticksWithoutNectarSinceExitingHive > 3600 || this.level().isRaining() || this.level().isNight() || this.hasNectar();

            return flag && !this.isHiveNearFire();
        } else {
            return false;
        }
    }

    public void pathfindRandomlyTowards(BlockPos pos) {
        Vec3 vec3d = Vec3.atBottomCenterOf(pos);
        byte b0 = 0;
        BlockPos blockposition1 = this.blockPosition();
        int i = (int) vec3d.y - blockposition1.getY();

        if (i > 2) {
            b0 = 4;
        } else if (i < -2) {
            b0 = -4;
        }

        int j = 6;
        int k = 8;
        int l = blockposition1.distManhattan(pos);

        if (l < 15) {
            j = l / 2;
            k = l / 2;
        }

        Vec3 vec3d1 = AirRandomPos.getPosTowards(this, j, k, b0, vec3d, 0.3141592741012573D);

        if (vec3d1 != null) {
            this.navigation.setMaxVisitedNodesMultiplier(0.5F);
            this.navigation.moveTo(vec3d1.x, vec3d1.y, vec3d1.z, 1.0D);
        }
    }

    public org.bukkit.inventory.ItemStack getFood() {
        return food;
    }

    public RessourcefulBeeGoToHiveGoal getGoToHiveGoal() {
        return goToHiveGoal;
    }

    public RessourcefulBeePollinateGoal getPollinateGoal() {
        return pollinateGoal;
    }
}