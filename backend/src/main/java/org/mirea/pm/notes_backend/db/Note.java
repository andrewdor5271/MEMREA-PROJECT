package org.mirea.pm.notes_backend.db;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Document(collection = "notes")
public class Note {
    @Transient
    private static final int MAX_TOSTRING_TEXT_LENGTH = 13;
    public @Id String id;

    public String ownerId;

    public String text;

    public LocalDateTime changedDateTime;

    public Note() {}

    public Note(String ownerId, String text, LocalDateTime changedDateTime)
    {
        this.ownerId = ownerId;
        this.text = text;
        this.changedDateTime = changedDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getChangedDateTime() {
        return changedDateTime;
    }

    public void setChangedDateTimeDateTime(LocalDateTime changedDateTime) {
        this.changedDateTime = changedDateTime;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Note otherNote)) {
            return false;
        }
        return id.equals(otherNote.getId()) &&
                text.equals(otherNote.getText()) &&
                changedDateTime.equals(otherNote.getChangedDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, text, changedDateTime);
    }

    @Override
    public String toString() {
        return String.format("Note(id=%s, owner_id=%s, text=%s, creationDate=%s)",
                id,
                ownerId,
                text.length() <= MAX_TOSTRING_TEXT_LENGTH ?
                        text : text.substring(0, MAX_TOSTRING_TEXT_LENGTH - 3) + "...",
                DateTimeFormatter.ofPattern("dd.MMMM.yyyy").format(changedDateTime));
    }
}
