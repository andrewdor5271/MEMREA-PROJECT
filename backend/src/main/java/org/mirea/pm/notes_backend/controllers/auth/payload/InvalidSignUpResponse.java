package org.mirea.pm.notes_backend.controllers.auth.payload;

public class InvalidSignUpResponse extends AuthResponse{
    public InvalidSignUpResponse() {
        super(
                AuthErrorCode.USERNAME_ALREADY_TAKEN,
                AuthErrorCode.USERNAME_ALREADY_TAKEN_MESSAGE
        );
    }
}
