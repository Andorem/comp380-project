package com.github.scanme;
//https://blog.csdn.net/yuzhiqiang_1993/article/details/78292004
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import static com.github.scanme.CreateEntryActivity.PERMREQ_CAMERA;

//click the scan button, take it into the system camera
//scan the QR
//get info--link to the entry, get the info
//public class QRScan extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_qrscan);
//    }
//    public void takePicture(View view) {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            // No permission granted
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMREQ_CAMERA);
//        }
//        else {
//            // Permission already been granted
//            final int REQUEST_IMAGE_CAPTURE = 1;
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Call System Camera App
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImageURI()); //create uri for image
//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }
//}
public class QRScan extends Fragment implements View.OnClickListener{
    private View view;
    private Button scanButton;
    private TextView resultTv;
    public QRScan(){ }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_qrscan, container,false);
        initView();
        return view;
    }
    private void initView(){
        scanButton = view.findViewById(R.id.scanButton);
        resultTv = view.findViewById(R.id.resultTv);
        scanButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        getActivity().startActivityForResult(intent,1111);
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