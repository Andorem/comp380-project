package com.github.scanme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.scanme.database.QRRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import static android.os.Build.ID;

public class genQR {

    //variables
    ImageView imageView;
    String textToQR;
    int QRwidth = 750;
    private Bitmap bitmap;
    Context context;

    genQR(Context context) {
        this.context = context;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen_qr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageView);
    }*/

    public void encode(String id) {
        new encodeAsync(id).execute(id);
    }

    private class encodeAsync extends AsyncTask<String, Void, Void> {
        String id;

        encodeAsync(String id) {
            this.id = id;
            Log.i("GENQR", "Starting QR encoding for ID: " + id);
        }

        @Override
        protected Void doInBackground(final String... params) {
            try {
                bitmap = Encoder(params[0]);

            } catch (WriterException e) {
                e.printStackTrace();
            }
            saveBitmap(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("GENQR", "QR successfully encoded for ID: " + id);
        }
    }

    private Bitmap Encoder(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRwidth, QRwidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        context.getResources().getColor(R.color.colorPrimaryDark):context.getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_8888);

        bitmap.setPixels(pixels, 0, QRwidth, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private void saveBitmap(String id) {
            String dirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/QRs";
            String filePath = "/QR_" + id + ".png";

            File qrDir = new File(dirPath);
            if(!qrDir.exists()) {
                qrDir.mkdirs();
            }
            File qrFile = new File(qrDir, filePath);
            try {
                FileOutputStream output = new FileOutputStream(qrFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                output.close();

                Log.i("GENQR", "QR File path: " + dirPath + filePath);
                Log.i("GENQR", "QR bitmap successfully saved for ID: " + id);
            }
            catch (FileNotFoundException e) {
                Log.e("GENQR", e.getMessage());
            }
            catch (IOException e) {
                Log.e("GENQR", e.getMessage());
            }
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
}
