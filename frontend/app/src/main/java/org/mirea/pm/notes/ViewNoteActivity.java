package org.mirea.pm.notes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import org.mirea.pm.notes.databinding.ActivityViewNoteBinding;

public class ViewNoteActivity extends AppCompatActivity {

    public static final String NOTE_TEXT_PARAM_NAME = "Text";
    public static final String DATE_STR_PARAM_NAME = "DateStr";

    private org.mirea.pm.notes.databinding.ActivityViewNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityViewNoteBinding.inflate(getLayoutInflater());

        binding.noteTextEdit.setText(getIntent().getStringExtra(NOTE_TEXT_PARAM_NAME));
        binding.creationDatetimeText.setText(getIntent().getStringExtra(DATE_STR_PARAM_NAME));

        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}