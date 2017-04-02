package com.roots;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText etProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btn_calc = (Button) findViewById(R.id.button);
        final Button btn_map = (Button) findViewById(R.id.showOnMap);
        final Button btn_jam = (Button) findViewById(R.id.btnJam);
        final TextView texFare = (TextView) findViewById(R.id.textFare);
        etProblem = (EditText) findViewById(R.id.et_problem);

        final MaterialSpinner mFrom = (MaterialSpinner) findViewById(R.id.spinner);
        final MaterialSpinner mTo = (MaterialSpinner) findViewById(R.id.spinner2);

        final ArrayList<String> mStops = new ArrayList<String>();

        final Map<String, Integer> mStopsDistances = new HashMap<>();

        final Map<String, ParseGeoPoint> mStopsLatLong = new HashMap<>();

        final MaterialDialog rLoader = new MaterialDialog.Builder(this)
                .title("Please Wait")
                .content("Fetching Routes")
                .cancelable(false)
                .progress(true, 0)
                .show();

        btn_map.setVisibility(View.INVISIBLE);
        texFare.setVisibility(View.INVISIBLE);
        btn_jam.setVisibility(View.INVISIBLE);
        etProblem.setVisibility(View.GONE);

        ParseQuery<ParseObject> mStopsQuery = ParseQuery.getQuery("MatadorStops");
        mStopsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                rLoader.dismiss();
                if (e == null) {
                    for (ParseObject mStop : objects) {
                        mStops.add(mStop.getString("name"));
                        mStopsLatLong.put(mStop.getString("name"),
                                mStop.getParseGeoPoint("coordinates"));
                    }
                    mFrom.setItems(mStops);
                    mFrom.setSelectedIndex(0);

                    final MaterialDialog dLoader = new MaterialDialog.Builder(MainActivity.this)
                            .title("Please Wait")
                            .content("Fetching Destinations")
                            .cancelable(false)
                            .progress(true, 0)
                            .show();
                    ParseQuery<ParseObject> mRoutesQuery = ParseQuery.getQuery("MatadorRoutes");
                    mRoutesQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                    mRoutesQuery.whereEqualTo("mStops", mStops.get(0));
                    mRoutesQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            dLoader.dismiss();
                            if (e == null) {
                                mTo.setItems(StringArraySkip(object.getJSONArray("mStops"),
                                        mStops.get(0)));
                                mTo.setSelectedIndex(0);
                                JSONToMap(mStopsDistances, object.getJSONArray("mStops"),
                                        object.getJSONArray("mDistances"));
                            } else {
                                Toast.makeText(MainActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        mFrom.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, final String item) {
                btn_map.setVisibility(View.INVISIBLE);
                texFare.setVisibility(View.INVISIBLE);
                btn_jam.setVisibility(View.INVISIBLE);
                etProblem.setVisibility(View.GONE);
                final MaterialDialog dLoader = new MaterialDialog.Builder(MainActivity.this)
                        .title("Please Wait")
                        .content("Fetching Destinations")
                        .progress(true, 0)
                        .show();
                ParseQuery<ParseObject> mRoutesQuery = ParseQuery.getQuery("MatadorRoutes");
                mRoutesQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                mRoutesQuery.whereEqualTo("mStops", item);
                mRoutesQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        dLoader.dismiss();
                        if (e == null) {
                            mTo.setItems(StringArraySkip(object.getJSONArray("mStops"), item));
                            mTo.setSelectedIndex(0);
                            JSONToMap(mStopsDistances, object.getJSONArray("mStops"),
                                    object.getJSONArray("mDistances"));

                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mTo.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                btn_map.setVisibility(View.INVISIBLE);
                texFare.setVisibility(View.INVISIBLE);
                btn_jam.setVisibility(View.INVISIBLE);
                etProblem.setVisibility(View.GONE);
            }
        });

        btn_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTo.getText().equals("Choose Destination")) {
                    Toast.makeText(MainActivity.this, "Destination not selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int total_dist = Math.abs(mStopsDistances.get(mFrom.getText()) -
                        mStopsDistances.get(mTo.getText()));

                if (total_dist <= 3) {
                    texFare.setText(R.string.fare_rs_5);
                } else if (total_dist <= 7) {
                    texFare.setText(R.string.fare_rs_8);
                } else {
                    texFare.setText(R.string.fare_rs_10);
                }

                btn_map.setVisibility(View.VISIBLE);
                texFare.setVisibility(View.VISIBLE);
                btn_jam.setVisibility(View.VISIBLE);
                etProblem.setVisibility(View.VISIBLE);
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
                myIntent.putExtra("from_lat", mStopsLatLong.get(mFrom.getText()).getLatitude()); //Optional parameters
                myIntent.putExtra("from_long", mStopsLatLong.get(mFrom.getText()).getLongitude()); //Optional parameters
                myIntent.putExtra("to_lat", mStopsLatLong.get(mTo.getText()).getLatitude()); //Optional parameters
                myIntent.putExtra("to_long", mStopsLatLong.get(mTo.getText()).getLongitude()); //Optional parameters
                startActivity(myIntent);
            }
        });

        btn_jam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*sendNotification("Traffic Jam Reported On: " + mFrom.getText().toString()
                        + "-" + mTo.getText().toString() + " Route");
                btn_jam.setEnabled(false);*/
                String problem = etProblem.getText().toString();
                if (TextUtils.isEmpty(problem)) {
                    Toast.makeText(MainActivity.this, "Please enter the problem.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                sendNotification(problem);
            }
        });
    }

    public void JSONToMap(Map<String, Integer> mStopsDist, JSONArray stops, JSONArray distances) {
        if (stops != null) {
            for (int i = 0; i < stops.length(); i++) {
                try {
                    mStopsDist.put(stops.getString(i), distances.getInt(i));
                } catch (JSONException e) {
                    Log.e(TAG, "JSONToMap: ", e);
                }
            }
        }
    }

    public ArrayList<String> StringArraySkip(JSONArray items, String skip) {
        ArrayList<String> itemsList = new ArrayList<String>();
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                try {
                    if (!items.getString(i).equalsIgnoreCase(skip)) {
                        itemsList.add(items.getString(i));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "StringArraySkip: ", e);
                }
            }
        }
        return itemsList;
    }

    public void sendNotification(String msg) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("message", msg);
        ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
            public void done(String success, ParseException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, R.string.problem_submitted_successfully,
                            Toast.LENGTH_SHORT).show();
                    etProblem.setText("");
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.error_submitting_problem),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "done: ", e);
                }
            }
        });
    }
}