package org.mirea.pm.notes_backend.controllers.note.payload;

public class DeleteNoteRequest {
    private String id;

    public DeleteNoteRequest(String id) {
        this.id = id;
    }

    public DeleteNoteRequest() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
