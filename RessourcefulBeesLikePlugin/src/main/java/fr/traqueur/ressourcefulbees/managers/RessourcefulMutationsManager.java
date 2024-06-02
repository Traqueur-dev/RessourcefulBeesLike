package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.MutationsManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.Mutation;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import fr.traqueur.ressourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.ressourcefulbees.models.RessourcefulBreed;
import fr.traqueur.ressourcefulbees.models.RessourcefulMutation;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RessourcefulMutationsManager implements MutationsManager, Saveable {

    private final RessourcefulBeesLikePlugin plugin;
    private final BeeTypeManager beeTypeManager;
    private final Map<Material, Mutation> mutations;

    public RessourcefulMutationsManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypeManager = plugin.getManager(BeeTypeManager.class);
        this.mutations = new HashMap<>();
    }

    @Override
    public RessourcefulBeesLikeAPI getPlugin() {
        return this.plugin;
    }

    @Override
    public String getFile() {
        return "mutations.yml";
    }

    @Override
    public void loadData() {
        FileConfiguration config = this.getConfig(this.plugin);

        for (Map<?, ?> map : config.getMapList(ConfigKeys.MUTATIONS)) {
            BeeType parent = this.beeTypeManager.getBeeType(((String) map.get(ConfigKeys.PARENT)));
            BeeType child = this.beeTypeManager.getBeeType(((String) map.get(ConfigKeys.CHILD)));
            Material block = Material.valueOf((String) map.get(ConfigKeys.BLOCK));

            try {
                RessourcefulMutation mutation = new RessourcefulMutation(parent, block, child);
                this.mutations.put(block, mutation);
            } catch (IllegalArgumentException e) {
                BeeLogger.severe("Invalid block type for mutation: " + block);
            }
        }

        BeeLogger.info("&aLoaded " + this.mutations.size() + " mutations.");
    }

    @Override
    public void saveData() {
        FileConfiguration config = this.getConfig(this.plugin);

        List<Map<String, Object>> mutations = this.mutations.values()
                .stream()
                .map(mutation -> (Map<String, Object>) new HashMap<String, Object>() {{
                    put(ConfigKeys.PARENT, mutation.getParent().getType());
                    put(ConfigKeys.CHILD, mutation.getChild().getType());
                    put(ConfigKeys.BLOCK, mutation.getBlock().name());
                }}).toList();

        config.set(ConfigKeys.MUTATIONS, mutations);
        try {
            config.save(new File(this.plugin.getDataFolder(), this.getFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
