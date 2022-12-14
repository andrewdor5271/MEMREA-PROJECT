package org.mirea.pm.notes_backend.controllers.note.payload;

import org.mirea.pm.notes_backend.db.Note;

import java.util.List;

public class NotesListResponse {
    private List<NoteStructure> notes;

    public NotesListResponse(List<NoteStructure> notes) {
        this.notes = notes;
    }

    public List<NoteStructure> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteStructure> notes) {
        this.notes = notes;
    }
}
