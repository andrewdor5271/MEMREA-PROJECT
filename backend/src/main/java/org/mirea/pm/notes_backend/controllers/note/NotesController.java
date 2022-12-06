package org.mirea.pm.notes_backend.controllers.note;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notes")
public class NotesController {
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public String all() {
        return "ok";
    }
}