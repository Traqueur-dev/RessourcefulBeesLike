package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.BreedsManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.Breed;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import fr.traqueur.ressourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.ressourcefulbees.listeners.BreedsListener;
import fr.traqueur.ressourcefulbees.models.RessourcefulBreed;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class RessourcefulBreedsManager implements BreedsManager, Saveable {

    private final RessourcefulBeesLikePlugin plugin;
    private final BeeTypeManager beeTypeManager;
    private final Set<Breed> breeds;

    public RessourcefulBreedsManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypeManager = plugin.getManager(BeeTypeManager.class);
        this.breeds = new HashSet<>();

        this.plugin.getServer().getPluginManager().registerEvents(new BreedsListener(this), this.plugin);
    }

    @Override
    public Breed getBreed(BeeType fatherType, BeeType motherType) {
        for (Breed breed : this.breeds) {
            if(breed.getParents().getA().getType().equals(fatherType.getType())
                    && breed.getParents().getB().getType().equals(motherType.getType()) ||
                    breed.getParents().getA().getType().equals(motherType.getType())
                    && breed.getParents().getB().getType().equals(fatherType.getType())) {
                return breed;
            }
        }
        return null;
    }

    @Override
    public RessourcefulBeesLikeAPI getPlugin() {
        return this.plugin;
    }

    @Override
    public String getFile() {
        return "breeds.yml";
    }

    @Override
    public void loadData() {
        FileConfiguration config = this.getConfig(this.plugin);

        config.getMapList(ConfigKeys.BREEDS).forEach(map -> {
            String parents = (String) map.get(ConfigKeys.PARENTS);
            List<BeeType> parentsArray = Stream.of(parents.split(",")).map(beeTypeManager::getBeeType).toList();
            BeeType child = this.beeTypeManager.getBeeType(((String) map.get(ConfigKeys.CHILD)));
            double chance = (double) map.get(ConfigKeys.CHANCE);
            this.breeds.add(new RessourcefulBreed(parentsArray.get(0), parentsArray.get(1), chance, child));
        });

       BeeLogger.info("&aLoaded " + this.breeds.size() + " breeds.");
    }

    @Override
    public void saveData() {
        FileConfiguration config = this.getConfig(this.plugin);

        List<Map<String, Object>> breeds = this.breeds
                .stream()
                .map(breed -> (Map<String, Object>) new HashMap<String, Object>() {{
            put(ConfigKeys.PARENTS, breed.getParents().getA().getType() + "," + breed.getParents().getB().getType());
            put(ConfigKeys.CHILD, breed.getChild().getType());
            put(ConfigKeys.CHANCE, breed.getPercent());
        }}).toList();

        config.set(ConfigKeys.BREEDS, breeds);
        try {
            config.save(new File(this.plugin.getDataFolder(), this.getFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
