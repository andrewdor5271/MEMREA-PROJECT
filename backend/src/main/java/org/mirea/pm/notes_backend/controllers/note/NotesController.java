package org.mirea.pm.notes_backend.controllers.note;
import org.mirea.pm.notes_backend.controllers.auth.payload.InvalidSignUpResponse;
import org.mirea.pm.notes_backend.controllers.note.payload.*;
import org.mirea.pm.notes_backend.db.Note;
import org.mirea.pm.notes_backend.db.NoteRepository;
import org.mirea.pm.notes_backend.db.User;
import org.mirea.pm.notes_backend.db.UserRepository;
import org.mirea.pm.notes_backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notes")
public class NotesController {
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository userRepository;

    private User getOwner(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("User with id " + id + " does not exist")
        );
    }

    private UserDetailsImpl getUserDetails() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> all() {
        UserDetailsImpl userDetails = getUserDetails();
        User owner = getOwner(userDetails.getId());
        List<Note> userNotes = noteRepository.findByOwner(owner);
        return ResponseEntity.ok(new NotesListResponse(userNotes));
    }

    @PostMapping("/changed")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changes(@Valid @RequestBody UserChangedNoteRequest request) {
        UserDetailsImpl userDetails = getUserDetails();
        User owner = getOwner(userDetails.getId());
        List<Note> newUserNotes = noteRepository.findByOwnerAndChangedDateTimeGreaterThan(
                owner, request.getChangeTime());
        return ResponseEntity.ok(new NotesListResponse(newUserNotes));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateNoteRequest request) {
        UserDetailsImpl userDetails = getUserDetails();
        User owner = getOwner(userDetails.getId());
        Note note = new Note(owner, request.getText(), request.getCreationTime());
        noteRepository.insert(note);
        return ResponseEntity.ok(new CreateNoteResponse(note.getId()));
    }

    @PostMapping("/modify")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> modify(@Valid @RequestBody ModifyNoteRequest request) {
        UserDetailsImpl userDetails = getUserDetails();
        Note note;
        try {
            note = noteRepository.findById(request.getId()).get();
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }

        User owner = getOwner(userDetails.getId());

        if(!Objects.equals(note.getOwner(), owner)) {
            return ResponseEntity.badRequest().build();
        }

        note.setText(request.getText());
        note.setChangedDateTime(request.getChangeTime());
        noteRepository.save(note);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> delete(@Valid @RequestBody DeleteNoteRequest request) {
        UserDetailsImpl userDetails = getUserDetails();
        Note note;
        try {
            note = noteRepository.findById(request.getId()).get();
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }

        User owner = getOwner(userDetails.getId());

        if(!Objects.equals(note.getOwner(), owner)) {
            return ResponseEntity.badRequest().build();
        }

        noteRepository.delete(note);
        return ResponseEntity.ok().build();
    }
}