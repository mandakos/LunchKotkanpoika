package com.example.manda.lunchkotkanpoika;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    HTTPGetThread thread;

    Calendar currentTime = Calendar.getInstance();

    public static ListView list;
    public static TextView dayofweek, noResult;
    Button find;
    public static EditText editDate;
    public static TextView date;

    ArrayList<String> menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        date = (TextView) findViewById(R.id.tvDate);
        dayofweek = (TextView) findViewById(R.id.tvWeekday);
        noResult = (TextView) findViewById(R.id.noResult);

        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH); // Jan = 0, dec = 11
        int day = currentTime.get(Calendar.DAY_OF_MONTH);

        // Päivämäärästä String jota voidaan käyttää urlissa
        String urlTime = Integer.toString(year) + "-" + Integer.toString(month + 1) + "-"
                + Integer.toString(day);
        // Päivämäärästä String jota voidaan käyttää textviewissä
        String urlString = Integer.toString(day) + "." + Integer.toString(month + 1) + "."
                + Integer.toString(year);
        date.setText(urlString);
        dayofweek.setText("");

        try {
            // Luodaan url
            URL url = new URL("https://www.amica.fi/api/restaurant/menu/day?date=" + urlTime + "&language=fi&restaurantPageId=66287");

            // Aloitetaan threadi jossa haetaan JSON data urlista
            thread = new HTTPGetThread(url, MainActivity.this);
            thread.start();
            Log.d("TAG", "Thread started ");

        }
        catch (MalformedURLException e) {
            Log.d("TAG", "Error starting thread ");

        }

        // Define edit text and button for searching menus for specific day
        find = (Button) findViewById(R.id.button);
        editDate = (EditText) findViewById(R.id.editDate);

        find.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String getDate = editDate.getText().toString();
                if (isDate(getDate)) {
                    try {
                        // Luodaan url
                        URL url = new URL("https://www.amica.fi/api/restaurant/menu/day?date=" + getDate + "&language=fi&restaurantPageId=66287");

                        // Aloitetaan threadi jossa haetaan JSON data urlista
                        thread = new HTTPGetThread(url, MainActivity.this);
                        thread.start();
                        Log.d("TAG", "Thread started ");

                    } catch (MalformedURLException e) {
                        Log.d("TAG", "Error starting thread ");

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Anna päivämäärä muodossa vvvv-kk-pp", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static boolean isDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
}