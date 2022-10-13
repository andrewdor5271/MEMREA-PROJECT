package org.mirea.pm.notes;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;


import androidx.navigation.ui.AppBarConfiguration;

import org.mirea.pm.notes.adapters.NoteListAdapter;
import org.mirea.pm.notes.adapters.NoteModel;
import org.mirea.pm.notes.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NoteListAdapter notesAdapter;
    private ArrayList<NoteModel> notesList = new ArrayList<>();
    private org.mirea.pm.notes.databinding.ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
            NoteModel note = (NoteModel)parent.getAdapter().getItem(position);
            intent.putExtra(ViewNoteActivity.NOTE_TEXT_PARAM_NAME, note.getText());
            intent.putExtra(ViewNoteActivity.DATE_STR_PARAM_NAME, note.getCreationTimeString(getResources().getString(R.string.datetime_format)));
            startActivity(intent);
        });

        binding.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView sView = (SearchView) search.getActionView();

        sView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                notesAdapter = new NoteListAdapter(
                        MainActivity.this,
                        new ArrayList<>(notesList)
                );
                binding.notesList.setAdapter(notesAdapter);
            }
        });

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