package com.example.calorietracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class FoodDescrption extends AppCompatActivity {

    final String GOOGLE_API_KEY = "AIzaSyDR0aSHW8k8iQNPaEih7JCO4vsrBBHK2tA";
    final String SEARCH_ENGINE_ID= "002430456392787690062:huqw-nzfr3s";
    private String descFood;
    private String imagUrl;

    private ImageView imgFood;
    private TextView lblDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_descrption);


        lblDescription = findViewById(R.id.textView18);
        imgFood = findViewById(R.id.imageView);

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("FoodName");
        makeURL(keyword, new String[]{"num"}, new String[]{"1"});
    }

    public void makeURL(String keyword, String[] parameters, String[] values) {
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
            String queryParameters = "";

            if ((parameters != null) && (values != null)) {
                for (int index = 0; index < parameters.length; index++) {
                    queryParameters += "&";
                    queryParameters += parameters[index];
                    queryParameters += "=";
                    queryParameters += values[index];
                }
            }

            String apiUrl = "https://www.googleapis.com/customsearch/v1?key=" + GOOGLE_API_KEY + "&cx=" + SEARCH_ENGINE_ID + "&q=" + keyword + queryParameters;

            CustomSearch customSearch = new CustomSearch();
            customSearch.execute(apiUrl);
        }
        catch (Exception ex) {

        }
    }

    private class CustomSearch extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            HttpURLConnection conn = null;

            try {
                URL apiUrl = new URL(strings[0]);
                conn = (HttpURLConnection) apiUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                Scanner scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNextLine()) {
                    result += scanner.nextLine();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            return result;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.print(s);
            try {
                JSONObject obj = new JSONObject(s);
                JSONArray array = obj.getJSONArray("items");
                System.out.print(array);
                JSONObject firstObj = array.getJSONObject(0);
                descFood = firstObj.getString("snippet");
                JSONObject childObj = firstObj.getJSONObject("pagemap");
                JSONArray childArray = childObj.getJSONArray("cse_thumbnail");
                JSONObject secondObj = childArray.getJSONObject(0);
                imagUrl = secondObj.getString("src");

                lblDescription.setText(descFood);

                LoadImage loadImage = new LoadImage();
                loadImage.execute(imagUrl);
            }
            catch (Exception e )
            {

            }
        }
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;

            try {
                InputStream inputStream = new URL(strings[0]).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            catch (Exception ex) {

            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imgFood.setImageBitmap(bitmap);
        }
    }

}