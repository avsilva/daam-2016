package pt.iscte.daam.pinpointhint;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.LocationListener;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PinPointRestClient pins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /*MyLocation appLocationManager = new MyLocation(getBaseContext());
        String latitude = appLocationManager.getLatitude();
        appLocationManager.getLongitude();
        Toast.makeText(getBaseContext(), "Latitude: "+latitude, Toast.LENGTH_LONG).show();*/

        if(isConnected()){
            Toast.makeText(getBaseContext(), "Connection ready!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Connection not ready!", Toast.LENGTH_LONG).show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Lisbon and move the camera
        LatLng lisbon = new LatLng(38, -9);
        //mMap.addMarker(new MarkerOptions().position(lisbon).title("Marker in Portugal"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lisbon));
        try {
            pins = new PinPointRestClient(getBaseContext(), mMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }




}
