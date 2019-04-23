/* Class for handling bitmap encoding/decoding, rescaling
   and size reduction of images loaded into Activity ImageViews.

   (Prevents OutOfMemory exception caused by unnecessarily looading full quality bitmaps)

   Consists of methods from the Android Bitmap developer resource:
   https://developer.android.com/topic/performance/graphics/load-bitmap
 */

package com.github.scanme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ExifInterface;
import android.graphics.Matrix;
import android.view.View;

import java.io.IOException;

public class BitmapHandler {
   public static Bitmap decodeAsThumbnail(Context context, String imagePath, int toWidth, int toHeight) {

       // First decode without allocating memory to check dimensions
       final BitmapFactory.Options options = new BitmapFactory.Options();
       options.inJustDecodeBounds = true;
       BitmapFactory.decodeFile(imagePath, options);

       // Calculate inSampleSize to scale down image
       options.inSampleSize = calculateInSampleSize(options, toWidth, toHeight);

       // Decode bitmap with inSampleSize set
       options.inJustDecodeBounds = false;
       return rotateImage(context, imagePath, options);
       //return BitmapFactory.decodeFile(imagePath, options);
   }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int toWidth, int toHeight) {
        // Raw height and width of image
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > toHeight || width > toWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= toHeight
                    && (halfWidth / inSampleSize) >= toWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap rotateImage(Context context, String imagePath) {
       return rotateImage(context, imagePath, null);
    }

    public static Bitmap rotateImage(Context context, String imagePath, BitmapFactory.Options options) {

        // optional third parameter (indicated as null) for setting dimension restrictions
        Bitmap image = (options == null ? BitmapFactory.decodeFile(imagePath) : BitmapFactory.decodeFile(imagePath, options));

        ExifInterface ei;
        try {
            ei = new ExifInterface(imagePath);
        }
        catch (IOException e) {
            return image;
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(image, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(image, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(image, 270);
            default:
                return image;
        }
    }

    private static Bitmap rotateImage(Bitmap image, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        image.recycle();
        return rotatedImg;
    }

    public static Bitmap createFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getLayoutParams().width, view.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getLayoutParams().width, view.getLayoutParams().height);
        view.draw(canvas);
        return bitmap;
    }

}
