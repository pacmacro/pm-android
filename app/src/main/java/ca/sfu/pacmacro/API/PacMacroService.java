package ca.sfu.pacmacro.API;

import java.util.List;
import java.util.Map;

import ca.sfu.pacmacro.API.model.CharacterData;
import ca.sfu.pacmacro.API.model.Id;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Retrofit interface for the PacMacro server API
 */
public interface PacMacroService {
    @Headers("Content-Type: application/json")
    @POST("ghost")
    Call<Id> addGhost(@Body Map<String, Float> latlng);

    @GET("ghost/locations")
    Call<List<CharacterData>> getGhosts();

}
