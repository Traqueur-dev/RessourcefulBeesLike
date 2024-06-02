package fr.traqueur.ressourcefulbees.api.adapters.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.ressourcefulbees.api.managers.BeeTypeManager;
import fr.traqueur.ressourcefulbees.api.models.Bee;
import fr.traqueur.ressourcefulbees.api.models.BeeType;

import java.io.IOException;

public class BeeAdapter extends TypeAdapter<Bee> {

    private static final String TYPE = "beetype";
    private static final String BABY = "baby";

    private final BeeTypeManager manager;

    public BeeAdapter(BeeTypeManager manager) {
        this.manager = manager;
    }

    @Override
    public void write(JsonWriter jsonWriter, fr.traqueur.ressourcefulbees.api.models.Bee bee) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name(TYPE).value(bee.getBeeType().getType());
        jsonWriter.name(BABY).value(bee.isBaby());
        jsonWriter.endObject();

    }

    @Override
    public fr.traqueur.ressourcefulbees.api.models.Bee read(JsonReader jsonReader) throws IOException {
        BeeType type = null;
        boolean baby = false;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case TYPE -> type = this.manager.getBeeType(jsonReader.nextString());
                case BABY -> baby = jsonReader.nextBoolean();
            }
        }
        jsonReader.endObject();

        return new Bee(type, baby);
    }

    private record Bee(BeeType type, boolean baby) implements fr.traqueur.ressourcefulbees.api.models.Bee {
        @Override
        public BeeType getBeeType() {
            return type;
        }

        @Override
        public boolean isBaby() {
            return baby;
        }
    }
}
