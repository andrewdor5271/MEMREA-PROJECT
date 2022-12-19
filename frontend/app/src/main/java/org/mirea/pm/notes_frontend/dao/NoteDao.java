package org.mirea.pm.notes_frontend.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.mirea.pm.notes_frontend.datamodels.NoteModel;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM NoteModel")
    List<NoteModel> getAll();

    @Insert
    void insert(NoteModel note);

    @Update
    void update(NoteModel note);

    @Delete
    void delete(NoteModel note);
}
