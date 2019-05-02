package com.github.scanme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CreateEntryActivity extends AppCompatActivity {

    protected static final int PERMREQ_CAMERA = 1;
    private boolean ENTRY_CREATED;

    private QRRepository qrRepo = new QRRepository(getApplication());
    private genQR qrGenerator;

    // UI elements
    private RelativeLayout cameraBackground;
    private FloatingActionButton cameraButton;
    private ImageView entryImage;
    private EditText editTitle, editDescription;
    private Spinner locationSpinner;

    // QR elements
    private File imageFile;
    private String ID, imagePath = "", qrPath = "", location = "";
    private Bitmap qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_entry);

        ENTRY_CREATED = false;

        cameraBackground = findViewById(R.id.cameraBackground);
        cameraButton = findViewById(R.id.cameraButton);
        entryImage = findViewById(R.id.entryImage);
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);

        // Location spinner creation
        locationSpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);
        locationSpinner.setOnItemSelectedListener(spinnerListener);

        ID = UUID.randomUUID().toString();
        qrPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/QRs" + "/QR_" + ID + ".png";

       qrGenerator = new genQR(this);
       qrGenerator.encode(ID);
    }

    public void takePicture(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // No permission granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMREQ_CAMERA);
        }
        else {
            // Permission already been granted
            final int REQUEST_IMAGE_CAPTURE = 1;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Call System Camera App
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
                    takePicture(cameraButton);
                }
                else { // Denied
                }
                return;
            }
        }
    }



    @Override
    protected void onActivityResult(int request, int result, Intent intentData) {
        if (request == PERMREQ_CAMERA && result == RESULT_OK) {
            entryImage.setImageBitmap(BitmapHandler.rotateImage(this, imageFile.getAbsolutePath()));
            entryImage.setVisibility(View.VISIBLE);
            cameraButton.setVisibility(View.GONE);
        }
    }

    public Uri createImageURI() {
        imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_" + ID + ".png");
        Uri fileProvider = FileProvider.getUriForFile(CreateEntryActivity.this, "com.codepath.fileprovider", imageFile);
        return fileProvider;
    }

    public void saveEntry(View view) {
        ENTRY_CREATED = true;
        if (imageFile == null || !imageFile.exists()) {
            takePicture(cameraButton);
            return;
        }
        else {
            QR newQR = new QR(ID, editTitle.getText().toString(), editDescription.getText().toString(), location, imageFile.getAbsolutePath(), qrPath);
            qrRepo.insert(newQR);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    /* Spinner Listener */
    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            location = (String) parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /* Alert Dialog */
    private void showAlert( String alert) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle( "ERROR" )
                .setMessage(alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();
    }

    // Handle image deletion in case user aborts entry creation
    @Override
    public void onStop() {
        if (!ENTRY_CREATED) {
            File file = new File(qrPath);
            if (file.exists()) file.delete();

            file = new File(imagePath);
            if (file.exists()) file.delete();
        }
        super.onStop();
    }

}
