package com.developerabdulhalim.tvserial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bdtopcoder.smart_slider.SliderAdapter;
import com.bdtopcoder.smart_slider.SliderItem;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends AppCompatActivity {

    Button goToNext;
    CardView privacyPolicy;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        setStatusColor();
        setupSlider();
        FindViewByID();








        goToNext.setOnClickListener(v->{


            startActivity(new Intent(FirstActivity.this,SecondActiivty.class));

        });

        backButton.setOnClickListener(v->{

            showExitDialog();


                });


        privacyPolicy.setOnClickListener(v->{

            String Url="https://sites.google.com/view/banglaserialapp/home";
            Uri uri = Uri.parse(Url);
            Intent emailIntent= new Intent(Intent.ACTION_VIEW);
            emailIntent.setData(uri);
            startActivity(emailIntent);



        });




    }

    //every findViewById is here==============================

    private void FindViewByID(){

         goToNext =  findViewById(R.id.goToNext);
        privacyPolicy =  findViewById(R.id.privacyPolicy);
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


    // alert dialogue here if user want to go out -----------------------------------
    private static final long DOUBLE_BACK_PRESS_TIME = 2000; // time in milliseconds
    private long lastBackPressTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressTime < DOUBLE_BACK_PRESS_TIME) {
            // User has double-tapped the back button
            showExitDialog();
        } else {
            lastBackPressTime = currentTime;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Do you want to really exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close the app
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }




}