package com.github.scanme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//<<<<<<<<< Temporary merge branch 1

import java.util.ArrayList;
//=========
//>>>>>>>>> Temporary merge branch 2


public class ViewEntryActivity extends AppCompatActivity {
    //AlertDialog dialog;
    String ID;
    QR qr;
    boolean editMode = false;

    //ImageView pictureOutput = new ImageView(getApplicationContext()); TEST
    ImageView pictureOutput;
    TextView titleOutput;
    EditText titleEdit;
    TextView descriptionOutput;
    EditText descriptionEdit;
    Spinner locationSpinner;
    FloatingActionButton locationIcon;
    // QRRepository qrRepo = new QRRepository(getApplication());
     TextView editLabel;
     Button button;
     int option = 0;

     QRRepository qrRepo;

    //onCreate method starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        qrRepo = new QRRepository(getApplication());

        pictureOutput = findViewById(R.id.entryPicture);
        titleOutput = findViewById(R.id.titleView);
        descriptionOutput = findViewById(R.id.descriptionView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //dialogTwo = new AlertDialog.Builder(this).create();
        titleEdit = new EditText(this);
        descriptionEdit = new EditText(this);
        final Spinner locationSpinner = new Spinner(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);



        // ID = getIntent().getStringExtra("ID");
        qr = getIntent().getParcelableExtra("QR");
        ID = qr.getID();

        toolbar.setTitle(qr.getTitle());
        setSupportActionBar(toolbar);



       // locationSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        /*
        //TEST TEST TEST TEST
        Bitmap bitmap = (Bitmap)this.getIntent().getParcelableExtra("Bitmap");
        ImageView pictureOutput = (ImageView) findViewById(R.id.entryPicture);
        */

        // get image
        getImage(qr.getImagePath());
       // pictureOutput = findViewById(R.id.entryPicture);


        //image stretch
        //pictureOutput.setScaleType(ImageView.ScaleType.FIT_XY);

        //setter
        titleOutput.setText(qr.getTitle());
        //titleOutput = findViewById(R.id.titleView);

        descriptionOutput.setText(qr.getDescription());
        //descriptionOutput = findViewById(R.id.descriptionView);


/*
        Log.d("VIEW_ENTRY", "ID retrieved: " + ID);
        qrRepo = new QRRepository(getApplication());
        qr = qrRepo.getQR(ID).getValue();*/

        //location
        locationIcon = qr.getLocationButton((FloatingActionButton) findViewById(R.id.locationIcon));


        FloatingActionButton fab = findViewById(R.id.entryQR);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRPopup qrPopup = new QRPopup();
                qrPopup.show(getSupportFragmentManager(), "QR Popup");
            }
        });
    } // end of on create method
    //toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_view_entry, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        AlertDialog dialog;
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Edit");
        switch(item.getItemId()){
            case R.id.editTitle:
                //Toast.makeText(this, "editTitle", Toast.LENGTH_SHORT).show();
               // if (dialog.isShowing())
                   // dialog.dismiss();
                dialog.setView(titleEdit);
                //AlertDialog alertDialog = dialog.show();
                //edit and set new title
                titleEdit.setText(titleOutput.getText());
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        titleOutput.setText(titleEdit.getText());
                        qr.setTitle(titleEdit.getText().toString());
                        qrRepo.update(qr);
                        dialog.dismiss();
                    }
                });
                //final AlertDialog dialog = dialog.show();
                dialog.show();
                break;
            case R.id.editDescription:

                    dialog.setView(descriptionEdit);
                    //edit and set new description
                    descriptionEdit.setText(descriptionOutput.getText());
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE EDIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            descriptionOutput.setText(descriptionEdit.getText());
                            qr.setDescription(descriptionEdit.getText().toString());
                            qrRepo.update(qr);
                        }
                    });
                dialog.show();
                break;
            case R.id.editTag:
                //enter edit tag feature here
                dialog.setView(locationSpinner);
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE TAG", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationSpinner.setOnItemSelectedListener(spinnerListener);
                    }
                });

                break;
            case R.id.Delete:
                qrRepo.delete(qr);
                openMainActivity();
                break;
            case R.id.Print:
                QRPopup.printQR(this, qr);
                break;
        }
        //return true;
        return super.onOptionsItemSelected(item);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("CODE", "DELETE");
        startActivity(intent);
    }


    //gets image w. bitmap
    protected void getImage(String filePath){
        pictureOutput.setImageBitmap(BitmapHandler.rotateImage(this, filePath));
    }


    /* Spinner Listener */
    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           String location = (String) parent.getItemAtPosition(position);
            qr.setLocation(location);
            qrRepo.update(qr);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
}// end class
