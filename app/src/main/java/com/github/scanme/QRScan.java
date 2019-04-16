package com.github.scanme;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yzq.zxinglibrary.android.CaptureActivity;

public class scan extends Fragment implements View.OnClickListener {
    private View view;
    private Button scanButton;
    private TextView resultTv;
    public scan() { }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_scan, container, false);
        initView();

        return view;
    }

    private void initView() {
        scanButton = view.findViewById(R.id.scanButton);
        resultTv = view.findViewById(R.id.resultTv);
        scanButton.setOnClickListener(this);
    }
    //https://github.com/yuzhiqiang1993/zxing/blob/master/app/src/main/res/layout/activity_main.xml
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        getActivity().startActivityForResult(intent, 1111);
    }


}