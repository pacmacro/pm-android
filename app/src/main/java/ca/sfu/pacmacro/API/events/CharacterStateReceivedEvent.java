package ca.sfu.pacmacro.API.events;

import java.util.List;

import ca.sfu.pacmacro.API.model.CharacterStateData;

/**
 * Created by AlexLand on 2016-08-04.
 */
public class CharacterStateReceivedEvent {
    List<CharacterStateData> characterStateDataList;

    public CharacterStateReceivedEvent(List<CharacterStateData> characterStateDataList) {
        this.characterStateDataList = characterStateDataList;
    }

    public List<CharacterStateData> getCharacterStateDataList() {
        return characterStateDataList;
    }
}
