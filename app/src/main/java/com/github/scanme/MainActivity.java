package com.github.scanme;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

//ysi,0416
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.TextView;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    List<QR> entriesData;
    EntryListAdapter entriesAdapter;
    RecyclerView entriesList;

    boolean EDIT_MODE = false;
    List<QR> selectedQRs = new ArrayList<>();

    private QRRepository qrRepo;
    //ysi,0416
    private Button scanBtn;
    private TextView result;
    private Toolbar toolbar;
    private final int REQUEST_CODE_SCAN = 111;


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

        //ysi,0416
        initView();
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
     /* Intent intent  = new Intent(this, QRPrint.class);
        intent.putExtra("QRs", selectedQRs);
        startActivity(intent);   */
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

    //ysi,0416
    private void initView() {
        /*scan button*/
        scanBtn = findViewById(R.id.scanButton);
        scanBtn.setOnClickListener(this);
        /*scan result*/
        result = findViewById(R.id.resultTv);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("scan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void doScan(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_SCAN);
        }
        else{
            //permission already been granted
            Intent intent = new Intent(this, CaptureActivity.class);
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        }
    }

    @Override
    public void onClick(View v) {
        doScan();
    }

    @Override
    //new add by Alexis
    public  void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case REQUEST_CODE_SCAN: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //granted
                    doScan();
                }
                else{
                    //denied, do nothing
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传scan qr/ return code
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                result.setText("result of scan" + content);
            }
        }
    }
}
