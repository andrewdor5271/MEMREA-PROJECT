package org.mirea.pm.notes_backend.controllers.note.payload;

import java.util.List;

public class UploadSyncRequest {
    List<NoteStructure> notes;

    public UploadSyncRequest(){}
    public UploadSyncRequest(List<NoteStructure> notes) {
        this.notes = notes;
    }

    public List<NoteStructure> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteStructure> notes) {
        this.notes = notes;
    }
}
