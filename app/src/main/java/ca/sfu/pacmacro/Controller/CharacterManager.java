package ca.sfu.pacmacro.Controller;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.API.events.CharacterReceivedEvent;
import ca.sfu.pacmacro.API.events.CharacterSentEvent;
import ca.sfu.pacmacro.API.model.CharacterData;
import ca.sfu.pacmacro.Model.Character;

/**
 * Handle the creation and updating of Characters and their location.
 */
public class CharacterManager {
    private final String TAG = "CharacterManager";
    private PacMacroClient mApiClient;
    private List<Character> mCharacterList = new ArrayList<>();
    private InitializeMarkerCallback mMapCallback;

    public CharacterManager(PacMacroClient apiClient, InitializeMarkerCallback callback, GameController gameController) {
        this.mApiClient = apiClient;
        this.mMapCallback = callback;

        EventBus.getDefault().register(this);

        Log.d(TAG, "CharacterManager: Get ghosts request sent");
        gameController.registerAction(new GameLoopAction() {
            @Override
            public void execute() {
                mApiClient.getCharacters();
            }
        });
    }

    @Subscribe
    public void onCharacterSent(CharacterSentEvent event) {
        if (event.getStatus() == CharacterSentEvent.RequestStatus.SUCCESS) {
            Log.d(TAG, "Added character: " + event.getResponse().message());
            mApiClient.getCharacters();
        }
        else {
            Log.d(TAG, "Failed to add character");
        }
    }

    @Subscribe
    public void onCharactersReceived(CharacterReceivedEvent event) {
        if (mCharacterList.isEmpty()) {
            for (CharacterData characterData: event.getCharacterDataList()) {
                LatLng characterLocation = characterData.getLocation();
                // TODO: properly input character types
                Marker marker = mMapCallback.initializeMarker(characterLocation, "Character");
                Character character = new Character(characterData.getId().getIdAsInt(),
                        Character.CharacterType.PACMAN, marker);
                Log.d(TAG, "onCharactersReceived: Character added at " + characterLocation);
                mCharacterList.add(character);
            }
        }
        else {
            for (CharacterData characterData : event.getCharacterDataList()) {
                LatLng characterLocation = characterData.getLocation();
                int id = characterData.getId().getIdAsInt();
                Character character = findCharacterById(id);
                if (character != null) {
                    character.updateLocation(characterLocation);
                    Log.d(TAG, "onCharactersReceived: Character updated at " + characterLocation);
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
