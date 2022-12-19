package org.mirea.pm.notes_frontend.data_managers;

import android.app.Activity;
import android.util.JsonReader;

import org.mirea.pm.notes_frontend.App;
import org.mirea.pm.notes_frontend.R;
import org.mirea.pm.notes_frontend.database.AppDatabase;
import org.mirea.pm.notes_frontend.datamodels.NoteModel;
import org.mirea.pm.notes_frontend.util_storage.JwtStorage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteManager {
    private final Activity activity;
    private Runnable onReadyCallback;
    private Runnable onAuthErrorCallback;
    private Runnable onGenericNetworkErrorCallback;
    private List<NoteModel> notes;
    private final AppDatabase database;

    public NoteManager(Activity core) {
        this.activity = core;
        database = App.getInstance().getDatabase();
        notes = database.noteDao().getAll();
    }

    public List<NoteModel> getNotes() {
        return new LinkedList<>(notes);
    }

    public void setOnReadyCallback(Runnable callback) {
        this.onReadyCallback = callback;
    }

    public void setOnAuthErrorCallback(Runnable callback) {
        this.onAuthErrorCallback = callback;
    }

    public void setOnGenericNetworkErrorCallback(Runnable callback) {
        this.onGenericNetworkErrorCallback = callback;
    }

    public void synchronise(NoteModel note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try{
                String protocol = activity.getString(R.string.protocol);
                String socket = activity.getString(R.string.socket);
                URL url = new URL(activity.getString(R.string.url_create_note, protocol, socket));

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + JwtStorage.retrieve(activity));

                if(connection.getResponseCode() == 200) {
                    InputStreamReader reader = new InputStreamReader(
                            connection.getInputStream(), StandardCharsets.UTF_8);
                    JsonReader jsonReader = new JsonReader(reader);

                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        if (jsonReader.nextName().equals("id")) {
                            String id = jsonReader.nextString();

                            note.setMongoId(id);
                        }
                        else {
                            jsonReader.skipValue();
                        }
                    }
                }
                else if(connection.getResponseCode() == 401) {
                    if(onAuthErrorCallback != null) {
                        activity.runOnUiThread(onAuthErrorCallback);
                    }
                }
                else {
                    if(onGenericNetworkErrorCallback != null) {
                        activity.runOnUiThread(onGenericNetworkErrorCallback);
                    }
                }

                database.noteDao().insert(note);
                notes.add(note);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void createNote(NoteModel note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database.noteDao().insert(note);
            notes.add(note);
        });
    }

    public void updateNote(NoteModel note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database.noteDao().update(note);
        });
    }

    public void deleteNote(NoteModel note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database.noteDao().delete(note);
        });
    }
}
