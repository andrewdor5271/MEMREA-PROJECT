package org.mirea.pm.notes_backend.controllers.note.payload;

import java.util.List;

public class UploadSyncResponse {
    List<String> newNotesIds;

    public UploadSyncResponse(List<String> newNotesIds) {
        this.newNotesIds = newNotesIds;
    }

    public List<String> getNewNotesIds() {
        return newNotesIds;
    }

    public void setNewNotesIds(List<String> newNotesIds) {
        this.newNotesIds = newNotesIds;
    }
}
