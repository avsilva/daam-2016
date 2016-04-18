package pt.iscte.daam.pinpointhint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPointStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.iscte.daam.pinpointhint.model.Pin;


/**
 * Created by andre.silva on 24/03/2016.
 */
public class PinPointRestClient
{

    private URL url;
    public Map<Marker, Integer> hmap;
    private GoogleMap myMap;
    private Context myContext;

    private final static String mLogTag = "pin point log";
    private GeoJsonLayer mLayer;
    private ClusterManager<Pin> mClusterManager;

    // GeoJSON file to download
    private final String mGeoJsonUrl = "http://46.101.41.76/pinsgeojson/";
    //private final String mGeoJsonUrl2 = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";

    public PinPointRestClient(final Context mcontext, GoogleMap mMap) throws IOException {

        myMap = mMap;
        myContext = mcontext;
        try {
            url = new URL(mGeoJsonUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected GoogleMap getMap() {
        return myMap;
    }

    public void getPoints1(){
        getPinPoints points = new getPinPoints();
        points.execute();
    }

    public void getPoints2(){
        DownloadGeoJsonFile downloadGeoJsonFile = new DownloadGeoJsonFile();
        downloadGeoJsonFile.execute(mGeoJsonUrl);
    }


    private class DownloadGeoJsonFile extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                // Open a stream from the URL
                InputStream stream = new URL(params[0]).openStream();

                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    // Read and save each line of the stream
                    result.append(line);
                }

                // Close the stream
                reader.close();
                stream.close();

                // Convert result to JSONObject
                return new JSONObject(result.toString());
            } catch (IOException e) {
                Log.e(mLogTag, "GeoJSON file could not be read");
            } catch (JSONException e) {
                Log.e(mLogTag, "GeoJSON file could not be converted to a JSONObject");
            }
            return null;
        }

        /**
         * Assigns a color based on the given magnitude
         */
        private float magnitudeToColor(double magnitude) {
            if (magnitude < 1.0) {
                return BitmapDescriptorFactory.HUE_CYAN;
            } else if (magnitude < 2.5) {
                return BitmapDescriptorFactory.HUE_GREEN;
            } else if (magnitude < 4.5) {
                return BitmapDescriptorFactory.HUE_YELLOW;
            } else {
                return BitmapDescriptorFactory.HUE_RED;
            }
        }

        /**
         * Adds a point style to all features to change the color of the marker based on its magnitude
         * property
         */
        private void addColorsToMarkers() {
            // Iterate over all the features stored in the layer
            for (GeoJsonFeature feature : mLayer.getFeatures()) {
                // Check if the magnitude property exists
                if (feature.hasProperty("mag") && feature.hasProperty("place")) {
                    double magnitude = Double.parseDouble(feature.getProperty("mag"));

                    // Get the icon for the feature
                    BitmapDescriptor pointIcon = BitmapDescriptorFactory
                            .defaultMarker(magnitudeToColor(magnitude));

                    // Create a new point style
                    GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();

                    // Set options for the point style
                    pointStyle.setIcon(pointIcon);
                    pointStyle.setTitle("Magnitude of " + magnitude);
                    pointStyle.setSnippet("Earthquake occured " + feature.getProperty("place"));

                    // Assign the point style to the feature
                    feature.setPointStyle(pointStyle);
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {

                Log.e(mLogTag, jsonObject.toString());

                // Create a new GeoJsonLayer, pass in downloaded GeoJSON file as JSONObject
                /*mLayer = new GeoJsonLayer(myMap, jsonObject);
                // Add the layer onto the map
                addColorsToMarkers();
                mLayer.addLayerToMap();*/

                List<Pin> items = null;
                try {
                    items = new PinGeoJonReader().read(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.72, -9.18), 10));
                mClusterManager = new ClusterManager<Pin>(myContext, myMap);
                myMap.setOnCameraChangeListener(mClusterManager);
                mClusterManager.addItems(items);
            }
        }
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
