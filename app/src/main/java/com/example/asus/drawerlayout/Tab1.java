package com.example.asus.drawerlayout;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.drawerlayout.helper.HttpJsonParser;
import com.example.asus.drawerlayout.helper.MqttPubSub;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.SENSOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab1 extends Fragment implements SensorEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    SensorManager sensorManager;
    // L'accéléromètre
    Sensor HRM;
    TextView bpm;
    int heart_rateInt=0,nbRate=1;
    List<Integer> rateTab= new ArrayList<>();
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "message";
    private static final String KEY_value = "value";
    private static final String KEY_id = "User_id";
    String data;
    GifImageView imageGif;

    private int success;
    String UserValue,user_id;
    private static final String BASE_URL="http://heartrateiot-env.qkidbuxtye.eu-west-1.elasticbeanstalk.com/CloudIOT/";

    private OnFragmentInteractionListener mListener;

    public Tab1() {
        // Required empty public constructor
    }


    public static Tab1 newInstance(String param1, String param2) {
        Tab1 fragment = new Tab1();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab1, container, false);

    }
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        // initialise your views

        sensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        // Instancier l'accéléromètre
        HRM = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        bpm=(TextView) view.findViewById(R.id.bpmText);
        imageGif=(GifImageView) view.findViewById(R.id.gifImageView);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int) sensorEvent.values[0];
            //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            if(msg.length()==1)
                bpm.setText("00"+msg);
            else
            if(msg.length()==2)
                bpm.setText("0"+msg);
            else
                bpm.setText(msg);
            if(nbRate<=5 && !msg.equals("0"))
            {
                rateTab.add(Integer.parseInt(msg));
                nbRate++;
            }
            else
            if(!msg.equals("0"))
            {    imageGif.setImageResource(R.drawable.heart);
                int sum=0;
                sensorManager.unregisterListener(this, HRM);
                HRM=null;
                for (int i : rateTab) {
                    Log.e("tag", "onSensorChanged: "+i);
                    sum += i;
                }
                heart_rateInt = sum / rateTab.size();
                if(String.valueOf(heart_rateInt).length()==1)
                    bpm.setText("00"+String.valueOf(heart_rateInt));
                else
                if(String.valueOf(heart_rateInt).length()==2)
                    bpm.setText("0"+String.valueOf(heart_rateInt));
                else
                    bpm.setText(String.valueOf(heart_rateInt));
                try {
                    // get all the interfaces
                    List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                    //find network interface wlan0
                    for (NetworkInterface networkInterface : all) {
                        if (!networkInterface.getName().equalsIgnoreCase("wlan0")) continue;
                        //get the hardware address (MAC) of the interface
                        byte[] macBytes = networkInterface.getHardwareAddress();
                        if (macBytes == null) {
                            user_id="";
                        }


                        StringBuilder res1 = new StringBuilder();
                        for (byte b : macBytes) {
                            //gets the last byte of b
                            res1.append(Integer.toHexString(b & 0xFF) + ":");
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        user_id=res1.toString();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                UserValue=String.valueOf(heart_rateInt);
                new Tab1.AddValueAsyncTask().execute();
            }
        } else
            Log.d("Error", "Unknown sensor type");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    public void onPause() {
        sensorManager.unregisterListener(this, HRM);
        super.onPause();

    }

    @Override
    public void onResume() {
        sensorManager.registerListener(this, HRM, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    /**
     * AsyncTask for adding a user
     */
    private class AddValueAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
           /* HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Populating request parameters
            httpParams.put(KEY_id, user_id);
            httpParams.put(KEY_value, UserValue);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "add_rate.php", "POST", httpParams);
            try {
                success = jsonObject.getInt(KEY_SUCCESS);
                data=jsonObject.getString(KEY_DATA);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            MqttPubSub ps=new MqttPubSub(getActivity().getApplicationContext());
            JSONObject js=new JSONObject();
            try {
                js.put(KEY_id, user_id);
                js.put(KEY_value, UserValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ps.sendJsonMQTT(js,"AddRate");
            return null;
        }

        protected void onPostExecute(String result) {

           /* getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 1) {
                        //Display success message
                        Toast.makeText(getActivity(), "value Added"+data, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getActivity(), "Some error occurred while adding movie"+data, Toast.LENGTH_LONG).show();

                    }
                }
            });*/
        }


    }
}
