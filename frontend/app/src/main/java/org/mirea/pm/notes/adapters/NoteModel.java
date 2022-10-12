package org.mirea.pm.notes.adapters;


import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import org.mirea.pm.notes.R;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class NoteModel {
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
