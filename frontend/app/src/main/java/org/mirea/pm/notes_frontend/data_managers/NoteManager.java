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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class NoteManager {
    public final String INTERNAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private final Activity activity;
    private Runnable onReadyCallback;
    private Runnable onAuthErrorCallback;
    private Runnable onGenericNetworkErrorCallback;
    private final AppDatabase database;

    public NoteManager(Activity core) {
        this.activity = core;
        database = App.getInstance().getDatabase();
    }

    public Future<List<NoteModel>> getLocalNotes() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return executor.submit(() -> database.noteDao().getAll());
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

    public void uploadSync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try{
                String protocol = activity.getString(R.string.protocol);
                String socket = activity.getString(R.string.socket);
                URL url = new URL(activity.getString(R.string.url_upload_sync, protocol, socket));

                List<NoteModel> allNotes = database.noteDao().getAll();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + JwtStorage.retrieve(activity));
                connection.setDoOutput(true);

                StringBuilder jsonBuilder = new StringBuilder("[");

                SimpleDateFormat format = new SimpleDateFormat(INTERNAL_DATE_TIME_FORMAT, Locale.getDefault());
                for (NoteModel elem : allNotes) {
                    String dateStr = format.format(elem.getUpdateDate());
                    jsonBuilder
                            .append("{\"id\":\"")
                            .append(elem.getMongoId())
                            .append("\",\"text\":\"")
                            .append(elem.getText())
                            .append("\",\"updateTime\":\"")
                            .append(dateStr)
                            .append("},");
                }
                jsonBuilder.setLength(jsonBuilder.length() - 1);
                jsonBuilder.append("}");

                try(OutputStream ostream = connection.getOutputStream()) {
                    byte[] input = jsonBuilder.toString().getBytes(StandardCharsets.UTF_8);
                    ostream.write(input, 0, input.length);
                }

                if(connection.getResponseCode() == 200) {
                    database.noteDao().deleteWhereMongoIdIsEmpty();
                    InputStreamReader reader = new InputStreamReader(
                            connection.getInputStream(), StandardCharsets.UTF_8);
                    JsonReader jsonReader = new JsonReader(reader);

                    while (jsonReader.hasNext()) {

                        jsonReader.beginObject();

                        String text = "";
                        String mongoId = "";
                        Date updateDate = new Date();
                        while (jsonReader.hasNext()) {
                            switch (jsonReader.nextName()) {
                                case "id":
                                    mongoId = jsonReader.nextString();
                                    break;
                                case "text":
                                    text = jsonReader.nextString();
                                    break;
                                case "updateTime":
                                    updateDate = format.parse(jsonReader.nextString());
                                    break;
                            }
                            NoteModel newNote = new NoteModel(text, updateDate);
                            newNote.setMongoId(mongoId);
                            database.noteDao().insert(newNote);
                        }
                        jsonReader.endObject();
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
            }
            catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
    }

    public void downloadSync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try{
                String protocol = activity.getString(R.string.protocol);
                String socket = activity.getString(R.string.socket);
                URL url = new URL(activity.getString(R.string.url_all_notes, protocol, socket));

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + JwtStorage.retrieve(activity));

                SimpleDateFormat format = new SimpleDateFormat(INTERNAL_DATE_TIME_FORMAT, Locale.getDefault());
                if(connection.getResponseCode() == 200) {
                    database.noteDao().clear();
                    InputStreamReader reader = new InputStreamReader(
                            connection.getInputStream(), StandardCharsets.UTF_8);
                    JsonReader jsonReader = new JsonReader(reader);


                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {

                        jsonReader.beginObject();

                        String text = "";
                        String mongoId = "";
                        Date updateDate = new Date();
                        while (jsonReader.hasNext()) {
                            switch (jsonReader.nextName()) {
                                case "id":
                                    mongoId = jsonReader.nextString();
                                    break;
                                case "text":
                                    text = jsonReader.nextString();
                                    break;
                                case "updateTime":
                                    updateDate = format.parse(jsonReader.nextString());
                                    break;
                            }
                            NoteModel newNote = new NoteModel(text, updateDate);
                            newNote.setMongoId(mongoId);
                            database.noteDao().insert(newNote);
                        }
                        jsonReader.endObject();
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
            }
            catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
    }

    public void createNote(NoteModel note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> database.noteDao().insert(note));
    }

    public void updateNote(NoteModel note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> database.noteDao().update(note));
    }

    public void deleteNote(NoteModel note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> database.noteDao().delete(note));
    }
}
