package ca.sfu.pacmacro.API.events;

import java.util.List;

import ca.sfu.pacmacro.API.model.CharacterData;

/**
 * Created by AlexLand on 2016-07-16.
 */
public class CharacterReceivedEvent {
    List<CharacterData> characterDataList;

    public CharacterReceivedEvent(List<CharacterData> characterDatas) {
        this.characterDataList = characterDatas;
    }

    public List<CharacterData> getCharacterDataList() {
        return characterDataList;
    }
}
