package org.mirea.pm.notes_backend.controllers.auth.payload;

public abstract class AuthResponse {

    private int errorCode;
    private String message;

    public AuthResponse() { }

    public AuthResponse(AuthErrorCode errorCode, String message) {
        this.errorCode = errorCode.getCode();
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
