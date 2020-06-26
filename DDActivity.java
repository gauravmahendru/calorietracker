package com.example.calorietracker;

import android.content.Context;
import java.util.List;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.calorietracker.Model.ConsumptionTable;
import com.example.calorietracker.Model.FoodTable;
import com.example.calorietracker.Model.UserTable;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class DDActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private ListView listView;

    private ArrayList<FoodTable> foodTable;
    private ArrayList<String> data;
    private FdCategoryAdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dd);


        context = this;
        foodTable = new ArrayList<>();
        data = new ArrayList<>();
        listView = findViewById(R.id.thelistview);
        adapter = new FdCategoryAdapter(this, data);
        listView.setAdapter(adapter);

        FloatingActionButton btnSearch = findViewById(R.id.floatingActionButton);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchActivity.class);
                startActivity(intent);
            }
        });

        spinnerCategory = findViewById(R.id.spinner2);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {

                } else {

                    String category = spinnerCategory.getSelectedItem().toString();
                    ApiForCategory apiForCategory = new ApiForCategory();
                    apiForCategory.execute(category);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getFoodDescription(int foodCategoryIndex) {
        Intent intent = new Intent(this, FoodDescrption.class);
        intent.putExtra("FoodName", foodTable.get(foodCategoryIndex).getFoodName());
        startActivity(intent);
    }

    public void addConsumptionItem(int foodCategoryIndex) {


        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String user = sharedPreferences.getString("User", "");
            int userid = 0;

            JSONArray array = new JSONArray(user);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jObj = array.getJSONObject(i);
                if (jObj.has("userTable")) ;
                {
                    JSONObject jsonObject = jObj.getJSONObject("userTable");
                    System.out.print(jsonObject);
                    userid = Integer.parseInt(jsonObject.getString("userId"));
                }
            }

            UserTable usertable = new UserTable();
            usertable.setUserId(userid);

            FoodTable foodCategory = foodTable.get(foodCategoryIndex);
            double quantity = 1;

            ConsumptionTable consumptiontable = new ConsumptionTable(quantity, usertable, foodCategory);

            PostConsumptionItem postConsumptionItem = new PostConsumptionItem();
            postConsumptionItem.execute(consumptiontable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class PostConsumptionItem extends AsyncTask<ConsumptionTable, Void, Void> {

        @Override
        protected Void doInBackground(ConsumptionTable... consumptiontables) {

            String BaseUrl = "http://192.168.1.104:8080/Assignment15046/webresources/";
            URL url = null;
            HttpURLConnection connection = null;
            try {
                String response = "";
                Gson gson =new Gson();
                String postJSON=gson.toJson(consumptiontables[0]);
                url = new URL(BaseUrl + "restws.consumptiontable");
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
    }

    private class ApiForCategory extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String BaseUrl = "http://192.168.1.104:8080/Assignment15046/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";

            try {
                url = new URL(BaseUrl + "restws.foodtable/findByCategory/" + strings[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return textResult;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                data.clear();
                foodTable.clear();
                JSONArray array = new JSONArray(s);
                List<FoodTable> aList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject aObj = array.getJSONObject(i);
                    FoodTable foodCategory = new FoodTable();
                    foodCategory.setCalorie(aObj.getDouble("calorie"));
                    foodCategory.setCategory(aObj.getString("category"));
                    foodCategory.setFat(aObj.getDouble("fat"));
                    foodCategory.setFoodId(aObj.getInt("foodId"));
                    foodCategory.setFoodName(aObj.getString("foodName"));
                    foodCategory.setServingAmount(aObj.getString("servingAmount"));
                    foodCategory.setServingUnit(aObj.getString("servingUnit"));

                    aList.add(foodCategory);
                    data.add(foodCategory.getFoodName());
                }
                foodTable.addAll(aList);
                adapter.notifyDataSetChanged();
            }
            catch (Exception ex) {

            }
        }
    }

}




