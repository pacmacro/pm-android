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
        service.setCharacterLocation(characterType.toString(), latlngMap).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response) {
                Log.d(TAG, "Set location success");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Set location failed");
            }
        });
    }

    public void getPellets() {
        //TODO: get pellets from API
        EventBus.getDefault().post(new PelletReceivedEvent());
    }

    public void updateCharacterState(Character.CharacterType characterType, Character.CharacterState characterState) {
        Call<String> updateCharacterState = service.updateCharacterState(characterType.toString(), characterState.toString());

        updateCharacterState.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response) {
                EventBus.getDefault().post(new CharacterStateSentEvent());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Update character state failed: " + t.getMessage());
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
                Log.d(TAG, "Successfully selected character: " + characterType);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Failed to select character: " + characterType);
            }
        });
    }

    public void deselectCharacter(final Character.CharacterType characterType) {
        Call<String> deselectCharacter = service.deselectCharacter(characterType.toString());
        deselectCharacter.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response) {
                Log.d(TAG, "Successfully deselected character: " + characterType);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Failed to deselect character: " + characterType);
            }
        });
    }
}
