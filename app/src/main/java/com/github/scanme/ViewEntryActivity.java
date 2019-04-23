package com.github.scanme;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.github.scanme.database.QR;
import com.github.scanme.database.QRRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;
import android.widget.Toast;


public class ViewEntryActivity extends AppCompatActivity {

    String ID;
    QR qr;

    //ImageView pictureOutput = new ImageView(getApplicationContext()); TEST
    ImageView pictureOutput;
    TextView titleOutput;
    TextView descriptionOutput;
    // QRRepository qrRepo = new QRRepository(getApplication());

    //onCreate method starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);
        pictureOutput = findViewById(R.id.entryPicture);
        titleOutput = findViewById(R.id.titleView);
        descriptionOutput = findViewById(R.id.descriptionView);
        Toolbar toolbar = findViewById(R.id.toolbar);


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


        //setter
        titleOutput.setText(qr.getTitle());
        //titleOutput = findViewById(R.id.titleView);

        descriptionOutput.setText(qr.getDescription());
        //descriptionOutput = findViewById(R.id.descriptionView);

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
        switch(item.getItemId()){
            case R.id.Edit:
                Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Delete:
                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Print:
                Toast.makeText(this, "Print", Toast.LENGTH_SHORT).show();
                break;
        }
        //return true;
        return super.onOptionsItemSelected(item);
    }













    //gets image w. bitmap
    protected void getImage(String filePath){
        pictureOutput.setImageBitmap(BitmapHandler.rotateImage(this, filePath));
    }
}// end class
