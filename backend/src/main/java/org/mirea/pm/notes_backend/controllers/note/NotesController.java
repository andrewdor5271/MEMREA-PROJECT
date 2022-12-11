package org.mirea.pm.notes_backend.controllers.note;
import org.mirea.pm.notes_backend.controllers.note.payload.CreateNoteRequest;
import org.mirea.pm.notes_backend.controllers.note.payload.CreateNoteResponse;
import org.mirea.pm.notes_backend.controllers.note.payload.NotesListResponse;
import org.mirea.pm.notes_backend.controllers.note.payload.UserChangedNoteRequest;
import org.mirea.pm.notes_backend.db.Note;
import org.mirea.pm.notes_backend.db.NoteRepository;
import org.mirea.pm.notes_backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notes")
public class NotesController {
    @Autowired
    NoteRepository noteRepository;
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> all() {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Note> userNotes = noteRepository.findByOwnerId(userDetails.getId());
        return ResponseEntity.ok(new NotesListResponse(userNotes));
    }

    @PostMapping("/changed")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changes(@Valid @RequestBody UserChangedNoteRequest request) {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Note> newUserNotes = noteRepository.findByOwnerIdAndChangedDateTimeGreaterThan(
                userDetails.getId(), request.getChangeTime());
        return ResponseEntity.ok(new NotesListResponse(newUserNotes));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateNoteRequest request) {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Note note = new Note(userDetails.getId(), request.getText(), request.getCreationTime());
        noteRepository.insert(note);
        return ResponseEntity.ok(new CreateNoteResponse(note.getId()));
    }
}