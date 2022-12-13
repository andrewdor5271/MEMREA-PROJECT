package org.mirea.pm.notes_backend.db;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String>{
    List<Note> findByOwnerAndChangedDateTimeGreaterThan(User owner, LocalDateTime creationDateTime);
    List<Note> findByOwner(User owner);
}
