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

    public List<Pin> read(String json) throws JSONException {
        List<Pin> items = new ArrayList<Pin>();
        //String json = new Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();

        JSONObject jobj = new JSONObject(json);
        JSONArray poi = jobj.getJSONArray("features");
        for(int i=0; i<poi.length(); i++) {

            JSONObject t_poi = poi.getJSONObject(i);
            int id = t_poi.getInt("id");
            JSONObject t_poi_geometry = t_poi.getJSONObject("geometry");
            JSONObject t_poi_properties = t_poi.getJSONObject("properties");
            String descr = t_poi_properties.getString("descr");
            String type_name = t_poi_properties.getString("type_name");
            int type = t_poi_properties.getInt("type");
            JSONArray coords =  t_poi_geometry.getJSONArray("coordinates");
            Double lat = (Double) coords.get(1);
            Double lon = (Double) coords.get(0);
            items.add(new Pin(id, descr, type, type_name, lat, lon));
        }
        return items;
    }
}
