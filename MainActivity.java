package com.example.calorietracker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.calorietracker.Model.HomeActivity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Intent alarmIntent;
    private TextView username;
    private TextView password;
    private PendingIntent pendingIntent;
    private Button login;
    private Button signup;
    private AlarmManager alarmManager;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(this, ScheduledIntentService.class);
        pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, TimeUnit.MINUTES.toMillis((60 * 23) + 59), AlarmManager.INTERVAL_DAY, pendingIntent);


        username = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);

        signup = findViewById(R.id.button4);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignupActivity.class);
                startActivity(intent);
            }
        });


        login = findViewById(R.id.button2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = username.getText().toString();
                String passWord = password.getText().toString();

                if ((!userName.trim().isEmpty()) && (!passWord.trim().isEmpty())) {
                    Login login = new Login();
                    login.execute(userName, passWord);
                } else {
                    showAlert("Input is incorrect", "Username or Password is incorrect");
                }
            }
        });


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userInfo = sharedPreferences.getString("UserInfo", "");
        if (!userInfo.isEmpty()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

        }

    }

    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "DISMISS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public class Login extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String BaseUrl = "http://192.168.1.104:8080/Assignment15046/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";
            try {
                url = new URL(BaseUrl + "restws.credentialtable/findByUsernameANDfindByPasswordHash/" + strings[0] + "/" + SHA1(strings[1]));
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
            if (s.trim().length() > 2) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("User", s);
                editor.commit();

                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                showAlert("Invalid", "Username or Password");
            }
        }
    }

    private static String SHA1(String words) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest diges = MessageDigest.getInstance("SHA-1");
        byte[] wordBytes = words.getBytes("iso-8859-1");
        diges.update(wordBytes, 0, wordBytes.length);
        byte[] sha1hash = diges.digest();
        return hexConverter(sha1hash);
    }

    private static String hexConverter(byte[] value) {
        StringBuilder builder = new StringBuilder();
        for (byte by : value) {
            int half = (by >>> 4) & 0x0F;
            int second_half = 0;

            do {
                builder.append((0 <= half) && (half <= 9) ? (char) ('0' + half) : (char) ('a' + (half - 10)));
                half = by & 0x0F;
            } while (second_half++ < 1);
        }
        return builder.toString();
    }


}
