package ca.sfu.pacmacro.API.events;

import java.util.List;

import ca.sfu.pacmacro.API.model.CharacterData;

/**
 * Data structure to store characters fetched from the API
 */
public class CharactersReceivedEvent {
    List<CharacterData> characterDataList;

    public CharactersReceivedEvent(List<CharacterData> characterDataList) {
        this.characterDataList = characterDataList;
    }

    public List<CharacterData> getCharacterDataList() {
        return characterDataList;
    }
}
