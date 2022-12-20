package org.mirea.pm.notes_backend.controllers.note;
import org.mirea.pm.notes_backend.controllers.note.payload.*;
import org.mirea.pm.notes_backend.db.Note;
import org.mirea.pm.notes_backend.db.NoteRepository;
import org.mirea.pm.notes_backend.db.User;
import org.mirea.pm.notes_backend.db.UserRepository;
import org.mirea.pm.notes_backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
        List<NoteStructure> userNotes =
                noteRepository.findByOwner(owner).stream().map(note ->
                        new NoteStructure(note.getText(), note.getChangedDateTime(), note.getId()))
                        .toList();
        return ResponseEntity.ok(new NotesListResponse(userNotes));
    }

    @PostMapping("/changed")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changes(@Valid @RequestBody UserChangedNoteRequest request) {
        UserDetailsImpl userDetails = getUserDetails();
        User owner = getOwner(userDetails.getId());
        List<NoteStructure> newUserNotes = noteRepository.findByOwnerAndChangedDateTimeGreaterThan(
                owner, request.getChangeTime()).stream().map(note ->
                        new NoteStructure(note.getText(), note.getChangedDateTime(), note.getId()))
                .toList();
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
            return ResponseEntity.ok().build();
        }

        User owner = getOwner(userDetails.getId());

        if(!Objects.equals(note.getOwner(), owner)) {
            return ResponseEntity.badRequest().build();
        }

        noteRepository.delete(note);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload_sync")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> upload_sync(@Valid @RequestBody UploadSyncRequest request) {
        UserDetailsImpl userDetails = getUserDetails();

        try {
            User user = userRepository.findByName(userDetails.getUsername()).get();

            noteRepository.deleteByIdNotInAndOwner(
                    request.getNotes().stream()
                            .map(NoteStructure::getId)
                            .filter(id -> !Objects.equals(id, "")).toList(),
                    user);

            for(NoteStructure elem : request.getNotes()) {
                Note note = noteRepository.findById(elem.getId()).orElse(null);
                if(note == null) {
                    continue;
                }
                if(!note.getOwner().equals(user)) {
                    continue;
                }
                note.setText(elem.getText());
                note.setChangedDateTime(elem.getUpdateTime());
                noteRepository.save(note);
            }

            List<Note> newNotes = noteRepository.saveAll(request.getNotes().stream()
                            .filter(noteStructure -> Objects.equals(noteStructure.getId(), ""))
                            .map(noteStructure ->
                                    new Note(user, noteStructure.getText(), noteStructure.getUpdateTime())
                            ).toList());

            return ResponseEntity.ok(new NotesListResponse(newNotes.stream()
                            .map(note -> new NoteStructure(note.getText(), note.getChangedDateTime(), note.getId()))
                            .toList()));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}