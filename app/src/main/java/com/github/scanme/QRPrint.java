package com.github.scanme;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.content.Context;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.print.PrintManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.scanme.database.QR;


public class QRPrint extends AppCompatActivity {

    Button printBtn;
    List<QR> listQR = new ArrayList<>();
    QR qrOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_qrlist);
        listQR = new ArrayList<>();
        Intent intent = getIntent();
        listQR = intent.getParcelableArrayListExtra("QRs");
    }

    public void printDocument(View view)
    {
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) +
                " Document";

        printManager.print(jobName, new MyPrintDocumentAdapter(this),
                null);
    }

    public class MyPrintDocumentAdapter extends PrintDocumentAdapter
    {
        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 4;//calculate the number of pages based on the number of QRs selected

        private void drawPage(PdfDocument.Page page,
                              int pagenumber, QR qr) {
            Canvas canvas = page.getCanvas();

            pagenumber++; // Make sure page numbers start at 1

           /* int titleBaseLine = 72;
            int leftMargin = 54;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            canvas.drawText(
                    qr.getTitle(),
                    leftMargin,
                    titleBaseLine,
                    paint);
            paint.setTextSize(14);
            canvas.drawText("Cut these QR codes out and place them wherever you need them.", pageWidth / 2, titleBaseLine + 35, paint);
            canvas.drawText("Scan it later via ScanMe!", pageWidth / 2, titleBaseLine + 70, paint);
/*
            if (pagenumber % 2 == 0)
                paint.setColor(Color.RED);
            else
                paint.setColor(Color.GREEN);

            PageInfo pageInfo = page.getInfo();


            canvas.drawCircle(pageInfo.getPageWidth()/2,
                    pageInfo.getPageHeight()/2,
                    150,
                    paint);
*/
            //for ()
            //canvas.drawBitmap(BitmapFactory.decodeFile(qrOne.getQrPath()), 0, 0, null);
            //canvas.drawBitmap(BitmapFactory.decodeFile(qr.getQrPath()), null, new RectF(10,10, 0, 0),null);

            canvas.drawBitmap(QRPrint.SINGLE.createBitmap(qr, context), 0, 0, null);

        }



        public MyPrintDocumentAdapter(Context context)

        {
            this.context = context;
            this.totalpages = listQR.size();
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {
            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight =
                    newAttributes.getMediaSize().getHeightMils()/1000 * 72;
            pageWidth =
                    newAttributes.getMediaSize().getWidthMils()/1000 * 72;

            if (cancellationSignal.isCanceled() ) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }


        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {
            for (int i = 0; i <= totalpages; i++) {
                if (pageInRange(pageRanges, i))
                {
                    PageInfo newPage = new PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    drawPage(page, i, listQR.get(i));
                    myPdfDocument.finishPage(page);
                }
            }
            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }

        private boolean pageInRange(PageRange[] pageRanges, int page)
        {
            for (int i = 0; i<pageRanges.length; i++)
            {
                if ((page >= pageRanges[i].getStart()) &&
                        (page <= pageRanges[i].getEnd()))
                    return true;
            }
            return false;
        }

    }

    public final static class SINGLE {

        static int pageHeight = PrintAttributes.MediaSize.NA_LETTER.getHeightMils()/1000 * 72;
        static int pageWidth = PrintAttributes.MediaSize.NA_LETTER.getWidthMils()/1000 * 72;

        public static Bitmap createBitmap(QR qr, Context context) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate( R.layout.print_layout_single, null );
            int width = View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY);
            int height = View.MeasureSpec.makeMeasureSpec(pageHeight, View.MeasureSpec.EXACTLY);
            view.measure(width, height);

            TextView title = view.findViewById(R.id.title);
            title.setText(qr.getTitle());

            Bitmap qrBitmap = BitmapFactory.decodeFile(qr.getQrPath());
            ImageView image1 = view.findViewById(R.id.image1);
            image1.setImageBitmap(qrBitmap);

            ImageView image2 = view.findViewById(R.id.image2);
            image2.setImageBitmap(qrBitmap);


            ImageView image3 = view.findViewById(R.id.image3);
            image3.setImageBitmap(qrBitmap);

            ImageView image4 = view.findViewById(R.id.image4);
            image4.setImageBitmap(qrBitmap);

            TextView instr = view.findViewById(R.id.instructions);
            instr.setText("Place these wherever you need. Scan later with ScanMe!");

            /*TextView titleView = createTextView(context, qr.getTitle(), 20);
            wrapper.addView(titleView);

            int rows = 2;
            int cols = 2;
            GridLayout imageGrid = createGridLayout(context, rows, cols);

            Bitmap qrBitmap = BitmapFactory.decodeFile(qr.getQrPath());
            for (int i = 0; i <= rows * cols; ++i) {
                imageGrid.addView(createGridItem(context, qrBitmap, qr.getTitle()));
            }
            wrapper.addView(imageGrid);

            TextView instructionView = createTextView(context, qr.getTitle(), 12);
            wrapper.addView(instructionView);*/

            return BitmapHandler.createFromView(view);
        }

        public static LinearLayout createLinearLayout(Context context, int orientation) {
            LinearLayout view = new LinearLayout(context);
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
            int width = View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY);
            int height = View.MeasureSpec.makeMeasureSpec(pageHeight, View.MeasureSpec.EXACTLY);
            view.measure(width, height);
            return view;
        }

        public static TextView createTextView(Context context, String text, int size) {
            TextView view = new TextView(context);
            view.setText(text);
            view.setTextSize(size);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            return view;
        }

        public static ImageView createImageView(Context context, Bitmap bitmap) {
            ImageView view = new ImageView(context);
            view.setImageBitmap(bitmap);
           // view.setLayoutParams(new GridLayout.LayoutParams);
            return view;
        }
    }

}