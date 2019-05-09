package com.github.scanme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.github.scanme.entrylist.EntryListAdapter;
import com.github.scanme.entrylist.SwipeToDeleteCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;


public class MainActivity extends AppCompatActivity{

    boolean DEBUG = false;

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

        //first time boolean check
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if(isFirstRun) {
            // Create welcome entry
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.raw.logo);
            BitmapHandler.saveToFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), "logo.png", bm);
            qrRepo.insert(new QR("logo", "Welcome to ScanMe!", getResources().getString(R.string.welcome),
                    "other", getExternalFilesDir(Environment.DIRECTORY_PICTURES) +"/logo.png", getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() +"logo.png"));

            //do something
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setCancelable(true);
            builder.setTitle("Welcome");
            builder.setMessage("Would you like a tutorial?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            //Toast.makeText(MainActivity.this, "Confirm check",Toast.LENGTH_SHORT).show();

                            //start activity
                            startActivity(new Intent(MainActivity.this, Tutorial.class));

                        }
        });
            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            //Toast.makeText(MainActivity.this, "Delete check",Toast.LENGTH_SHORT).show();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();

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

        // Debug
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("debugMode", DEBUG).apply();
        if (DEBUG) {
            invalidateOptionsMenu();
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

    public void openTutorialActivity(){
        Intent intent = new Intent(this, Tutorial.class);
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

        // Show/hide debug item
        MenuItem debugItem = menu.findItem(R.id.debugButton);
        if (!DEBUG) debugItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    // Determine what to do when a menu icon is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.printButton:
                if (EDIT_MODE && !entriesAdapter.isSelectedQRsEmpty()) {
                    QRPrint qrPrint = new QRPrint(this, entriesAdapter.getSelectedQRs());
                    String temp = "";
                    for (QR qr : entriesAdapter.getSelectedQRs()) temp += qr.getTitle() + ", ";
                    Log.d("MAIN", "adapter QRs: " + temp);
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
            case R.id.tutorialButton:
                openTutorialActivity();
                break;
            case R.id.debugButton:
                populateDatabase();
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
            else {
                Toast.makeText(MainActivity.this, "QR not recognized!",Toast.LENGTH_SHORT).show();
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

    // For debugging: Create new placeholder QR objects and put in database
    private void populateDatabase() {
        createTestImages();

        String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/IMG_test";
        String qrPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/QRs/QR_test";

        qrRepo.insert(new QR("test1", "Kid Toys", "Action figures, superhero masks, children's books, boardgames",
                "bedroom", imagePath + "1.png", qrPath + "1.png"));
        qrRepo.insert(new QR("test2", "School Supplies", "Hole puncher, notecards, notebooks, magnets, erasers, keyrings, stapler, laptop charger and printer connector",
                "other", imagePath + "2.png", qrPath + "2.png"));
        qrRepo.insert(new QR("test3", "Bathroom Stuff", "Pairs of flipflops, bars of soap, conditioner and shampoo, body lotion, medication (aspirin, ibuprofen)",
                "bathroom", imagePath + "3.png", qrPath + "3.png"));
        qrRepo.insert(new QR("test4", "Kitchen Utensils", "Plates and bowls and stuff, eating mats, knives, and some pot covers",
                "kitchen", imagePath + "4.png", qrPath + "4.png"));
    }

    // For debugging: create and store placeholder images in external storage
    public void createTestImages() {
        String dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
       // if (!assetsDir.exists()) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.raw.logo);
            BitmapHandler.saveToFile(dir, "logo.png", bm);

            int[] qrIDs = {R.raw.testqr1, R.raw.testqr2, R.raw.testqr3, R.raw.testqr4};
            int[] imageIDs = {R.raw.testbox1, R.raw.testbox2, R.raw.testbox3, R.raw.testbox4};
            for (int i = 0; i < imageIDs.length; i++) {

                // Create dummy QR codes
                String path = dir + "/QRs/QR_test" + (i + 1) + ".png";
                File test = new File(path);
                if (!test.exists()) {
                    bm = BitmapFactory.decodeResource(getResources(), qrIDs[i]);
                    BitmapHandler.saveToFile(dir + "/QRs/", "QR_test" + (i + 1) + ".png", bm);
                }

                // Create dummy images
                path = dir + "/IMG_test" + (i + 1) + ".png";
                test = new File(path);
                if (!test.exists()) {
                    bm = BitmapFactory.decodeResource(getResources(), imageIDs[i]);
                    BitmapHandler.saveToFile(dir + "/", "IMG_test" + (i + 1) + ".png", bm);
                }
            }
      //  }
    }

}
