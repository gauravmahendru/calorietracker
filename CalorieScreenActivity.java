package com.example.calorietracker;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CalorieScreenActivity extends AppCompatActivity {

    private String goalCaloriesSet;
    private int totalStepsTaken;
    private String totalcaloriesConsumed;
    private double totalcaloriesBurned;
    private String restCalorie;
    private double totalCaloriesFromSteps;
    private String totalCaloriesBurned;

    private SharedPreferences sharedPreferences;
    private Context context;

    UserStepsDatabase db = null;
    TextView goalSet;
    TextView caloriesConsumed;
    TextView caloriesBurned;
    TextView stepsTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_screen);

        context = this;

        goalSet = findViewById(R.id.textView5);
        stepsTaken = findViewById(R.id.textView16);
        caloriesConsumed = findViewById(R.id.textView11);
        caloriesBurned = findViewById(R.id.textView12);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        goalCaloriesSet = sharedPreferences.getString("CalorieGoal", "0");
        goalSet.setText(goalCaloriesSet);

        ReadDB readDB = new ReadDB();
        readDB.execute();
    }

    private class ReadDB extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            int dailySteps = 0;
            db = Room.databaseBuilder(context,
                    UserStepsDatabase.class, "usersteps_Database")
                    .fallbackToDestructiveMigration()
                    .build();
            List<User> stepsList = db.userDAO().getAll();
            for(User each: stepsList )
            {
                dailySteps += each.getStepsTaken();
            }
            totalStepsTaken = dailySteps;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            GetConsumption getConsumption =new GetConsumption();
            getConsumption.execute();
        }
    }

    private class GetConsumption extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {


            String BaseUrl = "http://192.168.1.104:8080/Assignment15046/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";

            try{

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String userInfo = sharedPreferences.getString("User", "");
                String userid = "";

                JSONArray array = new JSONArray(userInfo);
                for(int i = 0; i<array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("userTable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("userTable");
                        System.out.print(jsonObject);
                        userid = jsonObject.getString("userId");
                    }
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String x =(simpleDateFormat.format(new Date()));

                url = new URL( BaseUrl + "restws.consumptiontable/totalCaloriesConsumed/" + userid + "/" + x );
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
            totalcaloriesConsumed = textResult;
            return textResult;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            Burned burn = new Burned();
            burn.execute();
        }

    }

    private class Burned extends AsyncTask<Void, Void, String>{


        @Override
        protected String doInBackground(Void... voids) {

            String BaseUrl = "http://192.168.1.104:8080/Assignment15046/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";

            try {

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String userInfo = sharedPreferences.getString("User", "");
                String userid = "";

                JSONArray array = new JSONArray(userInfo);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("userTable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("userTable");
                        System.out.print(jsonObject);
                        userid = jsonObject.getString("userId");
                    }
                }

                url = new URL(BaseUrl + "restws.usertable/findCalorieperstep/" + userid );
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                    System.out.println(textResult);
                }
                try {
                    totalCaloriesFromSteps = Double.parseDouble(textResult)* totalStepsTaken;

                }
                catch (Exception e){
                    e.getMessage();
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
            return textResult;
        }

        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            RestCalories restCalories = new RestCalories();
            restCalories.execute();
        }

    }

    private class RestCalories extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String BaseUrl = "http://192.168.1.104:8080/Assignment15046/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";

            try {

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String userInfo = sharedPreferences.getString("User", "");
                String userid = "";

                JSONArray array = new JSONArray(userInfo);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("userTable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("userTable");
                        System.out.print(jsonObject);
                        userid = jsonObject.getString("userId");
                    }
                }

                url = new URL(BaseUrl + "restws.usertable/findLevelOfActivity/" + userid );
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                    System.out.println(textResult);
                }
                try {
                    double totalCaloriesFromRest = Double.parseDouble(textResult);
                    totalCaloriesBurned = Double.toString(totalCaloriesFromRest + totalCaloriesFromSteps);

                }
                catch (Exception e){
                    e.getMessage();
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
            return totalCaloriesBurned;
        }
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            caloriesConsumed.setText(totalcaloriesConsumed);
            caloriesBurned.setText(totalCaloriesBurned);
            stepsTaken.setText(Integer.toString(totalStepsTaken));
        }
    }

}



