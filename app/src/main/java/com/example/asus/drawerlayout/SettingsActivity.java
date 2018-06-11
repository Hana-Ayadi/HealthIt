package com.example.asus.drawerlayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.asus.drawerlayout.helper.HttpJsonParser;
import com.example.asus.drawerlayout.helper.MqttPubSub;
import com.example.asus.drawerlayout.MainActivity;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class SettingsActivity extends MainActivity {
    private static final String TAG = "SettingsActivity";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "message";
    private static final String KEY_age = "age";
    private static final String KEY_id = "User_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_Heart = "heart_state";
    private static final String KEY_RelativeNUM = "RelativeNum";
    private static final String KEY_DoctorNUM = "DoctorNum";
    private static final String BASE_URL="http://heartrateiot-env.qkidbuxtye.eu-west-1.elasticbeanstalk.com/CloudIOT/";
    private static String STRING_EMPTY = "";
    EditText name,age,DoctorNum,RelativeNum;
    RadioButton Male,Female,HealthF,HealthNF;
    String data;
    RadioGroup group1,group2;

    private int success;

    String userName,userAge,UserGender,UserRelativeNum,UserState,UserDoctorNum,user_id;

    Button signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        mDrawerLayout.addView(contentView, 0);

        name=(EditText)findViewById(R.id.input_name);
        age=(EditText)findViewById(R.id.input_age);
        Male=(RadioButton) findViewById(R.id.Male);
        Female=(RadioButton) findViewById(R.id.Female);
        HealthF=(RadioButton) findViewById(R.id.HealthF);
        HealthNF=(RadioButton) findViewById(R.id.HealthNF);
        DoctorNum=(EditText)findViewById(R.id.input_mobile_doctor);
        RelativeNum=(EditText)findViewById(R.id.input_mobile);
        signupButton=(Button)findViewById(R.id.btn_signup);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();

            }
        });



    }
    public void addUser()
    {    Boolean chek1=false;

        if (!STRING_EMPTY.equals(name.getText().toString()) &&
                !STRING_EMPTY.equals(age.getText().toString()) &&
                !STRING_EMPTY.equals(RelativeNum.getText().toString())&&
                (Male.isChecked()||Female.isChecked())&&
                (HealthNF.isChecked()||HealthF.isChecked())


                ) {

            userName = name.getText().toString();
            userAge = age.getText().toString();
            UserRelativeNum = RelativeNum.getText().toString();
            UserDoctorNum = DoctorNum.getText().toString();
            if(Male.isChecked())
                UserGender="M";
            else  UserGender="F";
            if(HealthNF.isChecked())
                UserState="NF";
            else  UserState="F";

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
            new AddUserAsyncTask().execute();

        } else {
            Toast.makeText(SettingsActivity.this,
                    "One or more fields left empty!",
                    Toast.LENGTH_LONG).show();

        }

    }
    /**
     * AsyncTask for adding a user
     */
    private class AddUserAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
           MqttPubSub mqtt = new MqttPubSub(getApplicationContext());
          // HttpJsonParser httpJsonParser = new HttpJsonParser();
            /*Map<String, String> httpParams = new HashMap<>();
            //Populating request parameters
            httpParams.put(KEY_USER_NAME, userName);
            httpParams.put(KEY_id, user_id);
            httpParams.put(KEY_age, userAge);
            httpParams.put(KEY_GENDER, UserGender);
            httpParams.put(KEY_Heart, UserState);
            httpParams.put(KEY_RelativeNUM,UserRelativeNum );
            httpParams.put(KEY_DoctorNUM,UserDoctorNum);*/
            JSONObject jsobj = new JSONObject();
            try {
                jsobj.put(KEY_USER_NAME,userName);
                jsobj.put(KEY_id, user_id);
                jsobj.put(KEY_age, userAge);
                jsobj.put(KEY_GENDER, UserGender);
                jsobj.put(KEY_Heart, UserState);
                jsobj.put(KEY_RelativeNUM,UserRelativeNum );
                jsobj.put(KEY_DoctorNUM,UserDoctorNum);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mqtt.sendJsonMQTT(jsobj,"HeartRate");
            /*JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "update_user.php", "POST", httpParams);
            try {
                success = jsonObject.getInt(KEY_SUCCESS);
                data=jsonObject.getString(KEY_DATA);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            return null;
        }

        protected void onPostExecute(String result) {


        }
    }









}
