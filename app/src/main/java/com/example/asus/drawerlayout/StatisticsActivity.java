package com.example.asus.drawerlayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.asus.drawerlayout.helper.HttpJsonParser;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StatisticsActivity extends MainActivity {
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_Cle = "cle";
    private static final String KEY_Valeur= "valeur";
    private static final String BASE_URL="http://heartrateiot-env.qkidbuxtye.eu-west-1.elasticbeanstalk.com/CloudIOT/";
    private ArrayList<HashMap<String, String>> AVGList;

     Button day,month;
    LineGraphSeries<DataPoint> series;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_statistics, null, false);
        mDrawerLayout.addView(contentView, 0);
       day=(Button)findViewById(R.id.angry_btn);
        month=(Button)findViewById(R.id.angry_btn2);
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(StatisticsActivity.this, StatisticsActivity.class);

                StatisticsActivity.this.startActivity(myIntent);

            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent2 = new Intent(StatisticsActivity.this, StatisticsActivityMonth.class);

                StatisticsActivity.this.startActivity(myIntent2);

            }
        });
        new FetchAVGAsyncTask().execute();

    }

    /**
     * Fetches the list of movies from the server
     */
    private class FetchAVGAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(BASE_URL + "Hadoop_AVG_Result.php", "GET", null);
            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONArray Average;
                if (success == 1) {
                    AVGList = new ArrayList<>();
                    Average = jsonObject.getJSONArray(KEY_DATA);
                    //Iterate through the response and populate movies list

                    for (int i = 0; i < Average.length(); i++) {
                        JSONObject avg = Average.getJSONObject(i);
                        String movieId = avg.getString(KEY_Cle);
                        String movieName = avg.getString(KEY_Valeur);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_Cle, movieId);
                        map.put(KEY_Valeur, movieName);
                        AVGList.add(map);
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
                    populateAVGListGraph();
                }
            });
        }

    }

    /**
     * Updating parsed JSON data into Graph
     * */
    private void populateAVGListGraph() {

        GraphView graph = (GraphView) findViewById(R.id.graph1);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        for (int j=0; j< AVGList.size();j++)
         {
             HashMap<String, String> s=AVGList.get(j);
             String k= s.get(KEY_Cle);
             String l=s.get(KEY_Valeur);
             String n= k.substring(8,10);
             Log.e("items",n+l);
             series.appendData(new DataPoint(Integer.parseInt(n),Integer.parseInt(l)),true,31);

         }

        series.setColor(Color.rgb(214,77,77));
        series.setThickness(7);
        //axe vert
        graph.getViewport().setMinY(40.0);
        graph.getViewport().setMaxY(120.0);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setNumHorizontalLabels(11);
        graph.getGridLabelRenderer().setNumVerticalLabels(11);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(31);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getLegendRenderer().setVisible(true);
        series.setTitle("Statistics Per Day");

        //pts
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(6);

        graph.addSeries(series);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20) {
            // If the result code is 20 that means that
            // the user has deleted/updated the movie.
            // So refresh the movie listing
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }





}
