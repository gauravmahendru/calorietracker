package com.example.calorietracker;


import android.os.AsyncTask;
import android.util.Pair;

import com.example.calorietracker.Model.FoodItemModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class RestAPI {

    private static final String BASE_URL = "http://192.168.1.104:8080/Assignment15046/webresources/";
    private RestclientAsync restasync;

    public RestAPI() {
        restasync = new RestclientAsync();
    }

    public String getApiCalls(String apiurl) {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try{
            url = new URL(apiurl);
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
        return textResult;
    }


    public void findAllFood() {
        String url = BASE_URL + "calorietracker.foodtable";
        restasync.execute(url, "allFood");
    }

    public void checkLogin(String username, String password) {
        String url = "";
        restasync.execute(url, "checkLogin");
    }



    private class RestclientAsync extends AsyncTask<String, Void, Pair<String, String>>{
        @Override
        protected Pair<String, String> doInBackground(String... strings) {
            String result = "";

            try {
                switch (strings[1]) {
                    case "allFood":
                        result = getApiCalls(strings[0]);
                        break;

                    case "checkLogin":
                        result = getApiCalls(strings[0]);
                        break;
                }
            }
            catch (Exception e) {
            }

            return Pair.create(result, strings[1]);
        }

        @Override
        protected void onPostExecute(Pair<String, String> result) {
            System.out.print(result);

            try {
                switch (result.second) {
                    case "allFood":
                        String x = result.first;
                        System.out.print(x);
                        break;

                    case "checkLogin":
                        break;
                }
            }
            catch (Exception e) {

            }
        }
    }

    private static String SHA1(String words) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        MessageDigest diges = MessageDigest.getInstance("SHA-1");
        byte[] wordBytes = words.getBytes("iso-8859-1");
        diges.update(wordBytes,0,wordBytes.length);
        byte[] sha1hash = diges.digest();
        return hexConverter(sha1hash);
    }

    private static String hexConverter(byte[] value){
        StringBuilder builder = new StringBuilder();
        for (byte by : value) {
            int half = (by >>> 4) & 0x0F;
            int second_half = 0;

            do {
                builder.append((0 <= half) && (half <= 9) ? (char) ('0' + half) : (char)('a'+(half-10)));
                half = by & 0x0F;
            }while (second_half++ < 1);
        }
        return builder.toString();
    }
}
