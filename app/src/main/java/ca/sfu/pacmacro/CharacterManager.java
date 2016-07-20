package ca.sfu.pacmacro;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.API.events.CharacterSentEvent;
import ca.sfu.pacmacro.API.events.GhostReceivedEvent;
import ca.sfu.pacmacro.API.model.CharacterData;

/**
 * Created by AlexLand on 2016-07-19.
 */
public class CharacterManager {
    private final String TAG = "CharacterManager";
    private PacMacroClient mApiClient;
    private List<Character> mCharacterList = new ArrayList<>();
    private InitializeMarkerCallback mMapCallback;

    public CharacterManager(PacMacroClient apiClient, InitializeMarkerCallback callback) {
        this.mApiClient = apiClient;
        this.mMapCallback = callback;

        EventBus.getDefault().register(this);

        Log.d(TAG, "CharacterManager: Get ghosts request sent");
        Timer timer = new Timer("FetchGhosts");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mApiClient.getGhosts();
            }
        }, 0, 500);


        // Initialize all characters with markers pointing to the Harbor Center
//        LatLng latLng = new LatLng(49.2847564,-123.1118716);
//        for (Character.CharacterType type: Character.CharacterType.values()) {
//            Marker marker = callback.initializeMarker(latLng, type.name());
//            Character character = new Character(type, marker);
//            mCharacterList.add(character);
//        }
    }

    @Subscribe
    public void onCharacterSent(CharacterSentEvent event) {
        if (event.getStatus() == CharacterSentEvent.RequestStatus.SUCCESS) {
            Log.d(TAG, "Added character: " + event.getResponse().message());
            mApiClient.getGhosts();
        }
        else {
            Log.d(TAG, "Failed to add character");
        }
    }

    @Subscribe
    public void onGhostsReceived(GhostReceivedEvent event) {
        if (mCharacterList.isEmpty()) {
            for (CharacterData characterData: event.getCharacterDataList()) {
                LatLng characterLocation = characterData.getLatLng();
                // TODO: properly input character types
                Marker marker = mMapCallback.initializeMarker(characterLocation, "Character");
                Character character = new Character(characterData.getId().getIdAsInt(),
                        Character.CharacterType.PACMAN, marker);
                Log.d(TAG, "onGhostsReceived: Character added at " + characterLocation);
                mCharacterList.add(character);
            }
        }
        else {
            for (CharacterData characterData : event.getCharacterDataList()) {
                LatLng characterLocation = characterData.getLatLng();
                int id = characterData.getId().getIdAsInt();
                Character character = findCharacterById(id);
                if (character != null) {
                    character.updateLocation(characterLocation);
                    Log.d(TAG, "onGhostsReceived: Character updated at " + characterLocation);
                }
            }
        }
    }

    private Character findCharacterById(int id) {
        for (Character character: mCharacterList) {
            if (character.getId() == id) {
                return character;
            }
        }
        return null;
    }
}
