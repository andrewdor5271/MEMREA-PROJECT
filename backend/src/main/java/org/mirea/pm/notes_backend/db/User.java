package org.mirea.pm.notes_backend.db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class User {

    public @Id @GeneratedValue String id;

    public String name;
    public String passwordHash;

    public User() {}

    public User(String name, String passwordHash) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User otherUser)) {
            return false;
        }
        return id.equals(otherUser.getId()) &&
                name.equals(otherUser.getName()) &&
                passwordHash.equals(otherUser.getPasswordHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, passwordHash);
    }

    @Override
    public String toString() {
        return String.format("Note(id=%s, name=%s, passwordHash=%s)",
                id, name, passwordHash);
    }
}
