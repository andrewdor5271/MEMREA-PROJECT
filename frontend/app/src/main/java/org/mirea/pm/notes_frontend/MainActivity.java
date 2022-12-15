package org.mirea.pm.notes_frontend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import androidx.navigation.ui.AppBarConfiguration;

import org.mirea.pm.notes_frontend.adapters.NoteListAdapter;
import org.mirea.pm.notes_frontend.adapters.NoteModel;
import org.mirea.pm.notes_frontend.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NoteListAdapter notesAdapter;
    private ArrayList<NoteModel> notesList = new ArrayList<>();
    private org.mirea.pm.notes_frontend.databinding.ActivityMainBinding binding;
    private NoteModel noteInEdit = null;

    // call when you need to modify existing note
    private void startNoteEdit(NoteModel note) {
        noteInEdit = note;
    }

    // call to add a new note or to modify existing
    private void finishNoteEdit(NoteModel edited) {
        notesAdapter.add(edited);
        if(noteInEdit != null) {
            notesAdapter.remove(noteInEdit);
            noteInEdit = null;
        }
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

        final ArrayList<NoteModel> list = new ArrayList<>();
        for (int i = 0; i < 20; i ++) {
            list.add(new NoteModel("Sample " + i, new Date()));
        }

        notesList = new ArrayList<>(list);

        notesAdapter = new NoteListAdapter(
                this,
                new ArrayList<>(notesList)
        );
        binding.notesList.setAdapter(notesAdapter);

        binding.notesList.setOnItemClickListener((parent, view, position, id) -> {
            NoteModel note = (NoteModel)parent.getAdapter().getItem(position);
            noteEditActivityResultLauncher.launch(prepareNoteModelAsViewNoteIntent(note));
            startNoteEdit(note);
        });

        binding.fab.setOnClickListener(view -> {
            NoteModel note = new NoteModel("", new Date());
            noteEditActivityResultLauncher.launch(prepareNoteModelAsViewNoteIntent(note));
        });

        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.action_search);
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}