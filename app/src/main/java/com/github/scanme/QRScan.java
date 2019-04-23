package com.github.scanme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;

import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.github.scanme.CreateEntryActivity.PERMREQ_CAMERA;

//click the scan button, take it into the system camera
//scan the QR
//get info--link to the entry, get the info
public class QRScan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
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
            // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImageURI());
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void openViewEntryActivity(String id) {
        // Grab QR from database
        QRRepository qrRepo = new QRRepository(getApplication());
        QR qr = qrRepo.getQR(id).getValue();

        // Pass QR to its entry screen
        Intent intent = new Intent(this, ViewEntryActivity.class);
        intent.putExtra("QR", qr);
        startActivity(intent);
    }
}
