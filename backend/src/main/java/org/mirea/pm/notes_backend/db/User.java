package org.mirea.pm.notes_backend.db;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document(collection = "users")
public class User {

    private @Id String id;

    @Indexed(unique = true)
    @NotBlank
    @Size(min = 1, max = 20)
    private String name;
    @NotBlank
    @Size(min = 4, max = 20)
    private String passwordHash;



    @DBRef
    private Set<Role> roles = new HashSet<>();

    public User() {}

    public User(String name, String passwordHash) {
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
