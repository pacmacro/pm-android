package ca.sfu.pacmacro.API;

import java.util.List;
import java.util.Map;

import ca.sfu.pacmacro.API.model.CharacterData;
import ca.sfu.pacmacro.API.model.CharacterStateData;
import ca.sfu.pacmacro.API.model.Id;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit interface for the PacMacro server API
 */
public interface PacMacroService {
    @Headers("Content-Type: application/json")
    @POST("ghost")
    Call<Id> addCharacter(@Body Map<String, Float> latlng);

    @GET("ghost/locations")
    Call<List<CharacterData>> getCharacters();

    //TODO: add methods for fetching pellets

    @POST("ghost/{id}/state")
    Call<Id> updateCharacterState(@Path("id") int id);

    @GET("ghost/states")
    Call<List<CharacterStateData>> getCharacterStates();

}
