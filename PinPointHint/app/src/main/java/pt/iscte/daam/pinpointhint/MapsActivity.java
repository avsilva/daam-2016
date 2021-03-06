package pt.iscte.daam.pinpointhint;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.iscte.daam.pinpointhint.common.ActivityUtils;
import pt.iscte.daam.pinpointhint.model.Pin;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PinPointRestClient pins;
    private ActivityUtils pinUtils;
    protected Marker tempMarker;
    protected LatLng latlong;
    protected LatLng latlongDetails;
    RadioButton bUserDetails;
    private static final int REQUEST_ADD_PINPOINT = 2;
    private final static String mLogTag = "pin point log";

    //public Map<Marker, Integer> hmap;
    //private GeoJsonLayer mLayer;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;


    /**
     * Maps name of data set to data (list of LatLngs)
     * Also maps to the URL of the data set for attribution
     */
    //private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();
    private HashMap<String, ArrayList<LatLng>> mLists = new HashMap<String, ArrayList<LatLng>>();

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

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        bUserDetails = (RadioButton) findViewById(R.id.bUserDetails);
        bUserDetails.setText(sharedPreferences.getString("nome", ""));

        Button bUserDetails = (Button) findViewById(R.id.bUserDetails);
        bUserDetails.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MapsActivity.this, UserDetailsActivity.class);
                MapsActivity.this.startActivity(intent);
            }

        });


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

    @Override
    protected void onRestart(){
        super.onRestart();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void showList(View v) {
        Intent i = new Intent(MapsActivity.this, ListActivity.class);
        JSONObject jsonPins = pins.getJSONPins();
        i.putExtra("PINS", jsonPins.toString());
        startActivity(i);
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
                    Intent i = new Intent(MapsActivity.this, DetailsActivity.class);
                    String pinID = marker.getSnippet();
                    String descr = marker.getTitle();
                    latlongDetails = marker.getPosition();
                    i.putExtra("ID", pinID);
                    i.putExtra("DESCR", descr);
                    i.putExtra("LAT",latlongDetails.latitude);
                    startActivity(i);
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

        //add pins to map via rest api call
        try {
            pins = new PinPointRestClient(getBaseContext(), mMap);
            pins.getClusters();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            JSONObject JSONpin = new JSONObject();
            try {
                String encoded_string = data.getStringExtra("FILE");
                JSONpin.put("geometry", "{ 'type': 'Point', 'coordinates': ["+latlong.longitude+", "+latlong.latitude+"] }");
                JSONpin.put("descr", data.getStringExtra("DESCR"));
                JSONpin.put("type", data.getIntExtra("TYPE", 0));
                JSONpin.put("pic", encoded_string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
