package fr.traqueur.ressourcefulbees.api.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

public class RessourcefulBeeEntity extends Bee {

    private final org.bukkit.inventory.ItemStack food;

    public RessourcefulBeeEntity(World world, org.bukkit.inventory.ItemStack food) {
        super(EntityType.BEE, ((CraftWorld) world).getHandle());
        this.food = food;

        Ingredient ingredient = Ingredient.of(ItemStack.fromBukkitCopy(food));
        this.goalSelector.addGoal(3, new RessourcefulBeeTemptGoal(this, 1.25D, ingredient, false));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getGoal() instanceof TemptGoal && !(wrappedGoal.getGoal() instanceof RessourcefulBeeTemptGoal));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.asBukkitCopy().isSimilar(food);
    }
}
