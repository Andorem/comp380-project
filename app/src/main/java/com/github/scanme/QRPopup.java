package com.github.scanme;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.scanme.database.QR;

import java.io.File;


public class QRPopup extends DialogFragment {


    QR qr;

    public QRPopup() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrpopup, container, false);

        qr = getActivity().getIntent().getParcelableExtra("QR");
        getDialog().setTitle(qr.getTitle());

        ImageView qrImage = view.findViewById(R.id.qrImage);
        qrImage.setImageBitmap(BitmapFactory.decodeFile(qr.getQrPath()));

        TextView qrTitle = view.findViewById(R.id.qrTitle);
        qrTitle.setText(qr.getTitle());

        ImageButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ImageButton shareButton = view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQR();
            }
        });

        return view;
    }

    public void shareQR() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "QR Code");
        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(new File(qr.getQrPath())));
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, qr.getTitle() +
                "\nScan this QR Code via ScanMe!");
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
    }
}
