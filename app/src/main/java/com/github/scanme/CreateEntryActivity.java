package com.github.scanme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.UUID;

public class CreateEntryActivity extends AppCompatActivity {

    protected static final int PERMREQ_CAMERA = 1;

    private RelativeLayout cameraBackground;
    private FloatingActionButton cameraButton;
    private File imageFile;
    private ImageView entryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_entry);

        cameraBackground = findViewById(R.id.cameraBackground);
        cameraButton = findViewById(R.id.cameraButton);
        entryImage = findViewById(R.id.entryImage);
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

    public void saveEntry(View view) {

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
            Bitmap resImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            entryImage.setImageBitmap(resImage);
            entryImage.setVisibility(View.VISIBLE);
            cameraButton.setVisibility(View.GONE);
        }
    }

    public Uri createImageURI() {
        imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_" + UUID.randomUUID().toString() + ".png");
        Uri fileProvider = FileProvider.getUriForFile(CreateEntryActivity.this, "com.codepath.fileprovider", imageFile);
        return fileProvider;
    }

}
