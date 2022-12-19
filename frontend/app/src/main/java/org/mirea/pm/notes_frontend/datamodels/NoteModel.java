package org.mirea.pm.notes_frontend.datamodels;


import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "notes")
public class NoteModel {
    @PrimaryKey
    public long id;

    public String mongoId;

    public String text;

    public Date updateDate;

    public NoteModel(String text, Date updateDate) {
        this.mongoId = "";
        this.text = text;
        this.updateDate = updateDate;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public String getText() {
        return text;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    @SuppressLint("SimpleDateFormat")
    public String getCreationTimeString(String format) {
        SimpleDateFormat dFormat = new SimpleDateFormat(format);
        return dFormat.format(getUpdateDate());
    }

    @NonNull
    public String toString() {
        return text;
    }
}