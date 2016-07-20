package ca.sfu.pacmacro.API.events;

import java.util.List;

import ca.sfu.pacmacro.API.model.CharacterData;

/**
 * Created by AlexLand on 2016-07-16.
 */
public class GhostReceivedEvent {
    List<CharacterData> characterDatas;

    public GhostReceivedEvent(List<CharacterData> characterDatas) {
        this.characterDatas = characterDatas;
    }

    public List<CharacterData> getCharacterDataList() {
        return characterDatas;
    }
}
