package com.github.scanme;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.CarouselView;

public class Tutorial extends AppCompatActivity {

    CarouselView carouselView;
    int[] tutorialImages = {R.raw.app_1, R.raw.app_2, R.raw.spp_3, R.raw.app_4};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        //setSupportActionBar(toolbar);

       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(tutorialImages.length);
        carouselView.setImageListener(imageListener);
    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(tutorialImages[position]);

        }
    };
}
