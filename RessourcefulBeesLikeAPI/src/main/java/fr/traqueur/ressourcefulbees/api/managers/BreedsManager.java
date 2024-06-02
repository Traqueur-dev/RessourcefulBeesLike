package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.Breed;

public interface BreedsManager {

    RessourcefulBeesLikeAPI getPlugin();

    Breed getBreed(BeeType fatherType, BeeType motherType);
}
