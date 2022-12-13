package org.mirea.pm.notes_backend.db;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Document(collection = "notes")
public class Note {
    @Transient
    private static final int MAX_TOSTRING_TEXT_LENGTH = 13;
    private @Id String id;

    @DBRef
    private User owner;

    private String text;

    private LocalDateTime changedDateTime;

    public Note() {}

    public Note(User owner, String text, LocalDateTime changedDateTime)
    {
        this.owner = owner;
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

    public void setChangedDateTime(LocalDateTime changedDateTime) {
        this.changedDateTime = changedDateTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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
        return Objects.hash(id, owner, text, changedDateTime);
    }

    @Override
    public String toString() {
        return String.format("Note(id=%s, owner_id=%s, text=%s, creationDate=%s)",
                id,
                owner.getId(),
                text.length() <= MAX_TOSTRING_TEXT_LENGTH ?
                        text : text.substring(0, MAX_TOSTRING_TEXT_LENGTH - 3) + "...",
                DateTimeFormatter.ofPattern("dd.MMMM.yyyy").format(changedDateTime));
    }
}
