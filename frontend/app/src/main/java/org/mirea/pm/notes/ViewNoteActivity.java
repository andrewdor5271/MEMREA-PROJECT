package org.mirea.pm.notes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;

import org.mirea.pm.notes.databinding.ActivityViewNoteBinding;

import java.util.Date;

public class ViewNoteActivity extends AppCompatActivity {

    public static final String NOTE_TEXT_PARAM_NAME = "Text";
    public static final String INPUT_DATE_STR_PARAM_NAME = "DateStr";
    public static final String NOTE_EDITED_PARAM_NAME = "Changed";
    public static final String OUTPUT_DATE_PARAM_NAME = "DateLong";

    private org.mirea.pm.notes.databinding.ActivityViewNoteBinding binding;
    private Boolean edited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityViewNoteBinding.inflate(getLayoutInflater());

        binding.noteTextEdit.setText(getIntent().getStringExtra(NOTE_TEXT_PARAM_NAME));
        binding.creationDatetimeText.setText(getIntent().getStringExtra(INPUT_DATE_STR_PARAM_NAME));

        binding.noteTextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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

    @Override
    public void finish()
    {
        Intent result = new Intent();
        result.putExtra(NOTE_EDITED_PARAM_NAME, edited);
        if(edited) {
            result.putExtra(NOTE_TEXT_PARAM_NAME, binding.noteTextEdit.getText().toString());
            result.putExtra(OUTPUT_DATE_PARAM_NAME, new Date());
        }
        setResult(RESULT_OK, result);
        super.finish();
    }
}