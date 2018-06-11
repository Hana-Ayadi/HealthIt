package com.example.asus.drawerlayout;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class MesureActivity extends MainActivity implements SensorEventListener {
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
    private int success;
    String UserValue,user_id;
    GifImageView imageGif;
    private static final String BASE_URL="http://heartrateiot-env.qkidbuxtye.eu-west-1.elasticbeanstalk.com/CloudIOT/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_mesure, null, false);
        mDrawerLayout.addView(contentView, 0);

        // Instancier le gestionnaire des capteurs,  le SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Instancier l'accéléromètre
        HRM = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        bpm=(TextView) findViewById(R.id.bpm);
        imageGif=(GifImageView) findViewById(R.id.gifImageView);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d("Accuracy", "onAccuracyChanged - accuracy: " + i);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, HRM);
        super.onPause();

    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, HRM, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }


    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int) event.values[0];
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
            {
                imageGif.setImageResource(R.drawable.heart);
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
                new MesureActivity.AddValueAsyncTask().execute();
            }
        } else
            Log.d("Error", "Unknown sensor type");
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
            /*HttpJsonParser httpJsonParser = new HttpJsonParser();
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
            MqttPubSub ps=new MqttPubSub(getApplicationContext());
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

           /* runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 1) {
                        //Display success message
                        Toast.makeText(MesureActivity.this,
                                "value Added"+data, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MesureActivity.this,
                                "Some error occurred while adding Value"+data,
                                Toast.LENGTH_LONG).show();

                    }
                }
            });*/
        }


    }}
