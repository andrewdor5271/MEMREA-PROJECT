package org.mirea.pm.notes_backend.controllers.auth.payload;

public enum AuthErrorCode {
    OK(0),
    PASSWORD_OR_LOGIN_INCORRECT(1),
    USERNAME_ALREADY_TAKEN(2);

    public final static String OK_MESSAGE = "ok";
    public final static String PASSWORD_OR_LOGIN_INCORRECT_MESSAGE =
            "Password or login incorrect. Please, provide correct credentials.";
    public final static String USERNAME_ALREADY_TAKEN_MESSAGE = "This username is already taken.";
    private int code;
    AuthErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
