package org.mirea.pm.notes_frontend.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.mirea.pm.notes_frontend.dao.NoteDao;
import org.mirea.pm.notes_frontend.database.converters.DateConverter;
import org.mirea.pm.notes_frontend.datamodels.NoteModel;

@Database(entities = {NoteModel.class}, version = VersionHandler.DB_VERSION)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
}
