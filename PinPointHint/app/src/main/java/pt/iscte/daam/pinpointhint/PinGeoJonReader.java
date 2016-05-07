package pt.iscte.daam.pinpointhint;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import pt.iscte.daam.pinpointhint.model.Pin;

/**
 * Created by andre.silva on 16/04/2016.
 */
public class PinGeoJonReader {

    private static final String REGEX_INPUT_BOUNDARY_BEGINNING = "\\A";
    private final static String mLogTag = "andre";

    public List<Pin> read(InputStream inputStream) throws JSONException {
        List<Pin> items = new ArrayList<Pin>();
        String json = new Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            String name = object.getString("name");
            int id = object.getInt("id");
            int type = object.getInt("type");
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            items.add(new Pin(id, name, type, lat, lng));
        }
        return items;
    }

    public List<Pin> read(String json) throws JSONException {
        List<Pin> items = new ArrayList<Pin>();
        //String json = new Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();

        JSONObject jobj = new JSONObject(json);
        JSONArray poi = jobj.getJSONArray("features");
        for(int i=0; i<poi.length(); i++) {
            //Log.e(mLogTag, "item "+i);
            JSONObject t_poi = poi.getJSONObject(i);
            int id = t_poi.getInt("id");
            //Log.e(mLogTag, "item andre "+id);

            JSONObject t_poi_geometry = t_poi.getJSONObject("geometry");
            JSONObject t_poi_properties = t_poi.getJSONObject("properties");
            //String descr = t_poi_properties.getString("descr");
            String name = t_poi_properties.getString("name");

            int type = t_poi_properties.getInt("type");
            JSONArray coords =  t_poi_geometry.getJSONArray("coordinates");
            Double lat = (Double) coords.get(1);
            Double lon = (Double) coords.get(0);
            LatLng position = new LatLng(lat, lon);
            //items.add(new Pin(position, name, 1));
            items.add(new Pin(id, name, type, lat, lon));
        }
        return items;
    }
}
