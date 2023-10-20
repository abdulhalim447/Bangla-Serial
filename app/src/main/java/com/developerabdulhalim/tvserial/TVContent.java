package com.developerabdulhalim.tvserial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class TVContent extends AppCompatActivity {


    private RequestQueue queue;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> links = new ArrayList<>();

    private GridView gridView;

    private ProgressDialog progressDialog;


    private EditText searchEditText;

    public static String URL="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvcontent);


        gridView = findViewById(R.id.gridView);
        searchEditText = findViewById(R.id.searchEditText);


        // Internet connection check here is here
        if (!isConnected()) {
            showNoInternetDialog();
            return; // You can choose to return here if you don't want any subsequent code to run without internet
        }


        //=================================================================


//===================================================================================================

        // Initialize the progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);  // Prevents users from dismissing it
        progressDialog.show();

//====================================================================================================

        // searchFilter========================================

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



//==================================================================================================


        queue = Volley.newRequestQueue(this);

        String url = URL;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    parseJson(response);
                    setUpGridView();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(TVContent.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });

        queue.add(stringRequest);

        //=============================================================================================
    }

    private void parseJson(String jsonString) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray valuesArray = jsonObject.getJSONArray("values");

        for (int i = 0; i < valuesArray.length(); i++) {
            JSONArray entry = valuesArray.getJSONArray(i);
            String title = entry.getString(0);
            String link = entry.getString(1);

            titles.add(title);
            links.add(link);
        }
    }
//==================================================================================================
    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> titles;
        private ArrayList<String> images; // Assuming the links are image URLs

        CustomAdapter(Context context, ArrayList<String> titles, ArrayList<String> images) {
            super(context, 0, titles);
            this.titles = titles;
            this.images = images;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_content, parent, false);
            }

            TextView titleTextView = convertView.findViewById(R.id.titleTextView);
            ImageView imageView = convertView.findViewById(R.id.imageView);

            titleTextView.setText(titles.get(position));

            if (getIntent().hasExtra("imageResId")) {
                int imageResId = getIntent().getIntExtra("imageResId", 0);
                if (imageResId != 0) {
                    imageView.setImageResource(imageResId);
                }
            }

            return convertView;
        }
    }

    //============================================================================================
    private void setUpGridView() {
        CustomAdapter adapter = new CustomAdapter(this, titles, links);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedLink = links.get(position);
            playVideoInWebView(selectedLink);
        });
    }


    private void playVideoInWebView(String videoUrl) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("videoUrl", videoUrl);
        startActivity(intent);
    }


    //==========================================================================
    // searchFilter method========================================
    private void filter(String text) {
        ArrayList<String> filteredTitles = new ArrayList<>();
        ArrayList<String> filteredLinks = new ArrayList<>();

        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).toLowerCase().contains(text.toLowerCase())) {
                filteredTitles.add(titles.get(i));
                filteredLinks.add(links.get(i));
            }
        }

        CustomAdapter adapter = new CustomAdapter(this, filteredTitles, filteredLinks);
        gridView.setAdapter(adapter);
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




