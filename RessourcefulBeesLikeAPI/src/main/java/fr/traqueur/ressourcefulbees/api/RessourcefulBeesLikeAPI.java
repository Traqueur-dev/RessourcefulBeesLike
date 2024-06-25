package fr.traqueur.ressourcefulbees.api;

import fr.traqueur.ressourcefulbees.api.lang.Formatter;
import fr.traqueur.ressourcefulbees.api.lang.LangKey;

public interface RessourcefulBeesLikeAPI {

    <T> T getManager(Class<T> clazz);

    <I, T extends I> void registerManager(T instance, Class<I> clazz);

    void registerMessage(LangKey langKey);

    void registerLanguage(String key, String path);

    String translate(String key, Formatter... formatters);

    default String translate(LangKey langKey, Formatter... formatters) {
        return this.translate(langKey.getKey(), formatters);
    }



}
