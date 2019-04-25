package com.github.scanme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import android.widget.TextView;

import java.util.ArrayList;
import android.widget.Toast;


public class ViewEntryActivity extends AppCompatActivity {
    AlertDialog dialog;
    String ID;
    QR qr;
    boolean editMode = false;

    //ImageView pictureOutput = new ImageView(getApplicationContext()); TEST
    ImageView pictureOutput;
    TextView titleOutput;
    EditText titleEdit;
    TextView descriptionOutput;
    EditText descriptionEdit;
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
        dialog = new AlertDialog.Builder(this).create();
        //dialogTwo = new AlertDialog.Builder(this).create();
        titleEdit = new EditText(this);
        descriptionEdit = new EditText(this);


        // ID = getIntent().getStringExtra("ID");
        qr = getIntent().getParcelableExtra("QR");
        ID = qr.getID();
        locationIcon = qr.getLocationButton((FloatingActionButton) findViewById(R.id.locationIcon));

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
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Edit");
        switch(item.getItemId()){
            case R.id.editTitle:
                dialog.setView(titleEdit);
                //edit and set new title
                titleEdit.setText(titleOutput.getText());
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        titleOutput.setText(titleEdit.getText());
                        qr.setTitle(titleEdit.getText().toString());
                        qrRepo.update(qr);
                    }
                });
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
}// end class
