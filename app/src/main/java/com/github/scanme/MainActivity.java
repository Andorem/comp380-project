package com.github.scanme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;

/*
import android.widget.Toast;
import android.widget.Button;
*/

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openCreateEntryActivity();
            }
        });
    }

    //This is the intent used for create button(main activity) ---> createEntryActivity.java
    public void openCreateEntryActivity(){
        Intent intent  = new Intent(this, CreateEntryActivity.class);
        startActivity(intent);
    }
}
