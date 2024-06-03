package fr.traqueur.ressourcefulbees.api.managers;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.api.models.Mutation;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;
import java.util.Set;

public interface MutationsManager {

    Set<Mutation> getMutationsForBlock(Material block);

    Set<Mutation> getMutationsForParent(BeeType parent);

    Mutation getMutation(BeeType parent, Material block);

    void mutateBee(Location to, BeeType child);

    Map<Material, Set<Mutation>> getMutations();

    RessourcefulBeesLikeAPI getPlugin();
}
