package com.developerabdulhalim.tvserial;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;


import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.bdtopcoder.smart_slider.SliderAdapter;
import com.bdtopcoder.smart_slider.SliderItem;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class MainActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler {

    // Views and Widgets
    private GridView gridView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageMenu;

    // Data
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    // Managers
    private InAppUpdateManager inAppUpdateManager;
    private ReviewManager reviewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Call all methods===================
        initializeViews();
        NavDrawerClick();
        setStatusColor();
        setupSlider();
        checkInternetConnection();
        initializeInAppUpdate();
        initializeInAppReview();
        populateGridViewData();
    }


    // Initialize all findViewBy ID============================
    private void initializeViews() {
        gridView = findViewById(R.id.gridView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_View);
        imageMenu = findViewById(R.id.imageMenu);
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


    //check Internet Connection ===================================================
    private void checkInternetConnection() {
        if (!isConnected()) {
            showNoInternetDialog();
            return;
        }
    }

    // initializeInAppUpdate==========================================================
    private void initializeInAppUpdate() {
        inAppUpdateManager = InAppUpdateManager.Builder(this, 1)
                .resumeUpdates(true)
                .mode(Constants.UpdateMode.IMMEDIATE)
                .snackBarAction("An update has just been downloaded.")
                .snackBarAction("RESTART")
                .handler(this);
        inAppUpdateManager.checkForAppUpdate();
    }



//initializeInAppReview=============================================================================
    private void initializeInAppReview() {
        reviewManager = ReviewManagerFactory.create(this);
    }

//=====================================================================================================




    // Drawar click event=========================================================
    // Drawer item Click event ------==

    private void NavDrawerClick(){

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.kenoAdAse) {
                    kenoAdAse();
                    drawerLayout.closeDrawers();
                } else if (itemId == R.id.rate) {
                    showRateApp();
                    drawerLayout.closeDrawers();
                }else if (itemId == R.id.bug) {
                    reportAbug();
                    drawerLayout.closeDrawers();
                }else if (itemId == R.id.moreApp) {
                    moreApp(MainActivity.this,"Developer Abdul Halim");
                    drawerLayout.closeDrawers();
                }else if (itemId == R.id.shareApp) {
                    ShareApp(MainActivity.this);
                    drawerLayout.closeDrawers();
                }else if (itemId == R.id.policy) {
                    PrivacyPolicy();
                    drawerLayout.closeDrawers();
                }else if (itemId == R.id.disclaimer) {
                    Disclaimer();
                    drawerLayout.closeDrawers();
                }

                return false;
            }


        });

        // App Bar Click Event
        imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }



    private void kenoAdAse() {
        new AlertDialog.Builder(this)
                .setTitle("কেন বিজ্ঞাপন আসে?")
                .setMessage(R.string.keno_ad_ase)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(getDrawable(R.drawable.baseline_snooze_24))
                .show();
    }

    //Rate this app method=====================================================
    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We got the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = reviewManager.launchReviewFlow(MainActivity.this, reviewInfo);
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



    // report a bug method======================================================
    private void reportAbug() {



        String email="abdulhalimbaccu447@gmail.com";
        Uri uri = Uri.fromParts("mailto",email,null);
        Intent emailIntent= new Intent(Intent.ACTION_VIEW);
        emailIntent.setData(uri);
        startActivity(emailIntent);


    }


    // more App =====================================================
    public static void moreApp(Context context, String DeveloperName){
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:"+ DeveloperName)));
        }catch (ActivityNotFoundException e){
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+ DeveloperName)));
        }
    }




