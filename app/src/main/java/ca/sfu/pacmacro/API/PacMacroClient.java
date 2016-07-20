package ca.sfu.pacmacro.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.sfu.pacmacro.API.model.CharacterData;
import ca.sfu.pacmacro.API.events.CharacterSentEvent;
import ca.sfu.pacmacro.API.events.GhostReceivedEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by AlexLand on 2016-07-16.
 */
public class PacMacroClient {
    private Retrofit retrofit;
    private PacMacroService service;

    public PacMacroClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CharacterData.class, new CharacterDeserializer())
                .create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://pacmacro.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.service = retrofit.create(PacMacroService.class);
    }

    public void addGhost(float latitude, float longitude) {
        Map<String, Float> latlngMap = new HashMap<>();
        latlngMap.put(JsonProperties.PROPERTY_LATITUDE, latitude);
        latlngMap.put(JsonProperties.PROPERTY_LONGITUDE, longitude);
        service.addGhost(latlngMap).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) {
                EventBus.getDefault().post(new CharacterSentEvent(CharacterSentEvent.RequestStatus.SUCCESS, response));
            }

            @Override
            public void onFailure(Throwable t) {
                EventBus.getDefault().post(new CharacterSentEvent(CharacterSentEvent.RequestStatus.FAILED, null));
            }
        });
    }

    public void getGhosts() {
        Call<List<CharacterData>> getGhosts = service.getGhosts();

        getGhosts.enqueue(new Callback<List<CharacterData>>() {
            @Override
            public void onResponse(Response<List<CharacterData>> response) {
                EventBus.getDefault().post(new GhostReceivedEvent(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }
}
