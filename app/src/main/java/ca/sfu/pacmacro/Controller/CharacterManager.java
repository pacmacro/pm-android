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
    private CharacterDisplayCriteria mDisplayCriteria;

    public CharacterManager(PacMacroClient apiClient, GameController gameController, CharacterDisplayCriteria displayCriteria) {
        this(apiClient, new InitializeMarkerCallback() {
            @Override
            public Marker initializeMarker(LatLng latLng, String name, int drawableResourceId) {
                return null;
            }
        }, gameController, displayCriteria);
    }

    public CharacterManager(PacMacroClient apiClient, InitializeMarkerCallback callback, GameController gameController,
                            CharacterDisplayCriteria displayCriteria) {
        this.mApiClient = apiClient;
        this.mMapCallback = callback;
        this.mDisplayCriteria = displayCriteria;

        EventBus.getDefault().register(this);

        gameController.registerAction(new GameLoopAction() {
            @Override
            public void execute() {
                mApiClient.getCharacters();
                Log.v(TAG, "Get characters request sent");
            }
        });
    }

    @Subscribe
    public void onCharacterSent(CharacterSentEvent event) {
        if (event.getStatus() == CharacterSentEvent.RequestStatus.SUCCESS) {
            Log.v(TAG, "Added character: " + event.getResponse().message());
            mApiClient.getCharacters();
        }
        else {
            Log.v(TAG, "Failed to add character");
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
                    Marker marker = mMapCallback.initializeMarker(characterLocation, characterType.toString(), characterType.getIconId());
                    character = new Character(characterType, characterState, marker);
                    mCharacterList.add(character);
                    Log.v(TAG, "Character added: \n" +
                            "Type: " + characterType + ", " +
                            "Location: " + characterLocation + ", " +
                            "State: " + characterState);
                }
                else {
                    character.updateLocation(characterLocation);
                    character.updateState(characterState);
                    Log.v(TAG, "Character updated: \n" +
                            "Type: " + characterType + ", " +
                            "Location: " + characterLocation + ", " +
                            "State: " + characterState);
                }

                boolean isUninitialized = character.getState() == Character.CharacterState.UNINITIALIZED;
                boolean isHidden = mDisplayCriteria.isCharacterHidden(character);
                boolean displayCharacter = true;
                if (isUninitialized || isHidden) {
                    displayCharacter = false;
                }
                character.updateMarkerVisibility(displayCharacter);
            }
        }
        else {
            Log.v(TAG, "No characters received.");
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
        Character[] characters = new Character[mCharacterList.size()];
        return mCharacterList.toArray(characters);
    }


}
