package com.example.calorietracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class PieChartActivity extends AppCompatActivity {

    private TextView Date;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private SharedPreferences sharedPreferences;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        context = this;

        Date = findViewById(R.id.Date);

        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(PieChartActivity.this,android.R.style.Theme_Material_Light_Dialog_MinWidth, dateSetListener,year,month,day);
                dialog.getWindow();
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String date = month + "/" + dayOfMonth + "/" + year;
                Date.setText(date);

                String date2 = year + "-" + month + "-" + dayOfMonth;
                APIForPie apiForPie = new APIForPie();
                apiForPie.execute(date2);
            }
        };
    }

    private class APIForPie extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String BaseUrl = "http://192.168.1.104:8080/Assignment15046/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String txtResult = "";

            try{

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String userInfo = sharedPreferences.getString("User", "");
                String userId = "";

                JSONArray array = new JSONArray(userInfo);
                for(int i = 0; i<array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("userTable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("userTable");
                        System.out.print(jsonObject);
                        userId = jsonObject.getString("userId");
                    }
                }

                url = new URL( BaseUrl + "restws.reporttable/getTotalCaloriesConsumedBurnedANDRemainingCalories/" + userId + "/" + strings[0] );
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    txtResult += inStream.nextLine();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
            return txtResult;


        }

        protected void onPostExecute(String aVoid) {
            try {
                String response = aVoid.replace("{", "");
                response = response.replace("}", "");
                String[] array = response.split(",");
                PieChart pieChart = findViewById(R.id.pieChart);
                List<PieEntry> pieEntries = new ArrayList<>();
                pieEntries.add(new PieEntry(Float.parseFloat(((array[2]).split("="))[1]), "Calories Burned"));
                pieEntries.add(new PieEntry(Float.parseFloat(((array[0]).split("="))[1]), "Calories Consumed"));
                pieEntries.add(new PieEntry(Float.parseFloat(((array[1]).split("="))[1]), "Calories Remaining"));
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
                pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
                pieDataSet.setValueTextSize(20);
                PieData pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);
                pieChart.invalidate();

            }
            catch (Exception e) {

            }
            super.onPostExecute(aVoid);

        }
    }
}


