package org.mirea.pm.notes_frontend.data_managers;

import android.app.Activity;
import android.util.JsonReader;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class NoteManager {
    public final String INTERNAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm";

    private final Activity activity;
    private Runnable onUploadSyncReadyCallback;
    private Runnable onDownloadSyncReadyCallback;
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

    public void setOnUploadSyncReadyCallback(Runnable onUploadSyncReadyCallback) {
        this.onUploadSyncReadyCallback = onUploadSyncReadyCallback;
    }

    public void setOnDownloadSyncReadyCallback(Runnable onDownloadSyncReadyCallback) {
        this.onDownloadSyncReadyCallback = onDownloadSyncReadyCallback;
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

                StringBuilder jsonBuilder = new StringBuilder("{\"notes\":[");

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
                            .append("\"},");
                }
                jsonBuilder.setLength(jsonBuilder.length() - 1);
                jsonBuilder.append("]}");

                String json = jsonBuilder.toString();

                try(OutputStream ostream = connection.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    ostream.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if(code == 200) {
                    database.noteDao().deleteWhereMongoIdIsEmpty();

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.setDateFormat(format);

                    Map<String, Object> parsed = mapper.readValue(connection.getInputStream(), Map.class);

                    List<Map<String, Object>> notes =
                            (ArrayList<Map<String, Object>>) parsed.get("notes");

                    assert notes != null;
                    for(Map<String, Object> elem : notes) {
                        NoteModel newNote = new NoteModel(
                                (String) elem.get("text"),
                                format.parse((String) elem.get("updateTime"))
                        );
                        newNote.setMongoId((String) elem.get("id"));
                        database.noteDao().insert(newNote);
                    }

                    if(onUploadSyncReadyCallback != null) {
                        activity.runOnUiThread(onUploadSyncReadyCallback);
                    }
                }
                else if(code == 401) {
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
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + JwtStorage.retrieve(activity));

                SimpleDateFormat format = new SimpleDateFormat(INTERNAL_DATE_TIME_FORMAT, Locale.getDefault());
                int code = connection.getResponseCode();
                if(code == 200) {
                    database.noteDao().clear();

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.setDateFormat(format);

                    Map<String, Object> parsed = mapper.readValue(connection.getInputStream(), Map.class);

                    List<Map<String, Object>> notes =
                            (ArrayList<Map<String, Object>>) parsed.get("notes");

                    assert notes != null;
                    for(Map<String, Object> elem : notes) {
                        NoteModel newNote = new NoteModel(
                                (String) elem.get("text"),
                                format.parse((String) elem.get("updateTime"))
                        );
                        newNote.setMongoId((String) elem.get("id"));
                        database.noteDao().insert(newNote);
                    }

                    if(onUploadSyncReadyCallback != null) {
                        activity.runOnUiThread(onUploadSyncReadyCallback);
                    }
                }
                else if(code == 401) {
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
