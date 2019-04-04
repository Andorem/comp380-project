package com.github.scanme;

import androidx.lifecycle.Observer;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;

/*
import android.widget.Toast;
import android.widget.Button;
*/

public class MainActivity extends AppCompatActivity {

    List<QR> entriesData;
    EntriesListAdapter entriesAdapter;
    ListView entriesList;

    private QRRepository qrRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openCreateEntryActivity();
            }
        });

        // Populate list with custom layouts (adapter) for each entry (image, title, description, etc...)
        entriesData = new ArrayList<>();
        entriesList = findViewById(R.id.entriesList);
        entriesAdapter = new EntriesListAdapter(this, entriesData);
        entriesList.setAdapter(entriesAdapter);
        entriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openViewEntryActivity(entriesAdapter.getItem(position).getId());
            }
        });

        // Update the cached copy of the entries in the adapter
        qrRepo = new QRRepository(getApplication());
        qrRepo.getAllQRs().observe(this, new Observer<List<QR>>() {
            @Override
            public void onChanged(@Nullable final List<QR> QRs) {
                entriesAdapter.updateEntries(QRs);
            }
        });
    }

    // This is the intent used for create button(main activity) ---> createEntryActivity.java
    public void openCreateEntryActivity(){
        Intent intent  = new Intent(this, CreateEntryActivity.class);
        startActivity(intent);
    }

    public void openViewEntryActivity(String id){
        Intent intent  = new Intent(this, CreateEntryActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }
}
