package ca.sfu.pacmacro.API;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ca.sfu.pacmacro.API.model.CharacterLocationData;

/**
 * Created by AlexLand on 2016-07-16.
 */
public class CharacterDeserializer implements JsonDeserializer<CharacterLocationData> {

    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_LOCATION = "location";

    @Override
    public CharacterLocationData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonCharacter = json.getAsJsonObject();
        JsonObject jsonLocation = jsonCharacter.get(PROPERTY_LOCATION).getAsJsonObject();
        String name = jsonCharacter.get(PROPERTY_NAME).getAsString();
        float latitude = jsonLocation.get(JsonProperties.PROPERTY_LATITUDE).getAsFloat();
        float longitude = jsonLocation.get(JsonProperties.PROPERTY_LONGITUDE).getAsFloat();
        return new CharacterLocationData(name, latitude, longitude);
    }
}
