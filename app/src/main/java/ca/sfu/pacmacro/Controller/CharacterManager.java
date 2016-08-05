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
import ca.sfu.pacmacro.API.events.CharacterStateReceivedEvent;
import ca.sfu.pacmacro.API.model.CharacterData;
import ca.sfu.pacmacro.API.model.CharacterStateData;
import ca.sfu.pacmacro.Model.Character;

/**
 * Handle the creation and updating of Characters and their location.
 */
public class CharacterManager {
    private final String TAG = "CharacterManager";
    private PacMacroClient mApiClient;
    private List<Character> mCharacterList = new ArrayList<>();
    private InitializeMarkerCallback mMapCallback;

    public CharacterManager(PacMacroClient apiClient, GameController gameController) {
        this(apiClient, new InitializeMarkerCallback() {
            @Override
            public Marker initializeMarker(LatLng latLng, String name) {
                return null;
            }
        }, gameController);
    }

    public CharacterManager(PacMacroClient apiClient, InitializeMarkerCallback callback, GameController gameController) {
        this.mApiClient = apiClient;
        this.mMapCallback = callback;

        EventBus.getDefault().register(this);

        gameController.registerAction(new GameLoopAction() {
            @Override
            public void execute() {
                mApiClient.getCharacters();
                Log.d(TAG, "CharacterManager: Get characters request sent");
                mApiClient.getCharacterStates();
                Log.d(TAG, "CharacterManager: Get character states request sent");
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

    @Subscribe
    public void onCharacterStateReceived(CharacterStateReceivedEvent event) {
        for (CharacterStateData characterStateData: event.getCharacterStateDataList()) {
            Character.CharacterState characterState = characterStateData.getState();
            Character character = findCharacterById(characterStateData.getId());
            if (character != null) {
                character.updateState(characterState);
                Log.d(TAG, "onCharacterStateReceived: Character state updated to " + characterState);
            }
        }
    }

    public Character findCharacterById(int id) {
        for (Character character: mCharacterList) {
            if (character.getId() == id) {
                return character;
            }
        }
        return null;
    }

    public Character[] getCharacters() {
        return new Character[] {new Character(0, Character.CharacterType.PACMAN, null),
                                new Character(0, Character.CharacterType.INKY, null),
                                new Character(0, Character.CharacterType.BLINKY, null),
                                new Character(0, Character.CharacterType.PINKY, null),
                                new Character(0, Character.CharacterType.CLYDE, null)};
    }
}
