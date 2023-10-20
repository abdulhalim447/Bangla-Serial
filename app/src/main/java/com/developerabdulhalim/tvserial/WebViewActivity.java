package com.developerabdulhalim.tvserial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.os.AsyncTask;

public class WebViewActivity extends AppCompatActivity {

    private WebView videoWebView;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private int originalOrientation;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web_view);

        videoWebView = findViewById(R.id.videoWebView);



        // Internet connection check here is here
        if (!isConnected()) {
            showNoInternetDialog();
            return; // You can choose to return here if you don't want any subsequent code to run without internet
        }


        //=================================================================


        // Initialize the progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Prevents users from dismissing it


        WebSettings webSettings = videoWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        videoWebView.setWebChromeClient(new WebChromeClient() {


            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }
                customView = view;
                originalOrientation = getRequestedOrientation();
                customViewCallback = callback;
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.addView(customView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));

                // Set the screen orientation to landscape when entering fullscreen
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }


            @Override
            public void onHideCustomView() {
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.removeView(customView);
                customView = null;

                // Revert the screen orientation to its original state when exiting fullscreen
                setRequestedOrientation(originalOrientation);

                customViewCallback.onCustomViewHidden();
                videoWebView.setVisibility(View.VISIBLE);

            }


        });
        videoWebView.setWebViewClient(new WebViewClient());

        // Get the URL passed from the Intent
        String targetUrl = getIntent().getStringExtra("videoUrl");
        if (targetUrl != null) {
            new VideoUrlExtractor().execute(targetUrl);
        }
    }

    private class VideoUrlExtractor extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                Document document = Jsoup.connect(urls[0]).get();
                Elements iframes = document.select("iframe[src]");
                for (Element iframe : iframes) {
                    String iframeSrc = iframe.attr("src");
                    if (iframeSrc.contains("https://www.blogger.com/")) {
                        return iframeSrc;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                videoWebView.loadUrl(result);

            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                videoWebView.getWebChromeClient().onHideCustomView();
            }
        } else if (videoWebView.canGoBack()) {
            videoWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }



    //Check Internet connection==========================================================
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

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




}