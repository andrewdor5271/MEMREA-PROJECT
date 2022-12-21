package org.mirea.pm.notes_frontend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import androidx.navigation.ui.AppBarConfiguration;

import org.mirea.pm.notes_frontend.adapters.NoteListAdapter;
import org.mirea.pm.notes_frontend.data_managers.NoteManager;
import org.mirea.pm.notes_frontend.datamodels.NoteModel;
import org.mirea.pm.notes_frontend.databinding.ActivityMainBinding;
import org.mirea.pm.notes_frontend.util.Constants;
import org.mirea.pm.notes_frontend.util_storage.JwtStorage;

import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NoteManager noteManager;
    private NoteListAdapter notesAdapter;
    private ArrayList<NoteModel> notesList = new ArrayList<>();

    private org.mirea.pm.notes_frontend.databinding.ActivityMainBinding binding;
    private Menu menu;

    private NoteModel noteInEdit = null;

    public void authActivity() {
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(intent);
    }

    public void networkErrorToast() {
        Toast.makeText(this, getString(R.string.default_network_error_message), Toast.LENGTH_LONG).show();
    }

    public void processAuthResponse(InputStream stream) throws IOException {
        InputStreamReader reader = new InputStreamReader(
                stream, StandardCharsets.UTF_8);
        JsonReader jsonReader = new JsonReader(reader);

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            if(jsonReader.nextName().equals("token")) {
                String token = jsonReader.nextString();

                JwtStorage.save(this, token);
            }
            else {
                jsonReader.skipValue();
            }
        }
    }

    public void updateAuth() {
        String oldJwt = JwtStorage.retrieve(this);
        if (oldJwt.isEmpty()) {
            authActivity();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String protocol = getString(R.string.protocol);
            String socket = getString(R.string.socket);
            try {
                URL url = new URL(getString(R.string.url_extend_token, protocol, socket));

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + oldJwt);

                if(connection.getResponseCode() == 200) {
                    processAuthResponse(connection.getInputStream());
                }
                else if(connection.getResponseCode() == 401) {
                    runOnUiThread(this::authActivity);
                }
                else {
                    networkErrorToast();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void userDetailsDialog() {
        UserDialogFragment dialogFragment = UserDialogFragment.newInstance(getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                .getString(Constants.USERNAME_PREF_NAME, ""));
        dialogFragment.setLogoutCallback(noteManager::logoutHandler);
        dialogFragment.show(getSupportFragmentManager(), UserDialogFragment.TAG);
    }

    // call when you need to modify existing note
    public void startNoteEdit(NoteModel note) {
        noteInEdit = note;
    }

    // call to add a new note or to modify existing
    public void finishNoteEdit(NoteModel edited) {
        if(noteInEdit != null) {
            edited.mongoId = noteInEdit.mongoId;
            edited.id = noteInEdit.id;
            notesAdapter.remove(noteInEdit);
            noteInEdit = null;
            noteManager.updateNote(edited);
        }
        else {
            noteManager.createNote(edited);
        }
        notesAdapter.add(edited);
    }

    public void deleteNote(NoteModel note) {
        notesAdapter.remove(note);
        noteManager.deleteNote(note);
    }

    private void cancelNoteEdit() {
        noteInEdit = null;
    }

    private Intent prepareNoteModelAsViewNoteIntent (NoteModel note)
    {
        Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
        intent.putExtra(ViewNoteActivity.NOTE_TEXT_PARAM_NAME, note.getText());
        intent.putExtra(ViewNoteActivity.INPUT_DATE_STR_PARAM_NAME, note.getCreationTimeString(getResources().getString(R.string.datetime_format)));
        return intent;
    }

    private void setLoading(boolean loading) {
        if(loading) {
            binding.notesList.setEnabled(false);
            binding.fab.setEnabled(false);
            binding.toolbar.setEnabled(false);
            for(int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setEnabled(false);
            }
            binding.mainActivityProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.notesList.setEnabled(true);
            binding.fab.setEnabled(true);
            binding.toolbar.setEnabled(true);
            binding.mainActivityProgressBar.setVisibility(View.GONE);
            for(int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setEnabled(true);
            }
        }
    }

    private void initNotesList() {
        Future<List<NoteModel>> data = noteManager.getLocalNotes();
        setLoading(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                List<NoteModel> notes = data.get();
                runOnUiThread(() -> {
                    notesAdapter = new NoteListAdapter(this, notes);
                    binding.notesList.setAdapter(notesAdapter);
                    setLoading(false);
                });

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // обработчик результата изменения заметки
        ActivityResultLauncher<Intent> noteEditActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Activity finished normally
                        Intent resultData = result.getData();
                        if(resultData != null && resultData.getBooleanExtra(
                                ViewNoteActivity.NOTE_EDITED_PARAM_NAME,
                                false)
                        ) {
                            // Processing output here
                            Date editDate =
                                    (Date) resultData.getSerializableExtra(ViewNoteActivity.OUTPUT_DATE_PARAM_NAME
                                    );
                            NoteModel resultNote = new NoteModel(
                                    resultData.getStringExtra(ViewNoteActivity.NOTE_TEXT_PARAM_NAME), editDate
                            );
                            finishNoteEdit(resultNote);
                        }
                        else {
                            cancelNoteEdit();
                        }
                    }
                });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        noteManager = new NoteManager(this);
        noteManager.setOnAuthErrorCallback(this::authActivity);
        noteManager.setOnGenericNetworkErrorCallback(this::networkErrorToast);
        noteManager.setOnUploadSyncReadyCallback(this::initNotesList);
        noteManager.setOnDownloadSyncReadyCallback(this::initNotesList);

        binding.notesList.setOnItemClickListener((parent, view, position, id) -> {
            NoteModel note = (NoteModel)parent.getAdapter().getItem(position);
            noteEditActivityResultLauncher.launch(prepareNoteModelAsViewNoteIntent(note));
            startNoteEdit(note);
        });

        binding.fab.setOnClickListener(view -> {
            NoteModel noteModel = new NoteModel("", new Date());
            noteEditActivityResultLauncher.launch(prepareNoteModelAsViewNoteIntent(noteModel));
        });

        updateAuth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu newMenu) {

        this.menu = newMenu;

        getMenuInflater().inflate(R.menu.menu_main, newMenu);

        MenuItem search = newMenu.findItem(R.id.action_search);
        SearchView sView = (SearchView) search.getActionView();

        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                notesAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notesAdapter.getFilter().filter(newText);
                return false;
            }
        });

        initNotesList();

        return super.onCreateOptionsMenu(newMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_user) {
            userDetailsDialog();
            return true;
        }
        else if(id == R.id.action_upload_sync) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.action_upload_sync))
                    .setMessage(getString(R.string.upload_sync_alert))
                    .setPositiveButton(R.string.yes, (dialog, which) -> noteManager.uploadSync())
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .show();
        }
        else if(id == R.id.action_download_sync) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.action_download_sync))
                    .setMessage(getString(R.string.download_sync_alert))
                    .setPositiveButton(R.string.yes, (dialog, which) -> noteManager.downloadSync())
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    public NoteManager getNoteManager() {
        return noteManager;
    }
}