package com.github.scanme;

import androidx.lifecycle.Observer;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.github.scanme.entrylist.EntryListAdapter;
import com.github.scanme.entrylist.SwipeToDeleteCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;


public class MainActivity extends AppCompatActivity{

    List<QR> entriesData;
    EntryListAdapter entriesAdapter;
    RecyclerView entriesList;

    boolean EDIT_MODE = false;
    List<QR> selectedQRs = new ArrayList<>();

    private QRRepository qrRepo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize listeners for main screen buttons
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
       entriesAdapter = new EntryListAdapter(this, entriesData);
       entriesList.setAdapter(entriesAdapter);

       // Handle RecyclerView operations
       entriesList.setLayoutManager(new LinearLayoutManager(this));
       ItemTouchHelper swipeHelper = new ItemTouchHelper(new SwipeToDeleteCallback(entriesAdapter));
       swipeHelper.attachToRecyclerView(entriesList);

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

    public void openViewEntryActivity(QR qr){
        Intent intent  = new Intent(this, ViewEntryActivity.class);
        intent.putExtra("QR", qr);
        startActivity(intent);
    }

    public void openQRScanActivity(){
        Intent intent  = new Intent(this, QRScan.class);
        startActivity(intent);
    }

    public void openQRPrintActivity(ArrayList<QR> selectedQRs){
      Intent intent  = new Intent(this, QRPrint.class);
        intent.putExtra("QRs", selectedQRs);
        startActivity(intent);
    }

    /* Main Toolbar Menu */

    // Inflate the toolbar with the custom menu layout and retrieve icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Determine what to do when a menu icon is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.printButton:
                if (EDIT_MODE) openQRPrintActivity((ArrayList<QR>) entriesAdapter.getSelectedQRs());
                item.setIcon(EDIT_MODE ? R.drawable.ic_print : R.drawable.ic_checkmark);
                toggleEditMode();
                break;
            case R.id.scanButton:
                // openQRScanActivity();
                Toast.makeText(this, "Scan clicked!", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleEditMode() {
        EDIT_MODE = !EDIT_MODE;
        entriesAdapter.toggleSelectMode();
    }

}
