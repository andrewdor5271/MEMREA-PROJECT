package org.mirea.pm.notes_backend.controllers.auth.payload;

import org.springframework.stereotype.Component;
public class SignInSuccessfulResponse extends OkAuthResponse {

    private String token;
    private String id;

    public SignInSuccessfulResponse(String token, String id) {
        this.token = token;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
