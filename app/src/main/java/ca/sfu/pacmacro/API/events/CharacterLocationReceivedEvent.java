package ca.sfu.pacmacro.API.events;

import java.util.List;

import ca.sfu.pacmacro.API.model.CharacterLocationData;

/**
 * Created by AlexLand on 2016-07-16.
 */
public class CharacterLocationReceivedEvent {
    List<CharacterLocationData> characterLocationDataList;

    public CharacterLocationReceivedEvent(List<CharacterLocationData> characterLocationDatas) {
        this.characterLocationDataList = characterLocationDatas;
    }

    public List<CharacterLocationData> getCharacterLocationDataList() {
        return characterLocationDataList;
    }
}
