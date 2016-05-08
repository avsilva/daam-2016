package pt.iscte.daam.pinpointhint;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPointStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    public Map<Pin, Integer> hmap;
    private HashMap<Integer, Float> hcolor;
    private GoogleMap myMap;
    private Context myContext;

    private final static String mLogTag = "pin point log";
    private GeoJsonLayer mLayer;
    private ClusterManager<Pin> mClusterManager;
    private Pin clickedClusterItem;
    private Cluster clickedCluster;
    public List<Pin> items;

    protected Marker m;

    // GeoJSON file to download
    private final String mGeoJsonUrl = "http://46.101.41.76/pinsgeojson/";

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

    public void getPins(){
        getPinPoints points = new getPinPoints();
        points.execute();
    }

    public void getClusters(){
        LoadGeoJsonPins downloadGeoJsonPins = new LoadGeoJsonPins(this);
        downloadGeoJsonPins.execute(mGeoJsonUrl);
    }


    public void addNewPinPoint(JSONObject myJsonPin){
        addPinPoint point = new addPinPoint();
        point.execute(myJsonPin);
    }

    public List<Pin> getItems(){
        //return this.items;
        return items;
    }

    public ClusterManager<Pin> getClusterManager(){
        return mClusterManager;
    }

    public void onBackgroundTaskCompleted(){
        mClusterManager.addItems(items);
    }

    private class addPinPoint extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... params) {

            String json = null;
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = null;

                try {
                    builder = new Uri.Builder()
                            .appendQueryParameter("descr", params[0].getString("descr"))
                            .appendQueryParameter("name", params[0].getString("descr"))
                            .appendQueryParameter("type", params[0].getString("type"))
                            .appendQueryParameter("geom", params[0].getString("geometry"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String query = builder.build().getEncodedQuery();

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                int responseCode=urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        json+=line;
                    }
                    Log.e(mLogTag, "SIM FUNCIONOU = "+ line);
                }
                else {
                    json="";
                    Log.e(mLogTag, "NAO FUNCIONOU = "+ responseCode);

                }

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

        }
    }


    private class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {

            //Toast.makeText(myContext, "getInfoWindow " + marker.getTitle(), Toast.LENGTH_LONG).show();
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            String title = "";
            Integer typestr = null;
            if (clickedClusterItem != null) {
                title = clickedClusterItem.getName();
                typestr = clickedClusterItem.getType();
                /*for (Pin item : clickedClusterItem.getItems()) {
                    // Extract data from each item in the cluster as needed
                }*/
            }
            // Getting view from the layout file info_window_layout
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View v = inflater.inflate( R.layout.info_window_layout, null );

            // Getting reference to the TextView to set title
            TextView note = (TextView) v.findViewById(R.id.note);
            TextView type = (TextView) v.findViewById(R.id.type);

            //note.setText(marker.getTitle() );
            note.setText(title);
            type.setText(typestr.toString());

            // Returning the view containing InfoWindow contents
            return v;


        }
    }

    private class LoadGeoJsonPins extends AsyncTask<String, Void, JSONObject> {

        PinPointRestClient caller;

        LoadGeoJsonPins(PinPointRestClient caller) {
            this.caller = caller;
        }

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
         * Sets the color map to change the color of the marker based on pin type
         */
        private void setColorMap() {

            hcolor = new HashMap<Integer, Float>();
            hcolor.put(1, BitmapDescriptorFactory.HUE_RED);
            hcolor.put(2, BitmapDescriptorFactory.HUE_ORANGE);
            hcolor.put(3, BitmapDescriptorFactory.HUE_YELLOW);
            hcolor.put(4, BitmapDescriptorFactory.HUE_GREEN);
            hcolor.put(5, BitmapDescriptorFactory.HUE_CYAN);
            hcolor.put(6, BitmapDescriptorFactory.HUE_AZURE);
            hcolor.put(7, BitmapDescriptorFactory.HUE_BLUE);
            hcolor.put(8, BitmapDescriptorFactory.HUE_VIOLET);
            hcolor.put(9, BitmapDescriptorFactory.HUE_MAGENTA);
            hcolor.put(10, BitmapDescriptorFactory.HUE_ROSE);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            Double lat = null;
            Double lon = null;

            if (jsonObject != null) {

                Log.e(mLogTag, jsonObject.toString());
                mLayer = new GeoJsonLayer(myMap, jsonObject);

                items = null;
                try {
                    items = new PinGeoJonReader().read(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mClusterManager = new ClusterManager<Pin>(myContext, myMap);
                mClusterManager.setRenderer(new PinRenderer(myContext, myMap, mClusterManager));

                myMap.setOnCameraChangeListener(mClusterManager);

                myMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
                mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems());

                myMap.setOnMarkerClickListener(mClusterManager);

                setColorMap();

                MyLocation appLocationManager = new MyLocation(myContext);
                lat = appLocationManager.getLatitude();
                lon = appLocationManager.getLongitude();
                appLocationManager.getLongitude();

                if (lon != null && lat != null){
                    //TODO: add marker for user location
                    getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 17));
                } else {
                    // Add a marker in Lisbon and move the camera
                    LatLng lisbon = new LatLng(38.72, -9.18);
                    getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(lisbon, 12));
                }




                mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Pin>() {
                    @Override
                    public boolean onClusterClick(Cluster<Pin> cluster) {
                        //Log.e(mLogTag, "onClusterItemClick " + cluster.getSize());
                        Toast.makeText(myContext, "ZOOM IN PLEASE!", Toast.LENGTH_LONG).show();
                        clickedCluster = cluster; // remember for use later in the Adapter
                        return false;
                    }
                });

                mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Pin>() {
                    @Override
                    public boolean onClusterItemClick(Pin item) {
                        Log.e(mLogTag, "onClusterItemClick " + item.getPosition().latitude);
                        clickedClusterItem = item;

                        /*m = myMap.addMarker(new MarkerOptions()
                                .title(item.getName())
                                .snippet(item.getName())
                                .position(item.getPosition()));*/
                        //hmap.put(m, 1);

                        return false;
                    }
                });


                mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Pin>() {
                    @Override
                    public void onClusterItemInfoWindowClick(Pin item) {
                        Log.e(mLogTag, "onClusterItemInfoWindowClick " + item.getPosition().latitude);
                    }
                    // Does nothing, but you could go into the user's profile page, for example.
                });

                caller.onBackgroundTaskCompleted();
                //mClusterManager.addItems(items);
            }
        }
    }

    private class PinRenderer extends DefaultClusterRenderer<Pin> {

        public PinRenderer(Context context, GoogleMap map, ClusterManager<Pin> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Pin item, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            //Log.e(mLogTag, "pin = " + item.getName() + item.getType());
            markerOptions.title(item.getName());
            markerOptions.snippet(String.valueOf(item.getIdent()));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hcolor.get(item.getType())));



            /*hmap = new HashMap<Pin, Integer>();
            hmap.put(item, item.getIdent());*/

            /*for (GeoJsonFeature feature : mLayer.getFeatures()) {
                Log.e(mLogTag, "geometry type = " + feature.getGeometry().getType());
            }*/
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
                //hmap = new HashMap<Marker, Integer>();
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

                    //hmap.put(m, t_poi.getInt("id"));
                }


            } catch (Exception e) {
                Log.i("PinPoint", "An exception has occured in the connection - " + e.toString());
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
