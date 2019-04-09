package com.github.scanme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import static com.github.scanme.CreateEntryActivity.PERMREQ_CAMERA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//click the scan button, take it into the system camera
//scan the QR
//get info--link to the entry, get the info
public class QRScan extends AppCompatActivity implements View.OnClickListener{
    private View view;
    private Button scanButton;
    private TextView resultTv;
    public QRScan(){ }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        scanButton = view.findViewById(R.id.scanButton);
        resultTv = view.findViewById(R.id.resultTv);
        scanButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        Intent intent = new Intent(this, CaptureActivity.class);
        this.startActivityForResult(intent,1111);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1111){
            if(data != null){
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                resultTv.setText("result of scan:" + content);
            }
        }
    }
}
