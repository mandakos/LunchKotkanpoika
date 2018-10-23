package com.example.manda.lunchkotkanpoika;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class HTTPGetThread extends Thread {

    URL url;
    Activity activity;
    ArrayList<HashMap<String, String>> jsonList = new ArrayList<>();

    private static String json = "";
    private static String getString = "";
    private static String weekday = "";
    private static String date = "";

    public HTTPGetThread(URL url, Activity activity){
        this.url = url;
        this.activity = activity;
        Log.d("TAG", "URL: " + url);
    }


    @Override
    public void run() {
        Log.d("TAG", "Thread RUN ");
        loadStuff();

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                MainActivity.noResult.setText("Tälle päivälle ei löydy ruokalistaa");
                Log.d("TAG", "Thread " + getString);

                if(getString != null) {
                    try {
                        JSONObject jObj = new JSONObject(getString);
                        JSONObject lunchMenu = jObj.getJSONObject("LunchMenu");

                        // Get day of week:
                        weekday = lunchMenu.getString("DayOfWeek");
                        if(weekday != null) {
                            MainActivity.dayofweek.setText(weekday);
                            Log.d("TAG", "Thread RUN " + weekday);
                        } else {
                            MainActivity.dayofweek.setText("");
                        }

                        // Get date:
                        date = lunchMenu.getString("Date");
                        if(weekday != null) {
                            MainActivity.date.setText(date);
                            Log.d("TAG", "Thread RUN " + date);
                        } else {
                            MainActivity.date.setText(MainActivity.editDate.getText().toString());
                        }


                        // JSONArray for menus:
                        JSONArray setMenus = lunchMenu.getJSONArray("SetMenus");
                        String testMenus = lunchMenu.getString("SetMenus");
                        testMenus = testMenus.replace("[", "");
                        testMenus = testMenus.replace("]", "");
                        Log.d("TAG", "testmenus: " + testMenus);

                        for (int i = 0; i < setMenus.length(); i++) {

                            HashMap<String, String> test = new HashMap<>();

                            // get menu name:
                            JSONObject jsonObj = setMenus.getJSONObject(i);
                            String menuName = jsonObj.getString("Name");
                            test.put("MenuName", menuName);
                            Log.d("TAG", "getMenuname: " + menuName);


                            // get meal name:
                            //String getMealArray = jsonObj.getString("Meals");
                            //JSONArray mealArray = new JSONArray(getMealArray);
                            JSONArray mealArray = jsonObj.getJSONArray("Meals");
                            Log.d("TAG", "getMealName: " + mealArray);


                            List<String> meals = new ArrayList<>();

                            for (int j = 0; j < mealArray.length(); j++) {

                                JSONObject getMealName = mealArray.getJSONObject(j);
                                String mealName = getMealName.getString("Name");
                                meals.add(mealName);
                                Log.d("TAG", "getMealName: " + mealName);

                            }

                            if (meals != null) {
                                MainActivity.noResult.setText("");
                                String mealList = Arrays.toString(meals.toArray());
                                mealList = mealList.replace("[", "");
                                mealList = mealList.replace("]", "");
                                test.put("MealName", mealList);
                            }

                            jsonList.add(test);

                        } // End Loop

                    } catch(JSONException e){
                            Log.e("JSONException", "Error: " + e.toString());
                        } // catch (JSONException e)

                        ListAdapter adapter = new SimpleAdapter(activity, jsonList, R.layout.list_item, new String[]{"MenuName", "MealName"}, new int[]{R.id.menuName, R.id.itemMenu});
                        MainActivity.list.setAdapter(adapter);
                    }
                }
        });
    }


    private void loadStuff() {
        Log.d("TAG", "Load stuff");

        HttpURLConnection urlConnection = null;
        try{
            Log.d("TAG", "URL: " + url);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            String allData = fromStream(is);
            getString = allData;
            //Log.d("TAG", "allData: " + allData);

            is.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
    }
    public static String fromStream(InputStream is)
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        /* try parse the array to a JSON object
        try {
            jArr = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }*/

        // return JSON Object
        return json;
    }

}
