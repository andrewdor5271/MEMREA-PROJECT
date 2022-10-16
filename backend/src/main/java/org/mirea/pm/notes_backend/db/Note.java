package org.mirea.pm.notes_backend.db;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Note {

    private final int MAX_TOSTRING_TEXT_LENGTH = 13;
    public @Id String id;

    public String text;

    @Column(name = "creation_time", columnDefinition = "TIMESTAMP")
    public LocalDateTime creationDateTime;

    public Note() {}

    public Note(String text, LocalDateTime creationDateTime)
    {
        this.text = text;
        this.creationDateTime = creationDateTime;
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

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
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
                creationDateTime.equals(otherNote.getCreationDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Note(id=%s, text=%s, creationDate=%s)",
                id,
                text.length() <= MAX_TOSTRING_TEXT_LENGTH ?
                        text : text.substring(0, MAX_TOSTRING_TEXT_LENGTH - 3) + "...",
                DateTimeFormatter.ofPattern("dd.MMMM.yyyy").format(creationDateTime));
    }
}
