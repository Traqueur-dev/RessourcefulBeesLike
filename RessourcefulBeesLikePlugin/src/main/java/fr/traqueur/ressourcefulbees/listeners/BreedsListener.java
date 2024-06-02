package fr.traqueur.ressourcefulbees.listeners;

import fr.traqueur.ressourcefulbees.api.events.BeeBreedEvent;
import fr.traqueur.ressourcefulbees.api.events.BeeSpawnEvent;
import fr.traqueur.ressourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.BreedsManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.Breed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bee;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class BreedsListener implements Listener {

    private final BreedsManager breedsManager;
    private final BeeTypeManager beeTypeManager;

    public BreedsListener(BreedsManager manager) {
        this.breedsManager = manager;
        this.beeTypeManager = manager.getPlugin().getManager(BeeTypeManager.class);
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (!(event.getEntity() instanceof Bee)) {
            return;
        }

        Bee father = (Bee) event.getFather();
        Bee mother = (Bee) event.getMother();
        event.getEntity().remove();

        BeeType fatherType = beeTypeManager.getBeeTypeFromBee(father);
        BeeType motherType = beeTypeManager.getBeeTypeFromBee(mother);
        Breed breed = breedsManager.getBreed(fatherType, motherType);
        BeeType childType;

        double random = Math.random();
        if(breed == null || random > breed.getPercent()) {
            childType = Math.random() > 0.5 ? fatherType : motherType;
        } else {
            childType = breed.getChild();
        }
        BeeBreedEvent beeBreedEvent = new BeeBreedEvent(fatherType, motherType, childType);
        Bukkit.getPluginManager().callEvent(beeBreedEvent);
        if (beeBreedEvent.isCancelled()) {
            return;
        }
        BeeSpawnEvent beeSpawnEvent = new BeeSpawnEvent(beeBreedEvent.getChildType(), mother.getLocation(), true);
        Bukkit.getPluginManager().callEvent(beeSpawnEvent);
    }
}
