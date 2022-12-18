package org.mirea.pm.notes_backend.controllers.user;

import org.mirea.pm.notes_backend.controllers.note.payload.NotesListResponse;
import org.mirea.pm.notes_backend.controllers.user.payload.UsernameResponse;
import org.mirea.pm.notes_backend.db.Note;
import org.mirea.pm.notes_backend.db.User;
import org.mirea.pm.notes_backend.db.UserRepository;
import org.mirea.pm.notes_backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/current")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> current() {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(new UsernameResponse(userDetails.getUsername()));
    }
}
