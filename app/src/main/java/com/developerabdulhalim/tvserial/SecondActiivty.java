package com.developerabdulhalim.tvserial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bdtopcoder.smart_slider.SliderAdapter;
import com.bdtopcoder.smart_slider.SliderItem;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class SecondActiivty extends AppCompatActivity {

    Button goToNext;
    CardView rateApp;
    private ReviewManager reviewManager;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        setStatusColor();
        setupSlider();
        FindViewByID();

        reviewManager = ReviewManagerFactory.create(this);

        goToNext.setOnClickListener(v -> {


            startActivity(new Intent(SecondActiivty.this, MainActivity.class));

        });


        rateApp.setOnClickListener(v -> {

            promptForReview();


        });

        backButton.setOnClickListener(v -> {

            startActivity(new Intent(SecondActiivty.this,FirstActivity.class));


        });

    }



        //every findViewById is here==============================

        private void FindViewByID(){

            goToNext =  findViewById(R.id.goToNext);
            rateApp =  findViewById(R.id.privacyPolicy);
            backButton =  findViewById(R.id.backButton);



        }




        //Stsatus Bar color change===================================================
        private void setStatusColor() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.your_status_bar_color));
            }
        }

        // Image Slider method==================================================================
        private void setupSlider() {
            ViewPager2 viewPager2 = findViewById(R.id.smartSlider);

            List<SliderItem> sliderItems = new ArrayList<>();
            sliderItems.add(new SliderItem(R.drawable.slider_1,"image 1"));
            sliderItems.add(new SliderItem(R.drawable.slider_2,"image 2"));
            sliderItems.add(new SliderItem(R.drawable.slider_3,"image 2"));
            sliderItems.add(new SliderItem(R.drawable.slider_4,"image 2"));
            sliderItems.add(new SliderItem(R.drawable.slider_5,"image 2"));

            viewPager2.setAdapter(new SliderAdapter(sliderItems,viewPager2,3000));
        }




//InAppReview======================================================================================


    private void promptForReview() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We got the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = reviewManager.launchReviewFlow(SecondActiivty.this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {

                });
            } else {

                goToPlayStore();
            }
        });
    }

    private void goToPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }









}