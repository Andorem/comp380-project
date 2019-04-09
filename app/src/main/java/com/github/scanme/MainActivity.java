package com.github.scanme;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

/*
import android.widget.Toast;
import android.widget.Button;
*/

public class MainActivity extends AppCompatActivity {

    ArrayList<QREntry> entriesData = new ArrayList<>();//
    EntriesListAdapter entriesAdapter;//
    ListView entriesList;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);//Andy: bottom up
        setSupportActionBar(toolbar);

        FloatingActionButton createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {//listener
            @Override
            public void onClick(View v){
                openCreateEntryActivity();
            }
        });

        FloatingActionButton scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openQRScanActivity();
            }
        });

        entriesList = findViewById(R.id.entriesList);
        entriesAdapter = new EntriesListAdapter(this, entriesData);
        entriesList.setAdapter(entriesAdapter);

        Intent intent = getIntent();
        if (intent != null) { // Activity called after entry created/edited
            Bundle entryData = intent.getExtras();
            if (entryData != null) {
                updateEntryList(new QREntry(entryData.getString("ENTRY_ID"), entryData.getString("ENTRY_TITLE"), entryData.getString("ENTRY_DESCRIPTION"), entryData.getString("ENTRY_IMAGEPATH")));
            }
        }
    }

    void updateEntryList(QREntry entry) {
        entriesData.add(entry);
        entriesAdapter = new EntriesListAdapter(this, entriesData);
        entriesList.setAdapter(entriesAdapter);
    }


    //This is the intent used for create button(main activity) ---> createEntryActivity.java
    public void openCreateEntryActivity(){
        Intent intent  = new Intent(this, CreateEntryActivity.class);
        startActivity(intent);
    }

    public void openQRScanActivity(){
        Intent intent = new Intent(this, QRScan.class);
        startActivity(intent);
    }
}
