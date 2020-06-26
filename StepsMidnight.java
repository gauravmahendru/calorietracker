package com.example.calorietracker;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.calorietracker.Model.ReportModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class StepsMidnight {

    private String calorieGoal;
    private String calorieConsumed;
    private int stepsTaken;
    private String calorieBurned;
    private double ttlCalorieFromSteps;
    private SharedPreferences sharedPreferences;
    private Context context;

    UserStepsDatabase db = null;

    public StepsMidnight(Context context){this.context = context;}
    public void report(){

        db = Room.databaseBuilder(context,
                UserStepsDatabase.class, "usersteps_Database")
                .fallbackToDestructiveMigration()
                .build();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        calorieGoal = sharedPreferences.getString("CalorieGoal", "0");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CalorieGoal","0");
        editor.commit();
        ReadDB readDB = new ReadDB();
        readDB.execute();
    }

    private class ReadDB extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            int dailySteps = 0;
            List<User> stepsList = db.userDAO().getAll();
            for(User each: stepsList )
            {
                dailySteps += each.getStepsTaken();
            }
            stepsTaken = dailySteps;
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
            calorieConsumed = textResult;
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
                String userId = "";

                JSONArray array = new JSONArray(userInfo);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("userTable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("userTable");
                        System.out.print(jsonObject);
                        userId = jsonObject.getString("userId");
                    }
                }

                url = new URL(BaseUrl + "restws.usertable/findByUserId/" + userId );
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
                    ttlCalorieFromSteps = Double.parseDouble(textResult)* stepsTaken;

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
                String userId = "";

                JSONArray array = new JSONArray(userInfo);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("userTable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("userTable");
                        System.out.print(jsonObject);
                        userId = jsonObject.getString("userId");
                    }
                }

                url = new URL(BaseUrl + "restws.usertable/findByUserIdbmr2/" + userId );
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
                    calorieBurned = Double.toString(totalCaloriesFromRest + ttlCalorieFromSteps);

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
            return calorieBurned;
        }
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            PushAsync pushAsync = new PushAsync();
            pushAsync.execute();
        }
    }

    private class PushAsync extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String BaseUrl = "http://192.168.1.104:8080/Assignment15046/webresources/";
            URL url = null;
            HttpURLConnection connection = null;
            try {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String userInfo = sharedPreferences.getString("User", "");
                String userId = "";

                JSONArray array = new JSONArray(userInfo);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("userTable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("userTable");
                        System.out.print(jsonObject);
                        userId = jsonObject.getString("userId");
                    }
                }
                User user = new User(Integer.parseInt(userId), "timeEntered");
                user.setUid(Integer.parseInt(userId));
                ReportModel reportModel = new ReportModel(Double.parseDouble(calorieConsumed),Double.parseDouble(calorieBurned),stepsTaken,Double.parseDouble(calorieGoal),user);
                String response = "";
                Gson gson =new Gson();
                String postJSON=gson.toJson(reportModel);
                url = new URL(BaseUrl + "restws.reporttable/");
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

}






