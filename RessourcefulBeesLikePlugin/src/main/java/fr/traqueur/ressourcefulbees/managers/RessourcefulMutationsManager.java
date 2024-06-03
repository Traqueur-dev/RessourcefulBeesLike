package fr.traqueur.ressourcefulbees.managers;

import fr.traqueur.ressourcefulbees.RessourcefulBeesLikePlugin;
import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.BeesManager;
import fr.traqueur.ressourcefulbees.api.managers.MutationsManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.Mutation;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import fr.traqueur.ressourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.ressourcefulbees.listeners.MutationsListener;
import fr.traqueur.ressourcefulbees.models.RessourcefulMutation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RessourcefulMutationsManager implements MutationsManager, Saveable {

    private final RessourcefulBeesLikePlugin plugin;
    private final BeeTypeManager beeTypeManager;
    private final BeesManager beesManager;
    private final Map<Material, Set<Mutation>> mutations;

    public RessourcefulMutationsManager(RessourcefulBeesLikePlugin plugin) {
        this.plugin = plugin;
        this.beeTypeManager = plugin.getManager(BeeTypeManager.class);
        this.beesManager = plugin.getManager(BeesManager.class);
        this.mutations = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(new MutationsListener(plugin, this), plugin);
    }

    @Override
    public Set<Mutation> getMutationsForBlock(Material block) {
        return this.mutations.getOrDefault(block, new HashSet<>());
    }

    @Override
    public Set<Mutation> getMutationsForParent(BeeType parent) {
        return this.mutations.values().stream().flatMap(Set::stream).filter(mutation -> mutation.getParent().getType().equals(parent.getType())).collect(Collectors.toSet());
    }

    @Override
    public Mutation getMutation(BeeType parent, Material block) {
        return this.mutations.getOrDefault(block, new HashSet<>()).stream().filter(mutation -> mutation.getParent().getType().equals(parent.getType())).findFirst().orElseThrow();
    }

    @Override
    public void mutateBee(Location to, BeeType child) {
        to.getBlock().setType(Material.AIR);
        ItemStack item = this.beesManager.generateBeeSpawnEgg(child);
        to.getWorld().dropItem(to.add(0, 0.6, 0), item);
    }

    @Override
    public Map<Material, Set<Mutation>> getMutations() {
        return this.mutations;
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
                Set<Mutation> mutations = this.mutations.getOrDefault(block, new HashSet<>());
                RessourcefulMutation mutation = new RessourcefulMutation(parent, block, child);
                mutations.add(mutation);
                this.mutations.put(block, mutations);
            } catch (IllegalArgumentException e) {
                BeeLogger.severe("Invalid block type for mutation: " + block);
            }
        }

        BeeLogger.info("&aLoaded " + this.mutations.size() + " mutations.");
    }

    @Override
    public void saveData() {
        FileConfiguration config = this.getConfig(this.plugin);

        List<Map<String, Object>> mutations = this.mutations.values().stream().flatMap(mutationsSet -> mutationsSet.stream().map(mutation -> (Map<String, Object>) new HashMap<String, Object>() {{
            put(ConfigKeys.PARENT, mutation.getParent().getType());
            put(ConfigKeys.CHILD, mutation.getChild().getType());
            put(ConfigKeys.BLOCK, mutation.getBlock().name());
        }})).toList();

        config.set(ConfigKeys.MUTATIONS, mutations);
        try {
            config.save(new File(this.plugin.getDataFolder(), this.getFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
