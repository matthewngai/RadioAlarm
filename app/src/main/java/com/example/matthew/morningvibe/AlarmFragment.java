package com.example.matthew.morningvibe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AlarmFragment extends android.support.v4.app.Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<String> streamArray = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView lv;
    private HashMap<String, String> stations;
    Intent serviceIntent;
    private boolean isStreamPlaying = false;
    private String selectedStream = "";
    private String name;
    private String country_code;

    private OnFragmentInteractionListener mListener;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmFragment newInstance(String param1, String param2) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        try {
            //needs getActivity(), fragment not a context
            serviceIntent = new Intent(getActivity(), StreamService.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.alarm_fragment, null);
        stations = new HashMap<String,String>();
        String json = null;

        try {
            lv = (ListView) view.findViewById(R.id.lv);
            streamArray = new ArrayList<String>();

            InputStream is = getActivity().getAssets().open("stations.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            JSONArray obj = new JSONArray(json);

            for (int i = 0; i < obj.length(); i++) {
                name = obj.getJSONObject(i).getString("name");
                country_code = obj.getJSONObject(i).getString("country_code");

                streamArray.add(name);
                stations.put(name, country_code);
            }


//            for (int i = 0; i < obj.length(); i++) {
//                String name = obj.getJSONObject(i).getString("name");
//                String radio_station = "";
//
//                JSONArray streams = new JSONArray(obj.getJSONObject(i).getString("streams"));
//                for(int j = 0; j < streams.length(); j++) {
//                    if (j == 0){
//                        radio_station = streams.getJSONObject(j).getString("stream");
//                    }
//                }
//                streamArray.add(name);
//                stations.put(name, radio_station);
//            }

            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, streamArray);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                    String obs = stations.get(lv.getItemAtPosition(position));
                    selectedStream = obs;
//                    System.out.println(obs);
                    try {
//                        itemPlayStopClick();
                        Intent intent = new Intent (getActivity(), CountryList.class);
                        intent.setClassName("com.example.matthew.morningvibe",
                                "com.example.matthew.morningvibe.CountryList");
                        intent.putExtra("name", name);
                        intent.putExtra("country_code", obs);
                        startActivity(intent);
                    }

                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return view;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return view;
    }

//    private void itemPlayStopClick() {
//        if (!isStreamPlaying) {
//            isStreamPlaying = true;
//            playStream();
//        }
//        else {
//            if (isStreamPlaying) {
//                isStreamPlaying = false;
//                stopStream();
//            }
//        }
//    }

//    private void playStream() {
//        try {
//            serviceIntent.putExtra("streamLink", selectedStream);
//            System.out.println(serviceIntent);
//            getActivity().startService(serviceIntent);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopStream() {
//        try {
//            getActivity().stopService(serviceIntent);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        isStreamPlaying = false;
//    }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
