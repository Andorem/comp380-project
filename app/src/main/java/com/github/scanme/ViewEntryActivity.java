package com.github.scanme;

import android.os.Bundle;

import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

public class ViewEntryActivity extends AppCompatActivity {

    String ID;
    QR qr;
    QRRepository qrRepo = new QRRepository(getApplication());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        qr = getIntent().getParcelableExtra("QR");
        ID = qr.getId();
        Log.d("VIEW_ENTRY", "ID retrieved: " + ID);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "ID: " + ID, Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

}
