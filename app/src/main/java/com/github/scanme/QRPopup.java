package com.github.scanme;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.print.PrintHelper;

import android.print.PrintAttributes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.scanme.database.QR;

import java.io.File;


public class QRPopup extends DialogFragment {


    QR qr;
    View view;
    ImageView qrImage;
    TextView qrTitle;
    Bitmap qrBitmap;
    ConstraintLayout wrapper;

    public QRPopup() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_qrpopup, container, false);

        qr = getActivity().getIntent().getParcelableExtra("QR");
        getDialog().setTitle(qr.getTitle());

        qrImage = view.findViewById(R.id.qrImage);
        qrBitmap = BitmapFactory.decodeFile(qr.getQrPath());
        qrImage.setImageBitmap(qrBitmap);

        qrTitle = view.findViewById(R.id.qrTitle);
        qrTitle.setText(qr.getTitle());

        wrapper = view.findViewById(R.id.wrapper);

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

        ImageButton printButton = view.findViewById(R.id.printButton);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printQR(getContext(), qr);
            }
        });

        return view;
    }

    public void shareQR() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "QR Code");
        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(new File(qr.getQrPath())));
       // shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, qr.getTitle() +
       //         "\nScan this QR Code via ScanMe!");
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
    }

    public static void printQR(Context context, QR qr) {
        PrintHelper photoPrinter = new PrintHelper(context);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        photoPrinter.printBitmap(qr.getTitle(), QRPrint.SINGLE.createBitmap(qr, context));
    }

}
