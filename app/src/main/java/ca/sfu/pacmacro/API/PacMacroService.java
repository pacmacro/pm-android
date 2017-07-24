package ca.sfu.pacmacro.API;

import java.util.List;
import java.util.Map;

import ca.sfu.pacmacro.API.model.CharacterData;
import ca.sfu.pacmacro.API.model.Id;
import ca.sfu.pacmacro.API.model.PelletData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Retrofit interface for the PacMacro server API
 */
public interface PacMacroService {
    @Headers("Content-Type: application/json")
    @POST("ghost")
    Call<Id> addCharacter(@Body Map<String, Float> latlng);

    @Headers("Content-Type: application/json")
    @PUT("player/{type}/location")
    Call<String> setCharacterLocation(@Path("type") String type, @Body Map<String, Double> latlng);

    @Headers("Content-Type: application/json")
    @POST("player/{type}")
    Call<String> selectCharacter(@Path("type") String type, @Body Map<String, Double> latlng);

    @DELETE("player/{type}")
    Call<String> deselectCharacter(@Path("type") String type);

    @Headers("Content-Type: application/json")
    @PUT("player/{type}/state")
    Call<String> updateCharacterState(@Path("type") String type, @Body Map<String, String> state);

    @GET("player/details")
    Call<List<CharacterData>> getCharacterDetails();

    @GET("pacdots")
    Call<List<PelletData>> getPellets();

}
