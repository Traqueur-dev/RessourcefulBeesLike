package fr.traqueur.ressourcefulbees;

import fr.traqueur.ressourcefulbees.api.lang.LangKey;

public enum LangKeys implements LangKey {

    BEE_GIVE("bee_give"),
    BEE_BOX_GIVE("bee_box_give")
    ;

    private final String key;

    LangKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
