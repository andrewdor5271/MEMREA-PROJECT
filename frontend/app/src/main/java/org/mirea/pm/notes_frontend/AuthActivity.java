package org.mirea.pm.notes_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.no_go_back_auth), Toast.LENGTH_LONG).show();
    }
}