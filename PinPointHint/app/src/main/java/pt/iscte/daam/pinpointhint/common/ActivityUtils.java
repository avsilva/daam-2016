package pt.iscte.daam.pinpointhint.common;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    /*public void DataSet(ArrayList<LatLng> dataSet) {
        this.mDataset = dataSet;
    }

    public ArrayList<LatLng> getData() {
        return mDataset;
    }*/

}
