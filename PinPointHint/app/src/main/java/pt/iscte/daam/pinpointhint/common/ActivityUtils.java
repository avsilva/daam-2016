package pt.iscte.daam.pinpointhint.common;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pt.iscte.daam.pinpointhint.model.Pin;

/**
 * This is an utility class used within the activity classes.
 */
public final class ActivityUtils {
    public static final String EMPTY_STRING = "";
    //private ArrayList<LatLng> mDataset;

    public ActivityUtils() {
        //Utility class
    }

    public ArrayList<LatLng> readItems(List<Pin> pins)  {
        ArrayList<LatLng> list = new ArrayList<LatLng>();

            for (Pin item : pins) {
                list.add(item.getPosition());
            }

        return list;
    }


    /*public void DataSet(ArrayList<LatLng> dataSet) {
        this.mDataset = dataSet;
    }

    public ArrayList<LatLng> getData() {
        return mDataset;
    }*/

}
