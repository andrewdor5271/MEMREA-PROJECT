package org.mirea.pm.notes_frontend.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.mirea.pm.notes_frontend.datamodels.DeletedNote;
import org.mirea.pm.notes_frontend.datamodels.NoteModel;

import java.util.List;

@Dao
public interface DeletedNoteDao {
    @Query("SELECT * FROM notes")
    List<DeletedNote> getAll();

    @Insert
    DeletedNote insert();

    @Query("DELETE FROM deleted_notes")
    void clear();
}
