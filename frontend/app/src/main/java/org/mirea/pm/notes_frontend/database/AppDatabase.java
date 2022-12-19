package org.mirea.pm.notes_frontend.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import org.mirea.pm.notes_frontend.dao.NoteDao;
import org.mirea.pm.notes_frontend.datamodels.NoteModel;

@Database(entities = {NoteModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
}
