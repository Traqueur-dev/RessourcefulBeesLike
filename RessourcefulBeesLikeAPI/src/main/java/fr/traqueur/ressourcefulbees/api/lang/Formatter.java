package fr.traqueur.ressourcefulbees.api.lang;

import java.util.function.Function;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLikeAPI;
import fr.traqueur.ressourcefulbees.api.models.BeeType;
import org.apache.commons.lang.StringEscapeUtils;

public class Formatter {

    private final String pattern;
    private final Function<RessourcefulBeesLikeAPI, String> supplier;

    private Formatter(String pattern, Object supplier) {
        this.pattern = pattern;
        this.supplier = (wereWolfAPI) -> supplier.toString();
    }

    private Formatter(String pattern, Function<RessourcefulBeesLikeAPI, String> supplier) {
        this.pattern = pattern;
        this.supplier = supplier;
    }

    public static Formatter format(String pattern, Object supplier) {
        return new Formatter(pattern, supplier);
    }

    public static Formatter format(String pattern, Function<RessourcefulBeesLikeAPI, String> supplier) {
        return new Formatter(pattern, supplier);
    }

    public static Formatter beetype(BeeType beetype) {
        return format("&beetype&", (ressourcefulBeesLikeAPI -> ressourcefulBeesLikeAPI.translate(beetype.getType())));
    }

    public String handle(RessourcefulBeesLikeAPI api, String message) {
        return message.replaceAll(StringEscapeUtils.escapeJava(this.pattern), String.valueOf(this.supplier.apply(api)));
    }
}
