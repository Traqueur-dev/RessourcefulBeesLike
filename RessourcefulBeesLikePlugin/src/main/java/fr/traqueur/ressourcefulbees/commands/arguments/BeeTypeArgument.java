package fr.traqueur.ressourcefulbees.commands.arguments;

import fr.traqueur.ressourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import fr.traqueur.ressourcefulbees.commands.api.arguments.ArgumentConverter;
import fr.traqueur.ressourcefulbees.commands.api.arguments.TabConverter;

import java.util.List;
import java.util.stream.Collectors;

public class BeeTypeArgument implements ArgumentConverter<BeeType>, TabConverter {

    private final BeeTypeManager beeTypeManager;

    public BeeTypeArgument(BeeTypeManager beeTypeManager) {
        this.beeTypeManager = beeTypeManager;
    }

    @Override
    public BeeType apply(String s) {
        return this.beeTypeManager.getBeeType(s);
    }

    @Override
    public List<String> onCompletion() {
        return this.beeTypeManager.getBeeTypes().keySet().stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}
