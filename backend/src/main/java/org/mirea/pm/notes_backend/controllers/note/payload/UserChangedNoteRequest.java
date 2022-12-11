package org.mirea.pm.notes_backend.controllers.note.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class UserChangedNoteRequest {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime changeTime;

    public LocalDateTime getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(LocalDateTime changeTime) {
        this.changeTime = changeTime;
    }

    public UserChangedNoteRequest(LocalDateTime changeTime) {
        this.changeTime = changeTime;
    }

    public UserChangedNoteRequest() {}
}
