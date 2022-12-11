package org.mirea.pm.notes_backend.controllers.note.payload;

import org.mirea.pm.notes_backend.db.Note;

import java.util.List;

public class NotesListResponse {
    private List<Note> notes;

    public NotesListResponse(List<Note> notes) {
        this.notes = notes;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
