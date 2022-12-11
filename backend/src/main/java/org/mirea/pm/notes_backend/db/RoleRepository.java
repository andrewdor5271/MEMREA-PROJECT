package org.mirea.pm.notes_backend.db;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
    boolean existsByName(ERole name);
}