// Share app ==================================================================
// share App
public static void ShareApp(Context context){
    final String appPakageName = context.getPackageName();
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
    intent.putExtra(Intent.EXTRA_TEXT, "Download Now : https://play.google.com/store/apps/details?id="+appPakageName);
    context.startActivity(Intent.createChooser(intent, "Share Via"));
}




    // privacy policy ======================================================
    private void PrivacyPolicy() {

        String Url="https://sites.google.com/view/banglaserialapp/home";
        Uri uri = Uri.parse(Url);
        Intent emailIntent= new Intent(Intent.ACTION_VIEW);
        emailIntent.setData(uri);
        startActivity(emailIntent);


    }

    private void Disclaimer(){


        new AlertDialog.Builder(this)
                .setTitle("Disclaimer")
                .setMessage("Disclaimer:\n" +
                        "This app does not host or upload any files or contents on its own servers. All files or contents are hosted on third party websites. This app does not accept responsibility for contents hosted on third party websites. We only index and link to those links which are already available on the internet.\n" +
                        "\n" +
                        "All channels listed in this app are external YouTube channels and all channel content, logos and images are the property of their respective channel owners. We do not have any control over or ownership of these channels or their contents. If you are a channel owner and do not want your channel to be listed in this app, please contact us via email. We will remove your channel from our app as soon as possible. Thank you for your cooperation.\n" +
                        "(\"fair use\" Policy)")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(getDrawable(R.drawable.copyright_logo))
                .show();


    }










    //GridView call smartly =============================================================
    private void populateGridViewData() {
        // Consider refactoring this to be more dynamic.
        // Maybe an array of objects with all required properties,
        // then loop through them to add to the arrayList.
        arrayList.clear();
        addItemToGrid("স্টার জলসা\nচ্যানেল-১", R.drawable.starjalsha);
        addItemToGrid("স্টার জলসা\nচ্যানেল-২", R.drawable.starjalsha);
        addItemToGrid("জি বাংলা \nচ্যানেল-১", R.drawable.zee_bangla);
        addItemToGrid("জি বাংলা \nচ্যানেল-২", R.drawable.zee_bangla);
        addItemToGrid("কালার'স বাংলা ", R.drawable.colors_bangla);
        addItemToGrid("সান বাংলা", R.drawable.sun_bangla);
        // ... add other items...

        gridView.setAdapter(new MyAdapter());
    }

    // add girdiView Item======================================================
    private void addItemToGrid(String title, int iconResId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Title", title);
        hashMap.put("icon", String.valueOf(iconResId));
        arrayList.add(hashMap);
    }

    // Checking Internet connection is availabele============================================

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    // showeing no Internet Dialogeue==========================================================
    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // You can close the app or take any other appropriate action
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Method for showing keno ad ase==================================================























    // In App Update error handle============================================================================
    @Override
    public void onInAppUpdateError(int code, Throwable error) {

    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {

        if (status.isDownloaded()) {

            View view = getWindow().getDecorView().findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar.make(view,

                    "An update has Just been Downloaded",

                    Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("", new View.OnClickListener() {

                @Override

                public void onClick(View view) {

                    inAppUpdateManager.completeUpdate();


                }

            });

            snackbar.show();

        }


    }
























    // MyAdapter ===================================================================================
    // MyAdapter ===================================================================================
    // MyAdapter ===================================================================================
    // MyAdapter ===================================================================================

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item, parent, false);

            ImageView imgIcon = view.findViewById(R.id.itemCover);
            TextView lineOne = view.findViewById(R.id.lineOne);
            LinearLayout layItem = view.findViewById(R.id.layItem);

            HashMap<String, String> hashMap = arrayList.get(position);
            String Title = hashMap.get("Title");
            String icon = hashMap.get("icon");

            lineOne.setText(Title);

            if (imgIcon != null && icon != null) {
                int drawable = Integer.parseInt(icon);
                imgIcon.setImageResource(drawable);
            }

            layItem.setOnClickListener(v -> {
                Intent intent;
                switch (position) {
                    case 0:
                        TVContent.URL="https://sheets.googleapis.com/v4/spreadsheets/1uLtSQv_Cq1hVXIv56QTr-fw090v49KZ0c6ebcGV0cbM/values/Sheet1!A2:B40?key=AIzaSyDonalI79HrCo2LAmgZilAYZAFDXh1E1l4";
                        intent = new Intent(MainActivity.this, TVContent.class);
                        intent.putExtra("imageResId", R.drawable.playing_logo);
                        startActivity(intent);
                        break;
                    case 1:
                        TVContent.URL="https://sheets.googleapis.com/v4/spreadsheets/1ImCCg0W9gGTsyjHCfZIDTzKbC37lT8ID8zBeYozC8pU/values/Sheet1!A2:B40?key=AIzaSyDonalI79HrCo2LAmgZilAYZAFDXh1E1l4";
                        intent = new Intent(MainActivity.this, TVContent.class);
                        intent.putExtra("imageResId", R.drawable.playing_logo);
                        startActivity(intent);
                        break;
                    case 2:
                        TVContent.URL="https://sheets.googleapis.com/v4/spreadsheets/1aH34HPZ9upUiMfdJ2JnPoEsXR0_431Lr98Q4liIk56M/values/Sheet1!A2:B40?key=AIzaSyDonalI79HrCo2LAmgZilAYZAFDXh1E1l4";
                        intent = new Intent(MainActivity.this, TVContent.class);
                        intent.putExtra("imageResId", R.drawable.playing_logo);
                        startActivity(intent);
                        break;

                    case 3:
                        TVContent.URL="https://sheets.googleapis.com/v4/spreadsheets/1TZM_fFZDj6_hjBB9VeEpKFwCnPRGt5eFcnlG-_miz_g/values/Sheet1!A2:B40?key=AIzaSyDonalI79HrCo2LAmgZilAYZAFDXh1E1l4";
                        intent = new Intent(MainActivity.this, TVContent.class);
                        intent.putExtra("imageResId", R.drawable.playing_logo);
                        startActivity(intent);
                        break;
                    case 4:
                        TVContent.URL="https://sheets.googleapis.com/v4/spreadsheets/1529rFw9m75RVR1Gm1jHc70McoyQoVhGm7bi_T7LO1FY/values/Sheet1!A2:B40?key=AIzaSyDonalI79HrCo2LAmgZilAYZAFDXh1E1l4";
                        intent = new Intent(MainActivity.this, TVContent.class);
                        intent.putExtra("imageResId", R.drawable.playing_logo);
                        startActivity(intent);
                        break;
                    case 5:
                        TVContent.URL="https://sheets.googleapis.com/v4/spreadsheets/1w4BUJN2zhB0NFJdY-IiS8dgrJ9RZV06rngLs2tojHnU/values/Sheet1!A2:B40?key=AIzaSyDonalI79HrCo2LAmgZilAYZAFDXh1E1l4";
                        intent = new Intent(MainActivity.this, TVContent.class);
                        intent.putExtra("imageResId", R.drawable.playing_logo);
                        startActivity(intent);
                        break;

                    default:
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        break;
                }
            });


            return view;
        }
    }


}
