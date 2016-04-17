package pt.iscte.daam.pinpointhint.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by andre.silva on 16/04/2016.
 */
public class Pin implements ClusterItem{

    /*public final String name;
    public final int profilePhoto;*/
    private final LatLng mPosition;

    public Pin(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    /*public Pin(LatLng position, String name, int pictureResource) {
        this.name = name;
        profilePhoto = pictureResource;
        mPosition = position;
    }*/

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

}
