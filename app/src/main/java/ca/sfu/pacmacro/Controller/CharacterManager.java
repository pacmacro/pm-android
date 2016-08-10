package ca.sfu.pacmacro.Controller;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.API.events.CharacterSentEvent;
import ca.sfu.pacmacro.API.events.CharactersReceivedEvent;
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
                Log.d(TAG, "Get characters request sent");
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
    public void onCharactersReceived(CharactersReceivedEvent event) {
        if (event.getCharacterDataList() != null) {
            for (CharacterData characterData : event.getCharacterDataList()) {

                Character.CharacterType characterType = characterData.getType();
                Character.CharacterState characterState = characterData.getState();
                LatLng characterLocation = characterData.getLocation();

                Character character = getCharacterByType(characterType);
                if (character == null) {
                    Marker marker = mMapCallback.initializeMarker(characterLocation, characterType.toString());
                    character = new Character(characterType, characterState, marker);
                    mCharacterList.add(character);
                    Log.d(TAG, "Character added: \n" +
                            "Type: " + characterType + ", " +
                            "Location: " + characterLocation + ", " +
                            "State: " + characterState);
                }
                else {
                    character.updateLocation(characterLocation);
                    character.updateState(characterState);
                    Log.d(TAG, "Character updated: \n" +
                            "Type: " + characterType + ", " +
                            "Location: " + characterLocation + ", " +
                            "State: " + characterState);
                }
            }
        }
        else {
            Log.d(TAG, "No characters received.");
        }
    }

    public Character getCharacterByType(Character.CharacterType type) {
        for (Character character : mCharacterList) {
            if (character.getType() == type) {
                return character;
            }
        }
        return null;
    }

    public Character[] getCharacters() {
        return (Character[]) mCharacterList.toArray();
//        return new Character[] {new Character(Character.CharacterType.PACMAN, null),
//                                new Character(Character.CharacterType.INKY, null),
//                                new Character(Character.CharacterType.BLINKY, null),
//                                new Character(Character.CharacterType.PINKY, null),
//                                new Character(Character.CharacterType.CLYDE, null)};
    }
}
