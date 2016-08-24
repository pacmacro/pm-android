package ca.sfu.pacmacro.API;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ca.sfu.pacmacro.API.model.PelletData;

/**
 * Deserializer that converts JSON to PelletData.
 *
 * Valid JSON example:
 *   {
 *       "location" : {
 *           "latitude" : 999.999,
 *          "longitude" : 888.888
 *       },
 *       "eaten" : true,
 *       "powerdot" : false
 *   }
 */
public class PelletDeserializer implements JsonDeserializer<PelletData> {

    private static final String PROPERTY_EATEN = "eaten";
    private static final String PROPERTY_POWERDOT = "powerdot";
    private static final String PROPERTY_LOCATION = "location";

    @Override
    public PelletData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonCharacter = json.getAsJsonObject();
        JsonObject jsonLocation = jsonCharacter.get(PROPERTY_LOCATION).getAsJsonObject();
        boolean eaten = jsonCharacter.get(PROPERTY_EATEN).getAsBoolean();
        boolean powerdot = jsonCharacter.get(PROPERTY_POWERDOT).getAsBoolean();
        float latitude = jsonLocation.get(JsonProperties.PROPERTY_LATITUDE).getAsFloat();
        float longitude = jsonLocation.get(JsonProperties.PROPERTY_LONGITUDE).getAsFloat();
        return new PelletData(powerdot, latitude, longitude, eaten);
    }
}
