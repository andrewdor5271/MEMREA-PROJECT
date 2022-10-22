package org.mirea.pm.notes_backend.controllers.auth.payload;

public class OkAuthResponse extends AuthResponse{
    public OkAuthResponse() {
        super(
                AuthErrorCode.OK,
                AuthErrorCode.OK_MESSAGE
        );
    }
}
