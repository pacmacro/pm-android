package ca.sfu.pacmacro.API.events;

import java.util.List;

import ca.sfu.pacmacro.API.model.PelletData;

/**
 * Created by AlexLand on 2016-08-02.
 */
public class PelletReceivedEvent {
    List<PelletData> pelletDataList;

    public List<PelletData> getPelletDataList() {
        return pelletDataList;
    }
}
