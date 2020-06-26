package com.example.calorietracker.Model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorietracker.BarChartActivity;
import com.example.calorietracker.CalorieScreenActivity;
import com.example.calorietracker.DDActivity;
import com.example.calorietracker.MapsActivity;
import com.example.calorietracker.PieChartActivity;
import com.example.calorietracker.R;
import com.example.calorietracker.StepsMidnight;
import com.example.calorietracker.StepsTakenActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView date;
    private TextView time;
    private Button submit;
    private TextView calorieGoal;
    private EditText setCalorieGoal;
    private TextView welcome;
    private Button push;
    private SharedPreferences sharedPreferences;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = findViewById(R.id.textView7);
        time = findViewById(R.id.textView8);
        submit = findViewById(R.id.button);
        calorieGoal = findViewById(R.id.textView9);
        setCalorieGoal = findViewById(R.id.editText13);
        welcome = findViewById(R.id.textView3);
        push = findViewById(R.id.button5);


        String inputValue = sharedPreferences.getString("CalorieGoal", "");
        calorieGoal.setText("Calorie Goal: " + inputValue);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = null;
                try {
                    int goal = Integer.parseInt(setCalorieGoal.getText().toString());
                    toast = new Toast(getApplicationContext());

                    if ((goal > 0) && (goal < 10000)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("CalorieGoal", setCalorieGoal.getText().toString());
                        editor.commit();

                        calorieGoal.setText("Calorie Goal: " + goal);
                    } else
                        Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                }

                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(setCalorieGoal.getWindowToken(), 0);
                calorieGoal.setText("");
            }
        });

        try {
        String userInfo = sharedPreferences.getString("User", "");

        JSONArray array = new JSONArray(userInfo);
        for(int i = 0; i<array.length(); i++)
        {
        JSONObject jObj = array.getJSONObject(i);
        if(jObj.has("userTable"));
        {
            JSONObject jsonObject = jObj.getJSONObject("userTable");
            System.out.print(jsonObject);
            String userFirstName = jsonObject.getString("firstName");
            welcome.setText("Welcome " + userFirstName);
        }
            }
        }
        catch (Exception e)
            {
                welcome.setText("Welcome User");
            }


        Thread timeThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                        ((HomeActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                date.setText(simpleDateFormat.format(new Date()));

                                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                                time.setText(simpleDateFormat.format(new Date()));
                            }
                        });
                    }
                    catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        };
        timeThread.start();

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StepsMidnight update = new StepsMidnight(context);
                update.report();
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;

        if (id == R.id.nav_maps) {
            Intent intent = new Intent(context, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_calorietracker) {
            Intent intent = new Intent(context, CalorieScreenActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_stepstaken) {
            Intent intent = new Intent(context, StepsTakenActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_bargraph) {
            Intent intent = new Intent(context, BarChartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_piechart) {
            Intent intent = new Intent(context, PieChartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_dailydiet) {
            Intent intent = new Intent(context, DDActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

