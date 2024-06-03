package fr.traqueur.ressourcefulbees.listeners;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.events.BeeMutationEvent;
import fr.traqueur.ressourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.MutationsManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.Mutation;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.NoSuchElementException;

public class MutationsListener implements Listener {

    private final MutationsManager mutationsManager;
    private final BeeTypeManager beeTypeManager;

    public MutationsListener(RessourcefulBeesLikePlugin plugin, MutationsManager mutationsManager) {
        this.mutationsManager = mutationsManager;
        this.beeTypeManager = plugin.getManager(BeeTypeManager.class);
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        Entity entity = event.getEntity();
        if(entity.getType() != EntityType.BEE) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo().clone().subtract(0, 1,0);
        if(to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()) {
            return;
        }

        if(to.getBlock().isEmpty()) {
            return;
        }

        Bee bee = (Bee) entity;
        if(!bee.hasNectar()) {
            return;
        }

        BeeType type = this.beeTypeManager.getBeeTypeFromBee(bee);
        try {
            Mutation mutation = this.mutationsManager.getMutation(type, to.getBlock().getType());
            BeeMutationEvent beeMutationEvent = new BeeMutationEvent(bee, to, mutation.getParent(), mutation.getChild());
            Bukkit.getPluginManager().callEvent(beeMutationEvent);
        } catch (NoSuchElementException ignored) {}
    }

    @EventHandler
    public void onBeeMutation(BeeMutationEvent event) {
        this.mutationsManager.mutateBee(event.getLocation(), event.getChild());
        event.getBee().setHasNectar(false);
    }

}
