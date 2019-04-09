package com.github.scanme;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RelativeLayout;

import com.yzq.zxinglibrary.common.Constant;

public class FragmentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private QRScan scan = new QRScan();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        initView();
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("scan in Fragment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,scan).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1111){
            if(data != null){
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Log.i("result of scan:", content);
                scan.onActivityResult(requestCode,requestCode,data);
            }
        }
    }
}
