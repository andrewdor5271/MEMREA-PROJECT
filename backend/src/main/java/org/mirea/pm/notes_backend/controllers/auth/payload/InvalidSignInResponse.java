package org.mirea.pm.notes_backend.controllers.auth.payload;

public class InvalidSignInResponse extends AuthResponse {

    public InvalidSignInResponse() {
        super(
                AuthErrorCode.PASSWORD_OR_LOGIN_INCORRECT,
                AuthErrorCode.PASSWORD_OR_LOGIN_INCORRECT_MESSAGE
        );
    }
}
