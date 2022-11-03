package org.mirea.pm.notes_frontend.datamodels;


import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.mirea.pm.notes_frontend.R;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class NoteModel {
    @PrimaryKey
    long id;
    String text;
    Date creationTime;

    public NoteModel(String text, Date creationTime) {
        this.text = text;
        this.creationTime = creationTime;
    }

    public String getText() {
        return text;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    @SuppressLint("SimpleDateFormat")
    public String getCreationTimeString(String format) {
        SimpleDateFormat dFormat = new SimpleDateFormat(format);
        return dFormat.format(getCreationTime());
    }

    @NonNull
    public String toString() {
        return text;
    }
}