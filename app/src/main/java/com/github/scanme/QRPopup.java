package com.github.scanme;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.print.PrintHelper;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(QRPopup.this);
               // android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(QRPopup);
                builder.setCancelable(true);
                builder.setTitle("Are you sure to print or send your QR codes?");
                builder.setMessage("Send by email, save the forest.");

                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printQR();
                    }
                });
               // printQR();
                builder.show();
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

    public void printQR() {
       /* PrintHelper photoPrinter = new PrintHelper(getContext());
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap printBitmap = buildBitmap();
        photoPrinter.printBitmap(qr.getTitle(), printBitmap);*/
    }

    public Bitmap buildBitmap() {
        LinearLayout wrapper = view.findViewById(R.id.wrapper);

       /* ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setImageBitmap(qrBitmap);
        wrapper.addView(imageView);

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setText(qr.getTitle());
        wrapper.addView(textView);*/

        return BitmapHandler.createFromView(wrapper);
    }

}
