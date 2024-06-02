package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import org.bukkit.entity.Bee;

import java.util.Map;

public interface IBeeTypeManager {

    void registerBeeType(IBeeType IBeeType);

    IBeeType getBeeType(String type);

    IBeeType getBeeTypeFromBee(Bee bee);

    Map<String, IBeeType> getBeeTypes();

    RessourcefulBeesLikeAPI getPlugin();
}
