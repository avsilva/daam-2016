package pt.iscte.daam.pinpointhint;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by andre.silva on 24/03/2016.
 */
public class PinPointRestClient
{

    public URL url;
    public Map<Marker, Integer> hmap;
    private GoogleMap myMap;

    public PinPointRestClient(final Context context, GoogleMap mMap) throws IOException {

        myMap = mMap;
        try {
            url = new URL("http://46.101.41.76/pinsgeojson/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new getPinPoints().execute();
    }

    private class getPinPoints extends AsyncTask<String, Void, String>  {

        public String json;

        @Override
        protected String doInBackground(String... params)  {

            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                json = convertInputStreamToString(in);
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
                hmap = new HashMap<Marker, Integer>();
                for(int i=0; i<poi.length(); i++) {
                    JSONObject t_poi = poi.getJSONObject(i);
                    JSONObject t_poi_geometry = t_poi.getJSONObject("geometry");
                    JSONObject t_poi_properties = t_poi.getJSONObject("properties");
                    String descr = t_poi_properties.getString("descr");
                    String name = t_poi_properties.getString("name");
                    JSONArray coords =  t_poi_geometry.getJSONArray("coordinates");
                    Double lat = (Double) coords.get(1);
                    Double lon = (Double) coords.get(0);

                    Marker m = myMap.addMarker(new MarkerOptions()
                            .title(descr)
                            .snippet(name)
                            .position(new LatLng(lat, lon)));

                    hmap.put(m, t_poi.getInt("id"));
                }


            } catch (Exception e) {
                Log.i("guideMe", "An exception has occured in the connection - " + e.toString());
            }
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
