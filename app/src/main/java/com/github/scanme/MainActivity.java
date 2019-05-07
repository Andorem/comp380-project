package com.github.scanme;

import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.github.scanme.entrylist.EntryListAdapter;
import com.github.scanme.entrylist.SwipeToDeleteCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;


public class MainActivity extends AppCompatActivity{

    List<QR> entriesData;
    EntryListAdapter entriesAdapter;
    RecyclerView entriesList;

    SearchView searchView;

    boolean EDIT_MODE = false;
    List<QR> selectedQRs = new ArrayList<>();

    private QRRepository qrRepo;

    private final int REQUEST_CODE_SCAN = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoordinatorLayout wrapper = findViewById(R.id.wrapper);

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

        Intent intent = getIntent();
        if (intent != null) {
            String code = intent.getStringExtra("CODE");
            if (code != null) {
                switch(code) {
                    case "DELETE":
                        Toast.makeText(this, "Deleted QR Code!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }

    }

    // This is the intent used for create button(main activity) ---> createEntryActivity.java
    public void openCreateEntryActivity() {
        Intent intent  = new Intent(this, CreateEntryActivity.class);
        startActivity(intent);
    }

    public void openViewEntryActivity(QR qr){
        Intent intent  = new Intent(this, ViewEntryActivity.class);
        intent.putExtra("QR", qr);
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

        // Initiate search function
        searchView = (SearchView) menu.findItem(R.id.searchButton).getActionView();
        searchView.setOnQueryTextListener(searchListener);

        return super.onCreateOptionsMenu(menu);
    }

    // Determine what to do when a menu icon is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.printButton:
                if (EDIT_MODE && !entriesAdapter.isSelectedQRsEmpty()) {
                    QRPrint qrPrint = new QRPrint(this, entriesAdapter.getSelectedQRs());
                    qrPrint.printDocument(item.getActionView());
                }
                else {
                    entriesAdapter.clearSelectedQRs();
                }
                item.setIcon(EDIT_MODE ? R.drawable.ic_print : R.drawable.ic_checkmark);
                toggleEditMode();
                break;
            case R.id.scanButton:
                doScan();
                //Toast.makeText(this, "Scan clicked!", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleEditMode() {
        EDIT_MODE = !EDIT_MODE;
        entriesAdapter.toggleSelectMode();
    }

    /* Search Actions */
    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchEntries(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newQuery) {
                    searchEntries(newQuery);
                    return true;
                }

                private void searchEntries(String query) {
                    query = "%" + query + "%";
                    qrRepo.getQRs(query).observe(MainActivity.this, new Observer<List<QR>>() {
                                @Override
                                public void onChanged(@Nullable List<QR> QRs) {
                                    if (QRs == null) return;
                                    entriesAdapter.updateEntries(QRs);
                                }
                            });
                }
            };

    /*Scan Actions*/
    public void doScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // No permission granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_SCAN);
        }
        else {
            // Permission already been granted
            Intent intent = new Intent(this, CaptureActivity.class);
            if (intent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SCAN: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Granted
                    doScan();
                }
                else { // Denied
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,  resultCode, data);

        // 扫描二维码/条码回传scan qr/ return code
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                openViewEntryActivity(content);
            }
        }
    }

       public void openViewEntryActivity(String id) {
        // Grab QR from database
        Log.i("MAIN", "openViewEntryActivity from doScan: id = " + id);
        // Pass QR to its entry screen
        qrRepo.getQR(id).observe(this, new Observer<QR>() {
            @Override
            public void onChanged(@Nullable final QR qr) {
                if (qr == null) return;
                Intent intent = new Intent(MainActivity.this, ViewEntryActivity.class);
                intent.putExtra("QR", qr);
                startActivity(intent);
            }
        });
    }

}
