package org.mirea.pm.notes_backend.db;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoteRepository extends MongoRepository<Note, String>{
}
