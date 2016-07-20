package ca.sfu.pacmacro.API.events;

import retrofit2.Response;

/**
 * Created by AlexLand on 2016-07-16.
 */
public class CharacterSentEvent {
    private RequestStatus status;
    private Response response;

    public enum RequestStatus {
        SUCCESS,
        FAILED
    }

    public CharacterSentEvent(RequestStatus status, Response response) {
        this.status = status;
        this.response = response;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public Response getResponse() {
        return response;
    }
}
