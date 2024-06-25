package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.adapters.persistents.BeeTypePersistentDataType;
import fr.traqueur.ressourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import fr.traqueur.ressourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.ressourcefulbees.api.utils.Keys;
import fr.traqueur.ressourcefulbees.models.RessourcefulBeeType;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Bee;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RessourcefulBeeTypeManager implements BeeTypeManager, Saveable {

    private final RessourcefulBeesLikePlugin plugin;
    private final Map<String, BeeType> beeTypes;

    public RessourcefulBeeTypeManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypes = new HashMap<>();
    }

    public void registerBeeType(BeeType beeType) {
        this.beeTypes.put(beeType.getType().toLowerCase(), beeType);
        this.plugin.registerMessage(beeType::getType);
    }

    public BeeType getBeeType(String type) {
        return this.beeTypes.getOrDefault(type.toLowerCase(), null);
    }

    public BeeType getBeeTypeFromBee(Bee bee) {
        PersistentDataContainer container = bee.getPersistentDataContainer();
        if(!container.has(Keys.BEE)) {
            return this.getBeeType("normal_bee");
        }
        return container.get(Keys.BEE_TYPE, BeeTypePersistentDataType.INSTANCE);
    }

    public Map<String, BeeType> getBeeTypes() {
        return beeTypes;
    }

    @Override
    public RessourcefulBeesLikeAPI getPlugin() {
        return plugin;
    }

    @Override
    public String getFile() {
        return "beetypes.yml";
    }

    @Override
    public void loadData() {
        FileConfiguration config = this.getConfig(this.plugin);

        config.getMapList(ConfigKeys.BEETYPE).forEach(map -> {
            String type = (String) map.get(ConfigKeys.TYPE);
            Material food = Material.valueOf((String) map.get(ConfigKeys.FOOD));
            this.registerBeeType(new RessourcefulBeeType(type, food));
        });

        if(!this.beeTypes.containsKey("normal_bee")) {
            this.registerBeeType(new RessourcefulBeeType("normal_bee", Material.POPPY));
        }

        BeeLogger.info("&aLoaded " + this.beeTypes.size() + " bee types.");
    }

    @Override
    public void saveData() {
        FileConfiguration config = this.getConfig(this.plugin);

        List<Map<String, Object>> beetypes = this.beeTypes.values()
                .stream()
                .map(beetype -> (Map<String, Object>) new HashMap<String, Object>() {{
                    put(ConfigKeys.TYPE, beetype.getType());
                    put(ConfigKeys.FOOD, beetype.getFood().name());
                }}).toList();

        config.set(ConfigKeys.BEETYPE, beetypes);
        try {
            config.save(new File(this.plugin.getDataFolder(), this.getFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
