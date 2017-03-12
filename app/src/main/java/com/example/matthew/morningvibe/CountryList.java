package com.example.matthew.morningvibe;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matthew on 12/31/2015.
 */
public class CountryList extends AppCompatActivity {

    ArrayList<String> stationsArray = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private HashMap<String, String> stations;
    private static final String API_URL_COUNTRY = "http://api.dirble.com/v2/countries/";
    private static final String API_TOKEN = "4ede56b47467ff6a130d421cab";

    private String name;
    private String country_code;
    private int page = 1;
    private String url;
    private ListView lv;
    private String selectedStream = "";
    private String streamName = "";
    private String streamStation = "";
    StreamService streamService;
    private String stopCurrentStream = "";
    private CharSequence mTitle;
    Intent serviceIntent;
    private boolean isStreamPlaying = false;
    private Toast mToast;
    private int listItem = 0;
    StationsTask stationsTask = new StationsTask();

    public CountryList() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_list);

        mTitle = getTitle();
        try {
            //anything here..
            mToast = Toast.makeText(this  , "" , Toast.LENGTH_SHORT);
            lv = (ListView) findViewById(R.id.lv);
            name = getIntent().getStringExtra("name");

            country_code = getIntent().getStringExtra("country_code");
            stopCurrentStream = getIntent().getStringExtra("stop");
//            Log.d("STREAM ANME", stopStream);
            System.out.println(stopCurrentStream);
            if (stopCurrentStream != null) {
//                stopForeground(true);
                streamService = new StreamService();
                streamService.stopPlayingStream();

            }
            serviceIntent = new Intent(this, StreamService.class);
            stationsArray = new ArrayList<String>();
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stationsArray);
            stations = new HashMap<String,String>();

            lv.setAdapter(adapter);
            stationsArray.clear();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                    String obs = stations.get(lv.getItemAtPosition(position));
                    selectedStream = obs;
                    streamName = (String) lv.getItemAtPosition(position);

                    try {
                        itemPlayStopClick(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            url = API_URL_COUNTRY + country_code + "/stations?page=" + page + "&per_page=30&token=" + API_TOKEN;
            stationsTask.execute(url);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
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

    class StationsTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            final String TAG = params[0];
//            Log.d(TAG, "doInBackground: !!!");
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
//                System.out.println(result.toString());
                return result.toString();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
//            System.out.println(result);
            try {
                JSONArray obj = new JSONArray(result);
                Log.d("onPostExecute", Integer.toString(obj.length()));
            for (int i = 0; i < obj.length(); i++) {
                String name = obj.getJSONObject(i).getString("name");
                String radio_station = "";

                JSONArray streams = new JSONArray(obj.getJSONObject(i).getString("streams"));
                for(int j = 0; j < streams.length(); j++) {
                    if (j == 0){
                        radio_station = streams.getJSONObject(j).getString("stream");
                    }
                }
                stationsArray.add(name);
                stations.put(name, radio_station);
                Log.d("onPostExecute", name);
                Log.d("onPostExecute", Integer.toString(stationsArray.size()));
                adapter.notifyDataSetChanged();
//                adapter.add(name);
//                System.out.println(adapter);
            }

                if (obj.length() > 0) {
                    page++;
                    url = API_URL_COUNTRY + country_code + "/stations?page=" + page + "&per_page=30&token=" + API_TOKEN;
//                    new StationsTask().execute(url);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}
    }

    private void itemPlayStopClick(int position) {
        Log.d("itemPlayStopClick", "listview item clicked");
        if (isStreamPlaying == false) {
            listItem = position;
            Log.d("itemPlayStopClick-not",Boolean.toString(isStreamPlaying));
            isStreamPlaying = true;

            playStream();
        }
        else {
            if (isStreamPlaying && (listItem == position)) {
                Log.d("itemPlayStopClick-same",Boolean.toString(isStreamPlaying));
//                isStreamPlaying = !isStreamPlaying;
                stopStream();
            }
            else {
                listItem = position;

                isStreamPlaying = true;
                Log.d("itemPlayStopClick-else",Boolean.toString(isStreamPlaying));
                stopStream();
                playStream();
            }
        }
    }

    private void playStream() {
        try {
            serviceIntent.putExtra("streamLink", selectedStream);
            serviceIntent.putExtra("streamName", streamName);
            serviceIntent.putExtra("country_code", country_code);

            startService(serviceIntent);

//            bindService(new Intent(getBaseContext(), StreamService.class),
//                    mConnection, Context.BIND_AUTO_CREATE);
            isStreamPlaying = true;
            mToast.setText("Loading stream...");
            mToast.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopStream() {
        try {
            isStreamPlaying = false;
            stopService(serviceIntent);
            mToast.setText("Stream stopped");
            mToast.show();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
//    protected void onResume() {
//        super.onResume();
//        bindService(new Intent(this, StreamService.class), mConnection,
//                Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mConnection != null)
//            try {
//                unbindService(mConnection);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//    }
//
//    protected ServiceConnection mConnection = new ServiceConnection() {
//
//        public void onServiceConnected(ComponentName className, IBinder binder) {
//            streamService = ((DefaultBinder) binder).getService();
//        }
//        public void onServiceDisconnected(ComponentName className) {
//            streamService = null;
//        }
//    };

}

