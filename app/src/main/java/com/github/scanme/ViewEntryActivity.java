package com.github.scanme;

import android.os.Bundle;

import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ViewEntryActivity extends AppCompatActivity {

    String ID;
    QR qr;
    ImageView entryPicture;
    QRRepository qrRepo = new QRRepository(getApplication());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getImage(qr.getImagePath());
        //ID = getIntent().getStringExtra("ID");
        qr = getIntent().getParcelableExtra("QR");
        ID = qr.getId();
        entryPicture = findViewById(R.id.entryPicture);


        Log.d("VIEW_ENTRY", "ID retrieved: " + ID);
        //qrRepo = new QRRepository(getApplication());
        //qr = qrRepo.getQR(ID).getValue();

        FloatingActionButton fab = findViewById(R.id.entryQR);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "ID: " + ID, Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }



    protected void getImage( String filePath){
        entryPicture.setImageBitmap(BitmapHandler.rotateImage(this, filePath));
    }

}
