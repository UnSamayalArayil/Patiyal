package io.github.dnivra26.unsamayalarayil;

public class RegistrationResponse {
    public final String user_id;
    public final String message;
    public RegistrationResponse(String user_id, String message){
        this.user_id = user_id;
        this.message = message;
    }
}
