package com.example.asus.drawerlayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.asus.drawerlayout.helper.HttpJsonParser;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_Cle = "cle";
    private static final String KEY_Valeur= "valeur";
    private static final String BASE_URL="http://heartrateiot-env.qkidbuxtye.eu-west-1.elasticbeanstalk.com/CloudIOT/";
    private ArrayList<HashMap<String, String>> AVGList;
    LineGraphSeries<DataPoint> series;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Tab2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab2.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab2 newInstance(String param1, String param2) {
        Tab2 fragment = new Tab2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Tab2.FetchAVGAsyncTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab2, container, false);
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

            getActivity().runOnUiThread(new Runnable() {
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


        GraphView graph = (GraphView) getView().findViewById(R.id.graph1);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        for (int j=0; j< AVGList.size();j++)
        {
            HashMap<String, String> s=AVGList.get(j);
            String k= s.get(KEY_Cle);
            String l=s.get(KEY_Valeur);
            String n= k.substring(8,10);
            Log.e("val",n.concat(l));
            series.appendData(new DataPoint(Integer.parseInt(n),Integer.parseInt(l)),true,31);

        }

        series.setColor(Color.rgb(214,77,77));
        graph.addSeries(series);
        series.setDrawDataPoints(true);
        series.setThickness(7);
        graph.getLegendRenderer().setVisible(true);
        series.setTitle("Statistics Per Day");
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(31);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setNumHorizontalLabels(11);
        graph.getGridLabelRenderer().setNumVerticalLabels(11);
        series.setDataPointsRadius(6);
        graph.getViewport().setMinY(40.0);
        graph.getViewport().setMaxY(120.0);
        graph.getViewport().setYAxisBoundsManual(true);
        series.setTitle("Heart Rate per Day");



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20) {
            // If the result code is 20 that means that
            // the user has deleted/updated the movie.
            // So refresh the movie listing
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            startActivity(intent);
        }
    }






}
