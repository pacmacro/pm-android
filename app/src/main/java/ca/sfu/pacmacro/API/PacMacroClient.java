package ca.sfu.pacmacro.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.sfu.pacmacro.API.events.CharacterReceivedEvent;
import ca.sfu.pacmacro.API.events.CharacterSentEvent;
import ca.sfu.pacmacro.API.events.CharacterStateReceivedEvent;
import ca.sfu.pacmacro.API.events.CharacterStateSentEvent;
import ca.sfu.pacmacro.API.events.PelletReceivedEvent;
import ca.sfu.pacmacro.API.model.CharacterData;
import ca.sfu.pacmacro.API.model.CharacterStateData;
import ca.sfu.pacmacro.API.model.Id;
import ca.sfu.pacmacro.Model.Character;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Retrofit API client for the PacMacro server API
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
        service.addCharacter(latlngMap).enqueue(new Callback<Id>() {
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

    public void getCharacters() {
        Call<List<CharacterData>> getCharacters = service.getCharacters();

        getCharacters.enqueue(new Callback<List<CharacterData>>() {
            @Override
            public void onResponse(Response<List<CharacterData>> response) {
                EventBus.getDefault().post(new CharacterReceivedEvent(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    public void getPellets() {
        //TODO: get pellets from API
        EventBus.getDefault().post(new PelletReceivedEvent());
    }

    public void updateCharacterState(int id, Character.CharacterState characterState) {
        Call<Id> updateCharacterState = service.updateCharacterState(id);

        updateCharacterState.enqueue(new Callback<Id>() {
            @Override
            public void onResponse(Response<Id> response) {
                EventBus.getDefault().post(new CharacterStateSentEvent());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void getCharacterStates() {
        Call<List<CharacterStateData>> getCharacterStates = service.getCharacterStates();

        getCharacterStates.enqueue(new Callback<List<CharacterStateData>>() {
            @Override
            public void onResponse(Response<List<CharacterStateData>> response) {
                EventBus.getDefault().post(new CharacterStateReceivedEvent(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
