package com.example.calorietracker;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StepsTakenActivity extends AppCompatActivity {

    User eachDS;
    List<User> dataList;
    ArrayList<String> data;
    ArrayAdapter<String> adapter;
    ListView lv;

    UserStepsDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_taken);


        db = Room.databaseBuilder(getApplicationContext(),
                UserStepsDatabase.class, "user_database")
                .fallbackToDestructiveMigration()
                .build();

        eachDS = null;
        final TextView stepsTv = (TextView) findViewById(R.id.stepsTaken) ;
        Button submitButton = (Button) findViewById(R.id.button3);

        data = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        lv = findViewById(R.id.lvsteps);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eachDS = dataList.get(position);
                stepsTv.setText(Integer.toString(eachDS.stepsTaken));
            }
        });

        ReadDatabase rdb = new ReadDatabase();
        rdb.execute();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    int steps = Integer.parseInt(stepsTv.getText().toString());
                    if(steps < 1 || steps >= 100000)
                        Toast.makeText(getApplicationContext(), "Enter a valid value",Toast.LENGTH_LONG).show();
                    else
                    {
                        if (eachDS == null) {
                            InsertDatabase idb = new InsertDatabase();
                            idb.execute(steps);
                        }
                        else {
                            //Update
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                            String timeStamp = simpleDateFormat.format(new Date());

                            eachDS.setStepsTaken(steps);
                            eachDS.setTimeEntered(timeStamp);
                            UpdateDatabase udb = new UpdateDatabase();
                            udb.execute(eachDS);

                            eachDS = null;
                        }
                    }

                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Enter a valid value",Toast.LENGTH_LONG).show();
                }


            }
        });

    }

  //Write a method for error/alert here


    private class InsertDatabase extends AsyncTask<Integer, Void, Void>{
        @Override
        protected Void doInBackground(Integer... params) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String timeStamp = simpleDateFormat.format(new Date());
            User user = new User(params[0],timeStamp);
            long id = db.userDAO().insert(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ReadDatabase rdb = new ReadDatabase();
            rdb.execute();
        }
    }

    private class ReadDatabase extends AsyncTask<Void, Void, List<User>>{
        @Override
        protected List<User> doInBackground(Void... voids) {
            List<User> stepsList = db.userDAO().getAll();
            return  stepsList;
        }

        @Override
        protected void onPostExecute(List<User> dailySteps) {
            dataList = dailySteps;
            data.clear();
            for (User each : dailySteps) {
                String demo = "Steps: " + each.stepsTaken + "\n" + "Time: "  + each.timeEntered;
                data.add(demo);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private class UpdateDatabase extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            db.userDAO().updateUsers(users[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ReadDatabase rdb = new ReadDatabase();
            rdb.execute();
        }
    }


}
