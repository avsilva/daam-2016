package pt.iscte.daam.pinpointhint;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.iscte.daam.pinpointhint.common.ActivityUtils;
import pt.iscte.daam.pinpointhint.model.Pin;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PinPointRestClient pins;
    private ActivityUtils pinUtils;
    protected Marker tempMarker;
    protected LatLng latlong;
    public Map<Marker, Integer> hmap;

    private static final int REQUEST_ADD_PINPOINT = 2;

    private final static String mLogTag = "pin point log";
    private GeoJsonLayer mLayer;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    /**
     * Maps name of data set to data (list of LatLngs)
     * Also maps to the URL of the data set for attribution
     */
    //private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();
    private HashMap<String, ArrayList<LatLng>> mLists = new HashMap<String, ArrayList<LatLng>>();



    // GeoJSON file to download
    //private final String mGeoJsonUrl = "http://46.101.41.76/pinsgeojson/";
    //private final String mGeoJsonUrl = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected GoogleMap getMap() {
        return mMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //TODO: get current location
        /*MyLocation appLocationManager = new MyLocation(getBaseContext());
        String latitude = appLocationManager.getLatitude();
        appLocationManager.getLongitude();
        Toast.makeText(getBaseContext(), "Latitude: "+latitude, Toast.LENGTH_LONG).show();*/

        if (isConnected()) {
            Toast.makeText(getBaseContext(), "Connection ready!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Connection not ready!", Toast.LENGTH_LONG).show();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setUpMap();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void showPins(View v) {
        mOverlay.clearTileCache();
        mOverlay.setVisible(false);
        List<Pin> nPins = pins.getItems();
        ClusterManager<Pin> clusterMngr = pins.getClusterManager();
        clusterMngr.addItems(nPins);
    }

    public void heatMap(View v) {

        List<Pin> nPins = pins.getItems();
        ClusterManager<Pin> clusterMngr = pins.getClusterManager();
        clusterMngr.clearItems();
        clusterMngr.getClusterMarkerCollection().clear();
        clusterMngr.getMarkerCollection().clear();
        //Toast.makeText(getBaseContext(), "Show heat map! "+ nPins.size(), Toast.LENGTH_LONG).show();

        pinUtils = new ActivityUtils();
        ArrayList<LatLng> list = pinUtils.readItems(nPins);
        mLists.put("Pins", list);

        // Check if need to instantiate (avoid setData etc twice)
        if (mProvider == null) {
            mProvider = new HeatmapTileProvider.Builder().data(mLists.get("Pins")).build();
            mOverlay = getMap().addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        } else {
            mProvider.setData(mLists.get("Pins"));
            mOverlay.clearTileCache();
            mOverlay.setVisible(true);
        }
    }


    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        mMap.addMarker(markerOptions);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        //mMap = googleMap;
        if (mMap != null) {
            return;
        }
        mMap = map;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTitle().compareTo("Inserir Pin") == 0) {

                    Intent i = new Intent(MapsActivity.this, SubmissionActivity.class);
                    i.putExtra("LAT", latlong.latitude);
                    i.putExtra("LONG", latlong.longitude);
                    startActivityForResult(i, REQUEST_ADD_PINPOINT);
                } else {
                    Log.i("pin point", "Click POIDetailsActivity = ");
                    //Log.i("pin point", "Click POIDetailsActivity = " + hmap.get(marker));
                    /*Intent i = new Intent(MainActivity.this, POIDetailsActivity.class);
                    i.putExtra("id", hmap.get(marker));
                    startActivity(i);*/
                }
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (tempMarker != null) {
                    tempMarker.remove();
                }

                //add a new marker to the map
                tempMarker = mMap.addMarker(new MarkerOptions()
                        .title("Inserir Pin")
                        .position(latLng));

                latlong = latLng;

            }
        });

        // Add a marker in Lisbon and move the camera
        LatLng lisbon = new LatLng(38, -9);

        //TODO: modify bubble style
        /*IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
        iconFactory.setContentRotation(90);
        addIcon(iconFactory, "lisbon", lisbon);*/

        //mMap.addMarker(new MarkerOptions().position(lisbon).title("Marker in Portugal"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lisbon));

        //add bubble to map via rest api call
        try {
            pins = new PinPointRestClient(getBaseContext(), mMap);

            //reads data from rest api and shows simple bubbles
            //pins.getPins();

            //reads data from rest api and shows clustered bubbles
            pins.getClusters();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            //itemCalc.setText(data.getStringExtra("RESULT"));

            JSONObject JSONpin = new JSONObject();
            try {
                JSONpin.put("geometry", "{ 'type': 'Point', 'coordinates': ["+latlong.longitude+", "+latlong.latitude+"] }");
                JSONpin.put("descr", data.getStringExtra("DESCR"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Log.e(mLogTag, "SIM FUNCIONOU = "+ JSONpin.toString());
            pins.addNewPinPoint(JSONpin);

        }
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://pt.iscte.daam.pinpointhint/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://pt.iscte.daam.pinpointhint/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
