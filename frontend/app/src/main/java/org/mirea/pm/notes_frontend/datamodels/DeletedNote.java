package org.mirea.pm.notes_frontend.datamodels;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "deleted_notes")
public class DeletedNote {
    @PrimaryKey
    public long id;

    public String mongoId;

    public Date deletionDate;
}
