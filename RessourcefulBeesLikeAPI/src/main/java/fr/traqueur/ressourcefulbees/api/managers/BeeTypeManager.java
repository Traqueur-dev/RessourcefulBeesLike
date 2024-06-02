package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import org.bukkit.entity.Bee;

import java.util.Map;

public interface BeeTypeManager {

    void registerBeeType(BeeType BeeType);

    BeeType getBeeType(String type);

    BeeType getBeeTypeFromBee(Bee bee);

    Map<String, BeeType> getBeeTypes();

    RessourcefulBeesLikeAPI getPlugin();
}
