package org.mirea.pm.notes_backend;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mirea.pm.notes_backend.db.Note;
import org.mirea.pm.notes_backend.db.NoteRepository;
import org.mirea.pm.notes_backend.db.User;
import org.mirea.pm.notes_backend.db.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotesApplication.class)
@TestPropertySource(locations = "classpath:application.properties")
class NotesApplicationTests {
	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void checkNotesRepo() throws Exception {
		User user = new User("Test", "b");
		userRepository.save(user);

		Note note = new Note(user, "Test", LocalDateTime.now());
		note = noteRepository.save(note);

		List<Note> userNotes = noteRepository.findByOwner(user);

		if(userNotes.contains(note)) {
			noteRepository.delete(note);
			userRepository.delete(user);
		}
		else {
			noteRepository.delete(note);
			userRepository.delete(user);
			throw new Exception("Failed to add note");
		}

	}

}
