package org.mirea.pm.notes_backend.controllers.note.payload;

public class CreateNoteResponse {
    private String id;

    public CreateNoteResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
