package fr.traqueur.ressourcefulbees;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLike;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.lang.LangKey;
import fr.traqueur.ressourcefulbees.api.lang.Formatter;
import fr.traqueur.ressourcefulbees.api.managers.*;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import fr.traqueur.ressourcefulbees.api.utils.ConfigKeys;
import fr.traqueur.ressourcefulbees.commands.api.CommandManager;
import fr.traqueur.ressourcefulbees.commands.arguments.BeeTypeArgument;
import fr.traqueur.ressourcefulbees.managers.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.util.*;


public final class RessourcefulBeesLikePlugin extends RessourcefulBeesLike {

    private CommandManager commandManager;
    private List<Saveable> saveables;

    private Set<LangKey> langKeys;
    private HashMap<String, YamlConfiguration> languages;
    private String lang;
    @Override
    public void onLoad() {
        this.commandManager = new CommandManager(this);
        this.saveables = new ArrayList<>();
        this.languages = new HashMap<>();
        this.langKeys = new HashSet<>();
    }

    @Override
    public void onEnable() {

        for (LangKeys value : LangKeys.values()) {
            this.registerMessage(value);
        }

        this.registerManager(new RessourcefulBeeTypeManager(this), BeeTypeManager.class);
        this.commandManager.registerConverter(BeeType.class, "beetype", new BeeTypeArgument(this.getManager(BeeTypeManager.class)));

        this.registerManager(new RessourcefulBeesManager(this), BeesManager.class);
        this.registerManager(new RessourcefulToolsManager(this), ToolsManager.class);
        this.registerManager(new RessourcefulBreedsManager(this), BreedsManager.class);
        this.registerManager(new RessourcefulMutationsManager(this), MutationsManager.class);

        this.saveables.forEach(saveable -> {
            this.saveOrUpdateConfiguration(saveable.getFile(), saveable.getFile());
            BeeLogger.info("&eLoaded " + saveable.getClass().getSimpleName() + " config file: " + saveable.getFile() + ".");
            saveable.loadData();
        });

        this.saveOrUpdateConfiguration("languages" + File.separator + "languages.yml", "languages" + File.separator + "languages.yml");
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "languages" + File.separator + "languages.yml"));
        langConfig.getMapList(ConfigKeys.LANGUAGE).forEach(map -> {
            String key = (String) map.keySet().iterator().next();
            String path = (String) map.get(key);
            try {
                this.registerLanguage(key, path);
            } catch (NoSuchElementException e) {
                BeeLogger.severe("&c" + e.getMessage());
            }

        });
        BeeLogger.info("&aLoaded languages files. (" + this.languages.size() + " languages)");
        this.lang = langConfig.getString(ConfigKeys.USED_LANG);
        if(!this.languages.containsKey(this.lang)) {
            getServer().getPluginManager().disablePlugin(this);
            throw new NoSuchElementException("The language file " + this.lang + " does not exist.");
        }

        BeeLogger.info("RessourcefulBees Plugin enabled successfully !");
    }

    @Override
    public void onDisable() {
        this.saveables.forEach(Saveable::saveData);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public <T> T getManager(Class<T> clazz) {
        RegisteredServiceProvider<T> provider = getServer().getServicesManager().getRegistration(clazz);
        if (provider == null) {
            throw new NoSuchElementException("No provider found for " + clazz.getSimpleName() + " class.");
        }
        return provider.getProvider();
    }

    @Override
    public <I, T extends I> void registerManager(T instance, Class<I> clazz) {
        if(instance instanceof Saveable saveable) {
            this.saveables.add(saveable);
        }

        getServer().getServicesManager().register(clazz, instance, this, ServicePriority.Normal);
        BeeLogger.info("&eManager registered: " + clazz.getSimpleName());
    }

    @Override
    public void registerLanguage(String key, String path) {
        this.saveOrUpdateConfiguration("languages" + File.separator + path, "languages" + File.separator + path);
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "languages" + File.separator + path));
        for (LangKey value : this.langKeys) {
            if(!langConfig.contains(value.getKey())) {
                throw new NoSuchElementException("The language file " + path + " does not contain the key " + value.getKey() + ".");
            }
        }

        this.languages.put(key, langConfig);
    }

    @Override
    public void registerMessage(LangKey langKey) {
        if(this.langKeys.stream().anyMatch(msg -> msg.getKey().equals(langKey.getKey()))) {
            throw new IllegalArgumentException("The message " + langKey.getKey() + " is already registered.");
        }
        this.langKeys.add(langKey);
    }

    @Override
    public String translate(String key, Formatter... formatters) {
        if(!this.languages.get(this.lang).contains(key)) {
            throw new NoSuchElementException("The key " + key + " does not exist in the language file " + this.lang + ".");
        }
        String message = this.languages.get(this.lang).getString(key);
        for (Formatter formatter : formatters) {
            message = formatter.handle(this, message);
        }
        return message;
    }
}
