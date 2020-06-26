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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class BarChartActivity extends AppCompatActivity {

    private Context context;
    private SharedPreferences sharedPreferences;
    private TextView startDate;
    private TextView endDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog.OnDateSetListener dateSetListener2;
    private String startdateS;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        context = this;
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(BarChartActivity.this,android.R.style.Theme_Material_Light_Dialog_MinWidth, dateSetListener,year,month,day);
                dialog.getWindow();
                dialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(BarChartActivity.this,android.R.style.Theme_Material_Light_Dialog_MinWidth, dateSetListener2,year,month,day);
                dialog.getWindow();
                dialog.show();

            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String Date = month + "/" + dayOfMonth + "/" + year;
                startDate.setText(Date);

                startdateS = year + "-" + month + "-" + dayOfMonth;
            }
        };

        dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String Date = month + "/" + dayOfMonth + "/" + year;
                endDate.setText(Date);

                String endDate = year + "-" + month + "-" + dayOfMonth;
                BarAsync barAsync = new BarAsync();
                barAsync.execute(endDate);
            }
        };
    }


    private class BarAsync extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            String BaseUrl = "http://192.168.1.104:8080/Assignment15046/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";


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
                url = new URL( BaseUrl + "restws.reporttable/changeByDateRange/" + userId + "/" + startdateS + "/" + strings[0]);
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

        @Override
        protected void onPostExecute(String s) {
            List<Double> calorieBurned = new ArrayList<>();
            List<Double> calorieConsumed = new ArrayList<>();
            List<String> finalDate = new ArrayList<>();
            List<BarEntry> barConsumed = new ArrayList<>();
            List<BarEntry> barBurned = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    calorieBurned.add( jsonObject.getDouble("calorieBurned"));
                    calorieConsumed.add(jsonObject.getDouble("calorieConsumed"));
                    finalDate.add(jsonObject.getString("reportDate"));
                    barConsumed.add(new BarEntry(i, Float.parseFloat(Double.toString(calorieConsumed.get(i)))));
                    barBurned.add(new BarEntry(i, Float.parseFloat(Double.toString(calorieBurned.get(i)))));
                }

                barChart = findViewById(R.id.barChart);
                BarDataSet barDataSetConsumed = new BarDataSet(barConsumed, "Calories Consumed");
                barDataSetConsumed.setColor(ColorTemplate.PASTEL_COLORS[0]);
                BarDataSet barDataSetBurned = new BarDataSet(barBurned, "Calories Burned");
                barDataSetBurned.setColor(ColorTemplate.PASTEL_COLORS[1]);
                BarData barData = new BarData(barDataSetConsumed, barDataSetBurned);
                barData.setBarWidth(0.18f);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(finalDate));
                xAxis.setCenterAxisLabels(true);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                barChart.setData(barData);
                barChart.setDragEnabled(true);
                barChart.getXAxis().setAxisMinimum(0f);
                barChart.groupBars(0f, 0.05f, 0.5f);
                barChart.invalidate();



            }

            catch (Exception e) {

            }

            super.onPostExecute(s);
        }

    }

    }
