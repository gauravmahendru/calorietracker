package com.example.calorietracker;

import android.os.AsyncTask;

import com.example.calorietracker.Model.FoodModel;
import com.example.calorietracker.Model.FoodItemModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class PublicAPI {

    private class APICall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            HttpURLConnection conn = null;
            HttpURLConnection connection = null;

            try {
                URL apiUrl = new URL("https://api.nal.usda.gov/ndb/V2/reports?ndbno=01009&type=f&format=json&api_key=hvqrrNzrAMJ1Oq5kfejg6ErJQodxycnJeVvipHJG");
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
                        FoodModel foodModel = new FoodModel(foodName,calories,fat);
                        System.out.print(calories);
                        System.out.print(fat);
                        break;
                    }
                }
            }
            catch (Exception e) {

            }
        }
    }



    private class APICallSearch extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            HttpURLConnection connection = null;
            try {
                URL apUrl = new URL("https://api.nal.usda.gov/ndb/search/?format=json&q=jam&ds=Standard%20Reference&api_key=r11vc2Cq2KINFYhOn51aoI4YdG9OPTOejNxUOCJ3");
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
                ArrayList<FoodItemModel> foodItems = new ArrayList<>();
                JSONObject obj = new JSONObject(s);
                JSONObject listobj = obj.getJSONObject("list");

                JSONArray array = listobj.getJSONArray("item");
                for (int index = 0; index < array.length(); index++) {
                    JSONObject aObj = array.getJSONObject(index);
                    FoodItemModel foodItem = new FoodItemModel(aObj.getString("ndbno"), aObj.getString("name"));
                    foodItems.add(foodItem);
                }

                System.out.print(foodItems);

            } catch (Exception e) {

            }
        }
    }

}
