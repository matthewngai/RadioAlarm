package com.example.matthew.morningvibe;

import android.app.Activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Matthew on 12/29/2015.
 */
public class RingerFragment extends android.support.v4.app.Fragment {

    private String mParam1;
    private String mParam2;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    AlarmManager alarmManager;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CUSTOM_BUTTON_TITLE = "Enter Custom Stream";
    private static final String RADIO_BUTTON_TITLE = "Select Radio Station";
    private static final String COUNTRY_BUTTON_TITLE = "Select Country";
    private OnFragmentInteractionListener mListener;
    private ArrayList<String> listAlarms = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private boolean isAlarm = false;
    private TimePicker timePicker;
    private Switch toggleButton;
    private PendingIntent pendingIntent;
    SchedulingService schedulingService;
    List<String> list;
    List<String> stationsArray;
    private HashMap<String, String> stations;
    private HashMap<String, String> radio_stations;
    private Button countryButton;
    private Button stationButton;
    private Button okButton;
    private Button customButton;
    private String selectedCountry = "";
    private String selectedStation = "";
    private static final String API_URL_COUNTRY = "http://api.dirble.com/v2/countries/";
    private static final String API_TOKEN = "4ede56b47467ff6a130d421cab";
    private String url;
    private int page = 1;
    private CharSequence items[];
    private CharSequence station_items[];
    private int countryPosition;
    private int stationPosition;
    private String custom_text = "";
//    private StationsTask stationsTask;
    private boolean stationsLoaded = false;

    // TODO: Rename and change types and number of parameters
    public static RingerFragment newInstance(String param1, String param2) {
        RingerFragment fragment = new RingerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public RingerFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        try {
//            stationsTask = new StationsTask();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ringer_fragment, null);

        try {
            timePicker = (TimePicker) view.findViewById(R.id.timePicker);
            toggleButton = (Switch) view.findViewById(R.id.toggle);


            list = new ArrayList<>();
            stationsArray = new ArrayList<>();
            stations = new HashMap<>();
            radio_stations = new HashMap<>();
            getCountryData();   //fill the data

            items = list.toArray(new CharSequence[list.size()]);
            countryButton = (Button) view.findViewById(R.id.pickCountry);
            stationButton = (Button) view.findViewById(R.id.pickStation);
//            okButton = (Button) view.findViewById(R.id.pickStation);
            customButton = (Button) view.findViewById(R.id.customSelect);


            countryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                    adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int n) {
                            countryPosition = n;
                        }
                    })
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //...
                                    selectedCountry = list.get(countryPosition);
                                    countryButton.setText(list.get(countryPosition));
                                    stationButton.setText("Select Station");
                                    selectedStation = "";
                                    String cc = stations.get(list.get(countryPosition));
                                    url = API_URL_COUNTRY + cc + "/stations?page=" + page + "&per_page=30&token=" + API_TOKEN;
                                    new StationsTask().execute(url);
                                }
                            })
                            .setTitle("Select Country")
                            .show();
                }
            });

            stationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(stationsLoaded) {
                    station_items = stationsArray.toArray(new CharSequence[stationsArray.size()]);

                    AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                    adb.setSingleChoiceItems(station_items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int n) {
                            stationPosition = n;
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //...
                            try {
                                countryButton.setText(COUNTRY_BUTTON_TITLE);

                                stationButton.setText(RADIO_BUTTON_TITLE);
                                selectedStation = radio_stations.get(stationsArray.get(stationPosition));
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setTitle("Select Station")
                    .show();
                } else {

                }
                }
            });

            customButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Enter your custom radio stream: ");

                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText(custom_text);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            custom_text = input.getText().toString();
                            if (custom_text.trim().length() > 0) {
                                customButton.setText(custom_text);
                                selectedCountry = "";
                                stationButton.setText(stationsArray.get(stationPosition));
                                selectedStation = "";
                            } else {
                                customButton.setText(CUSTOM_BUTTON_TITLE);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });


            alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            //set the switch to ON

            toggleButton.setChecked(false);
            //attach a listener to check for changes in state
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Calendar calendar = Calendar.getInstance();
                        if (Build.VERSION.SDK_INT >= 23 ){
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        }
                        else {
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                        }
                            Intent myIntent = new Intent(getActivity(), AlarmReceiver.class);
                            myIntent.putExtra("streamCountry", selectedCountry);
                            myIntent.putExtra("streamLink", selectedStation);

                        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, 0);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Log.d("Ringer", "Alarm On");
                    } else {
                        Log.d("Ringer", "Alarm Off");
                        alarmManager.cancel(pendingIntent);
//                        schedulingService.cancelAlarm();
                    }
//                    Log.v("Switch State=", "" + isChecked);
                }

            });

            return view;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void getCountryData() {
        try {
            InputStream is = getActivity().getAssets().open("stations.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json;
            String name;
            String country_code;
            json = new String(buffer, "UTF-8");

            JSONArray obj = new JSONArray(json);
            for (int i = 0; i < obj.length(); i++) {
                name = obj.getJSONObject(i).getString("name");
                list.add(name);
                country_code = obj.getJSONObject(i).getString("country_code");
                stations.put(name, country_code);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    class StationsTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            stationsArray.clear();
            radio_stations.clear();
            final String TAG = params[0];
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
            try {

                JSONArray obj = new JSONArray(result);
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
                    radio_stations.put(name, radio_station);
                }
                stationsLoaded = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {}
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
