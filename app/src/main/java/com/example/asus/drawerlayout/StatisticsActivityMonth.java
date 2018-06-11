package com.example.asus.drawerlayout;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.renderscript.Sampler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.asus.drawerlayout.helper.HttpJsonParser;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class StatisticsActivityMonth extends MainActivity {
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_Cle = "month";
    private static final String KEY_Valeur= "value";
    private static final String BASE_URL="http://heartrateiot-env.qkidbuxtye.eu-west-1.elasticbeanstalk.com/CloudIOT/";
    Button month,day;
    private ArrayList<HashMap<String, String>> AVGMonthList;
    private String Month,ValueMin,ValueAVG,ValueMax;
    BarChart barChart;
    GraphView graph;
    ArrayList<BarEntry> barEntries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_statistics_month, null, false);
        mDrawerLayout.addView(contentView, 0);
       day=(Button)findViewById(R.id.angry_btn);
        month=(Button)findViewById(R.id.angry_btn2);
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(StatisticsActivityMonth.this, StatisticsActivity.class);

                StatisticsActivityMonth.this.startActivity(myIntent);

            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(StatisticsActivityMonth.this, StatisticsActivityMonth.class);

                StatisticsActivityMonth.this.startActivity(myIntent);

            }
        });
        new FetchAVGMonthAsyncTask().execute();


}
    private class FetchAVGMonthAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(BASE_URL + "fetch_all_AvgMonth.php", "GET", null);
            JSONObject jsonObject2 = httpJsonParser.makeHttpRequest(BASE_URL + "fetch_MinMonth.php", "GET", null);
            JSONObject jsonObject3 = httpJsonParser.makeHttpRequest(BASE_URL + "fetch_MaxMonth.php", "GET", null);

            //Recuperer le avg
            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONArray Average;
                if (success == 1) {
                    Average = jsonObject.getJSONArray(KEY_DATA);
                    //Iterate through the response and populate  list

                    for (int i = 0; i < Average.length(); i++) {
                        JSONObject avg = Average.getJSONObject(i);
                        Log.e("avg",avg.toString());
                         Month = avg.getString(KEY_Cle);
                         ValueAVG = avg.getString(KEY_Valeur);


                    }
                }
                else Log.e("Hello","Hello");

                int success2 = jsonObject2.getInt(KEY_SUCCESS);
                JSONArray Average2;
                if (success2 == 1) {
                    Average2 = jsonObject2.getJSONArray(KEY_DATA);
                    //Iterate through the response and populate  list
                    Log.e("success",Average2.toString());
                    for (int i = 0; i < Average2.length(); i++) {
                        JSONObject avg2 = Average2.getJSONObject(i);
                        Log.e("Min",avg2.toString());
                        ValueMin = avg2.getString(KEY_Valeur);


                    }
                }
                else Log.e("Hello","Hello");
                //Recupperer max
                int success3 = jsonObject3.getInt(KEY_SUCCESS);
                JSONArray Average3;
                if (success2 == 1) {
                    Average3 = jsonObject3.getJSONArray(KEY_DATA);
                    //Iterate through the response and populate  list

                    for (int i = 0; i < Average3.length(); i++) {
                        JSONObject avg3 = Average3.getJSONObject(i);
                        Log.e("Max",avg3.toString());
                        ValueMax = avg3.getString(KEY_Valeur);


                    }
                }
                else Log.e("Hello","Hello");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            runOnUiThread(new Runnable() {
                public void run() {
                    populateAVGMonthListGraph();
                }
            });
        }

    }
    private void populateAVGMonthListGraph() {


        graph = (GraphView) findViewById(R.id.graph1);

        Log.e("success",Month+ValueMin);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                //Min
                new DataPoint(1, Integer.parseInt(ValueMin)),
                //AVG
                new DataPoint(2, Integer.parseInt(ValueAVG)),
                //Max
                new DataPoint(3, Integer.parseInt(ValueMax)),

        });

        graph.addSeries(series);
        graph.getViewport().setMinY(40.0);
        graph.getViewport().setMaxY(120.0);
        graph.getViewport().setYAxisBoundsManual(true);

          series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb(214,77,77);

            }
        });

        series.setSpacing(30);
        graph.getLegendRenderer().setTextColor(Color.rgb(108,108,108));
        graph.getLegendRenderer().setTextSize(16);
        graph.getGridLabelRenderer().setNumVerticalLabels(11);
        graph.getGridLabelRenderer().setNumHorizontalLabels(11);



        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);



    }

}


