package com.github.scanme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;

import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//<<<<<<<<< Temporary merge branch 1

import java.io.File;
import java.util.ArrayList;
//=========
//>>>>>>>>> Temporary merge branch 2


public class ViewEntryActivity extends AppCompatActivity {
    //AlertDialog dialog;
    String ID;
    QR qr;
    boolean editMode = false;
    protected static final int PERMREQ_CAMERA = 1;
    Intent takePictureIntent;
    private File imageFile;
    Context context;

    //ImageView pictureOutput = new ImageView(getApplicationContext()); TEST
    ImageView pictureOutput;
    TextView titleOutput;

    TextView descriptionOutput;

    FloatingActionButton locationIcon;

    // QRRepository qrRepo = new QRRepository(getApplication());
     TextView editLabel;
     Button button;
     int option = 0;
     ArrayAdapter<CharSequence> adapter;
     QRRepository qrRepo;



    //onCreate method starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        context = this;

        qrRepo = new QRRepository(getApplication());

        pictureOutput = findViewById(R.id.entryPicture);
        titleOutput = findViewById(R.id.titleView);
        descriptionOutput = findViewById(R.id.descriptionView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //dialogTwo = new AlertDialog.Builder(this).create();




        adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // ID = getIntent().getStringExtra("ID");
        qr = getIntent().getParcelableExtra("QR");
        ID = qr.getID();

        toolbar.setTitle(qr.getTitle());
        setSupportActionBar(toolbar);


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

        locationIcon = qr.getLocationButton((FloatingActionButton) findViewById(R.id.locationIcon));

/*
        Log.d("VIEW_ENTRY", "ID retrieved: " + ID);
        qrRepo = new QRRepository(getApplication());
        qr = qrRepo.getQR(ID).getValue();*/

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
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        switch(item.getItemId()){
            case R.id.editTitle:
                final EditText titleEdit = new EditText(this);
                dialog.setTitle("Edit");
                //Toast.makeText(this, "editTitle", Toast.LENGTH_SHORT).show();
               // if (dialog.isShowing())
                   // dialog.dismiss();
               // titleEdit = new EditText(this);

                dialog.setView(titleEdit);
                //AlertDialog alertDialog = dialog.show();
                //edit and set new title
                titleEdit.setText(titleOutput.getText());
                dialog.setPositiveButton("SAVE EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        titleOutput.setText(titleEdit.getText());
                        qr.setTitle(titleEdit.getText().toString());
                        qrRepo.update(qr);
                        dialog.dismiss();
                        Toast.makeText(context, "Title edited!", Toast.LENGTH_SHORT).show();
                    }
                });
                //final AlertDialog dialog = dialog.show();
                dialog.show();
                break;
            case R.id.editDescription:
                    final EditText descriptionEdit = new EditText(this);
                    dialog.setTitle("Edit");
                    dialog.setView(descriptionEdit);
                    //edit and set new description
                    descriptionEdit.setText(descriptionOutput.getText());
                    dialog.setPositiveButton("SAVE EDIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            descriptionOutput.setText(descriptionEdit.getText());
                            qr.setDescription(descriptionEdit.getText().toString());
                            qrRepo.update(qr);
                            dialog.dismiss();
                            Toast.makeText(context, "Description edited!", Toast.LENGTH_SHORT).show();
                        }
                    });
                dialog.show();
                break;
            case R.id.editTag:
                final Spinner locationSpinner = new Spinner(this, Spinner.MODE_DIALOG);
                int p = this.getResources().getDimensionPixelSize(R.dimen.spinner_padding);
                locationSpinner.setPadding(p, p, p, p);
                locationSpinner.setAdapter(adapter);
                locationSpinner.setOnItemSelectedListener(spinnerListener);
                dialog.setTitle("Edit");
                final String oldLocation = qr.getLocation();
                dialog.setView(locationSpinner);
                //edit and set new description
                dialog.setPositiveButton("SAVE EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationIcon = qr.getLocationButton((FloatingActionButton) findViewById(R.id.locationIcon));
                        qrRepo.update(qr);
                        dialog.dismiss();
                        Toast.makeText(context, "Tag edited!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                qr.setLocation(oldLocation);
                            }
                        }
                );
                locationSpinner.setSelection(adapter.getPosition(qr.getLocation()), false);
                dialog.show();
                break;
            case R.id.editImage:
                takePicture(pictureOutput);
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
            qr.setLocation((String) parent.getItemAtPosition(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public void takePicture(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // No permission granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMREQ_CAMERA);
        }
        else {
            // Permission already been granted
            final int REQUEST_IMAGE_CAPTURE = 1;
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Call System Camera App
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImageURI());
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMREQ_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Granted
                    takePicture(pictureOutput);
                }
                else { // Denied
                }
                return;
            }
        }
    }



    @Override
    protected void onActivityResult(int request, int result, Intent intentData) {
        takePictureIntent = intentData;
        if (request == PERMREQ_CAMERA && result == RESULT_OK) {
            pictureOutput.setImageBitmap(BitmapHandler.rotateImage(this, qr.getImagePath()));
            qrRepo.update(qr);
            Toast.makeText(context, "Image edited!", Toast.LENGTH_SHORT).show();
        }
    }

    public Uri createImageURI() {
        imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_" + ID + ".png");
        Uri fileProvider = FileProvider.getUriForFile(ViewEntryActivity.this, "com.codepath.fileprovider", imageFile);

        return fileProvider;
    }

}// end class
