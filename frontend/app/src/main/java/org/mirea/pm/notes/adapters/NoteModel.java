package org.mirea.pm.notes.adapters;


import java.time.LocalDateTime;
import java.util.Date;

public class NoteModel {
    String text;
    Date creationTime;

    public NoteModel(String text, Date creationTime) {
        this.text = text;
        this.creationTime = creationTime;
    }

    public String getText() {
        return text;
    }

    public Date getCreationTime() {
        return creationTime;
    }
}
