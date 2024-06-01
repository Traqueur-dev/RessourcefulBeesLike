package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.IBreedsManager;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import fr.traqueur.ressourcefulbees.api.models.IBreed;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import fr.traqueur.ressourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.ressourcefulbees.listeners.BreedsListener;
import fr.traqueur.ressourcefulbees.models.Breed;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class BreedsManager implements IBreedsManager, Saveable {

    private final RessourcefulBeesLikePlugin plugin;
    private final IBeeTypeManager beeTypeManager;
    private final Set<IBreed> breeds;

    public BreedsManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypeManager = plugin.getManager(IBeeTypeManager.class);
        this.breeds = new HashSet<>();

        this.plugin.getServer().getPluginManager().registerEvents(new BreedsListener(this), this.plugin);
    }

    @Override
    public IBreed getBreed(IBeeType fatherType, IBeeType motherType) {
        for (IBreed breed : this.breeds) {
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
            List<IBeeType> parentsArray = Stream.of(parents.split(",")).map(beeTypeManager::getBeeType).toList();
            IBeeType child = this.beeTypeManager.getBeeType(((String) map.get(ConfigKeys.CHILD)));
            double chance = (double) map.get(ConfigKeys.CHANCE);
            this.breeds.add(new Breed(parentsArray.get(0), parentsArray.get(1), chance, child));
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
