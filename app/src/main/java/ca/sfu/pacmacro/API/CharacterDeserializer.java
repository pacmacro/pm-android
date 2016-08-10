package ca.sfu.pacmacro.API;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ca.sfu.pacmacro.API.model.CharacterData;

/**
 * Deserializer that converts JSON to CharacterData.
 *
 * Valid JSON example:
 * {
 *     "name": "Pacman",
 *     "state": "UNINITIALIZED",
 *     "location": {
 *         "latitude": 0.0,
 *         "longitude": 0.0
 *     }
 * }
 */
public class CharacterDeserializer implements JsonDeserializer<CharacterData> {

    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_STATE = "state";
    private static final String PROPERTY_LOCATION = "location";

    @Override
    public CharacterData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonCharacter = json.getAsJsonObject();
        JsonObject jsonLocation = jsonCharacter.get(PROPERTY_LOCATION).getAsJsonObject();
        String name = jsonCharacter.get(PROPERTY_NAME).getAsString();
        String state = jsonCharacter.get(PROPERTY_STATE).getAsString();
        float latitude = jsonLocation.get(JsonProperties.PROPERTY_LATITUDE).getAsFloat();
        float longitude = jsonLocation.get(JsonProperties.PROPERTY_LONGITUDE).getAsFloat();
        return new CharacterData(name, state, latitude, longitude);
    }
}
