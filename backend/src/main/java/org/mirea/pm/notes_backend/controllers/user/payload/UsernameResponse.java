package org.mirea.pm.notes_backend.controllers.user.payload;

public class UsernameResponse {
    private String username;

    public UsernameResponse(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
