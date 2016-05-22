package pt.iscte.daam.pinpointhint;

import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.daam.pinpointhint.model.Pin;

/**
 * Created by andre.silva on 16/04/2016.
 */
public class PinGeoJonReader {

    private static final String REGEX_INPUT_BOUNDARY_BEGINNING = "\\A";
    private final static String mLogTag = "pin point log";

    List<Pin> items = new ArrayList<Pin>();

    private static List<Pin> instances = new ArrayList<Pin>();

    public static List getPinInstances()  {
        return instances;
    }


    private static void addPinToList(Pin pin) {
        instances.add(pin);
    }

    public List<Pin> read(String json) throws JSONException {


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
            Pin pin = new Pin(id, descr, type, type_name, lat, lon);
            addPinToList(pin);
            items.add(pin);
        }
        return items;
    }
}
