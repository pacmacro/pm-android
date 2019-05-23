package ca.sfu.pacmacro.API;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.sfu.pacmacro.API.events.CharacterSentEvent;
import ca.sfu.pacmacro.API.events.CharacterStateSentEvent;
import ca.sfu.pacmacro.API.events.CharactersReceivedEvent;
import ca.sfu.pacmacro.API.events.PelletReceivedEvent;
import ca.sfu.pacmacro.API.model.CharacterData;
import ca.sfu.pacmacro.API.model.Id;
import ca.sfu.pacmacro.API.model.PelletData;
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
    private static final String TAG = "PacMacroClient";
    private static final String BASE_URL = "http://pacmacro.herokuapp.com/";

    private Retrofit retrofit;
    private PacMacroService service;

    public PacMacroClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CharacterData.class, new CharacterDeserializer())
                .registerTypeAdapter(PelletData.class, new PelletDeserializer())
                .create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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
        Call<List<CharacterData>> getCharacterDetails = service.getCharacterDetails();

        getCharacterDetails.enqueue(new Callback<List<CharacterData>>() {
            @Override
            public void onResponse(Response<List<CharacterData>> response) {
                EventBus.getDefault().post(new CharactersReceivedEvent(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    public void setCharacterLocation(Character.CharacterType characterType, double latitude, double longitude) {
        Map<String, Double> latlngMap = new HashMap<>();
        latlngMap.put(JsonProperties.PROPERTY_LATITUDE, latitude);
        latlngMap.put(JsonProperties.PROPERTY_LONGITUDE, longitude);
        if(characterType!=null) {
            service.setCharacterLocation(characterType.toString(), latlngMap).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Response<String> response) {
                    Log.v(TAG, "Set location success");
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.v(TAG, "Set location failed: " + t.getMessage());
                }
            });
        }
    }

    public void getPellets() {
        Call<List<PelletData>> getPellets = service.getPellets();
        getPellets.enqueue(new Callback<List<PelletData>>() {
            @Override
            public void onResponse(Response<List<PelletData>> response) {
                EventBus.getDefault().post(new PelletReceivedEvent(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.v(TAG, "Get pellets failed: " + t.getMessage());
            }
        });
    }

    public void updateCharacterState(Character.CharacterType characterType, Character.CharacterState characterState) {
        Map<String, String> stateMap = new HashMap<>();
        stateMap.put(JsonProperties.PROPERTY_STATE, characterState.toString());
        Call<String> updateCharacterState = service.updateCharacterState(characterType.toString(), stateMap);

        updateCharacterState.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response) {
                EventBus.getDefault().post(new CharacterStateSentEvent());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.v(TAG, "Update character state failed: " + t.getMessage());
            }
        });
    }

    public void selectCharacter(final Character.CharacterType characterType, double latitude, double longitude) {
        Map<String, Double> latlngMap = new HashMap<>();
        latlngMap.put(JsonProperties.PROPERTY_LATITUDE, latitude);
        latlngMap.put(JsonProperties.PROPERTY_LONGITUDE, longitude);
        Call<String> selectCharacter = service.selectCharacter(characterType.toString(), latlngMap);
        selectCharacter.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response) {
                Log.v(TAG, "Successfully selected character: " + characterType);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.v(TAG, "Failed to select character: " + characterType);
            }
        });
    }

    public void deselectCharacter(final Character.CharacterType characterType) {
        Call<String> deselectCharacter = service.deselectCharacter(characterType.toString());
        deselectCharacter.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response) {
                Log.v(TAG, "Successfully deselected character: " + characterType);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.v(TAG, "Failed to deselect character: " + characterType);
            }
        });
    }
}
