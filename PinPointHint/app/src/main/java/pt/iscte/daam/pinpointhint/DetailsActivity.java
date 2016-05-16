package pt.iscte.daam.pinpointhint;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class DetailsActivity extends AppCompatActivity {

    public static String APIURL = "http://46.101.41.76/pinsgeojson/";
    private URL url;
    private final static String mLogTag = "pin point log";

    private TextView tvDescr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // get the Intent from calling activity
        Intent i = getIntent();
        final String id = i.getStringExtra("ID");
        final String descr = i.getStringExtra("DESCR");

        tvDescr = (TextView) findViewById(R.id.tvDescr);
        tvDescr.setText(descr);

        //TODO: get details from REST API
        try {
            url = new URL(APIURL + id.toString());
            Log.e(mLogTag, "URL = "+ APIURL + id.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action" + id.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
            }
        });
    }


    private class getPinDetails extends AsyncTask<String, Void, String> {

        public String json;

        @Override
        protected String doInBackground(String... params)  {

            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                //json = convertInputStreamToString(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {

                JSONObject jobj = new JSONObject(result);
                JSONArray poi = jobj.getJSONArray("features");
                for(int i=0; i<poi.length(); i++) {
                    JSONObject t_poi = poi.getJSONObject(i);
                    JSONObject t_poi_geometry = t_poi.getJSONObject("geometry");
                    JSONObject t_poi_properties = t_poi.getJSONObject("properties");
                    String descr = t_poi_properties.getString("descr");
                    String name = t_poi_properties.getString("name");
                }


            } catch (Exception e) {
                Log.i("PinPoint", "An exception has occured in the connection - " + e.toString());
            }
        }
    }

}
