package com.example.calorietracker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.calorietracker.Model.FoodItemModel;
import com.example.calorietracker.Model.FoodModel;
import com.example.calorietracker.Model.FoodTable;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class SearchActivity extends AppCompatActivity {

    private TextView searchText;
    private Button searchBtn;
    private ListView listFoods;

    private ArrayList<FoodItemModel> foodItems;
    private ArrayList<String> data;
    private FdSearchAdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = this;
        foodItems = new ArrayList<>();
        data = new ArrayList<>();
        listFoods = findViewById(R.id.listview);
        adapter = new FdSearchAdapter(this, data);
        listFoods.setAdapter(adapter);

        searchText = findViewById(R.id.textView10);
        searchBtn = findViewById(R.id.button6);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchText.getText().length() == 0)
                {
                    searchText.setError("Please enter some value");
                }
                else
                {
                    APICallSearch apiCallSearch = new APICallSearch();
                    apiCallSearch.execute(searchText.getText().toString());
                }
            }
        });

    }

    public void getFoodDescription(int index) {
        Intent intent = new Intent(this, FoodDescrption.class);
        intent.putExtra("FoodName", foodItems.get(index).getFood_name());
        startActivity(intent);
    }

    public void getFoodDetails(int index) {
        FoodItemModel foodItem = foodItems.get(index);

        APICall apiCall = new APICall();
        apiCall.execute(foodItem.getFood_id());
    }

    private class APICall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            HttpURLConnection conn = null;

            try {
                URL apiUrl = new URL("https://api.nal.usda.gov/ndb/V2/reports?ndbno=" + strings[0] + "&type=f&format=json&api_key=hvqrrNzrAMJ1Oq5kfejg6ErJQodxycnJeVvipHJG");
                conn = (HttpURLConnection)apiUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner scanner = new Scanner(conn.getInputStream());

                while (scanner.hasNextLine()) {
                    result += scanner.nextLine();
                }
            }
            catch (Exception e) {

            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.print(s);

            try {
                JSONObject obj = new JSONObject(s);
                JSONArray array = obj.getJSONArray("foods");
                System.out.print(array);

                for (int index = 0; index < array.length(); index++) {
                    JSONObject aObj = array.getJSONObject(index);
                    System.out.print(aObj);

                    if (aObj.has("food")) {
                        JSONObject foodObj = aObj.getJSONObject("food");
                        System.out.print(foodObj);

                        JSONObject descObj = foodObj.getJSONObject("desc");
                        JSONArray nutArray = foodObj.getJSONArray("nutrients");

                        String foodName = descObj.getString("name");
                        String category = descObj.getString("fg");

                        String calories = "";
                        String fat = "";

                        for (int aIndex = 0; aIndex < nutArray.length(); aIndex++) {
                            JSONObject nutObj = nutArray.getJSONObject(aIndex);
                            if (Integer.parseInt(nutObj.getString("nutrient_id")) == 208) {
                                calories = nutObj.getString("value");

                            }

                            if (Integer.parseInt(nutObj.getString("nutrient_id")) == 205) {
                                fat = nutObj.getString("value");
                            }
                        }
                        FoodModel food = new FoodModel(foodName,calories,fat);
                        System.out.print(calories);
                        System.out.print(fat);

                        FoodTable foodTable = new FoodTable();
                        foodTable.setFoodName(food.getFood_name());
                        foodTable.setCategory("Other");
                        foodTable.setCalorie(Double.parseDouble(food.getFood_calories()));
                        foodTable.setServingUnit("gram");
                        foodTable.setServingAmount("100");
                        foodTable.setFat(Double.parseDouble(food.getFood_fat()));

                        PostFoodItem postFoodItem = new PostFoodItem();
                        postFoodItem.execute(foodTable);

                        break;
                    }
                }
            }
            catch (Exception e) {

            }
        }
    }

    private class PostFoodItem extends AsyncTask<FoodTable, Void, Void> {

        @Override
        protected Void doInBackground(FoodTable... foodTable) {
            String BaseUrl = "http://192.168.1.104:8080/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection connection = null;
            try {
                String response = "";
                Gson gson =new Gson();
                String postJSON=gson.toJson(foodTable[0]);
                url = new URL(BaseUrl + "calorietracker.foodtable");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(postJSON.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                PrintWriter out= new PrintWriter(connection.getOutputStream());
                out.print(postJSON);
                out.close();

                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    response += scanner.nextLine();
                }
            } catch(Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            finally {
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class APICallSearch extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            HttpURLConnection connection = null;
            try {
                URL apUrl = new URL("https://api.nal.usda.gov/ndb/search/?format=json&q=" + strings[0] + "&ds=Standard%20Reference&api_key=hvqrrNzrAMJ1Oq5kfejg6ErJQodxycnJeVvipHJG");
                connection = (HttpURLConnection) apUrl.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                Scanner scan = new Scanner(connection.getInputStream());
                while (scan.hasNextLine()) {
                    result += scan.nextLine();
                }
            } catch (Exception e) {

            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.print(s);

            try {
                data.clear();
                foodItems.clear();
                ArrayList<FoodItemModel> afoodItems = new ArrayList<>();
                JSONObject obj = new JSONObject(s);
                JSONObject listobj = obj.getJSONObject("list");

                JSONArray array = listobj.getJSONArray("item");
                for (int index = 0; index < array.length(); index++) {
                    JSONObject aObj = array.getJSONObject(index);
                    FoodItemModel foodItem = new FoodItemModel(aObj.getString("name"), aObj.getString("ndbno"));
                    afoodItems.add(foodItem);

                    data.add(foodItem.getFood_name());
                }
                foodItems.addAll(afoodItems);
                adapter.notifyDataSetChanged();

            } catch (Exception e) {

            }
        }
    }
}

