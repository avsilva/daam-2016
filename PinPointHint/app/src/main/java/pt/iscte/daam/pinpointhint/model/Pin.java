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
    private final String mDescr;
    private final String mTypeName;
    private final int mType;
    private final int mIdent;

    public Pin(int id, String descr, int type, String type_name,  double lat, double lng) {

        mPosition = new LatLng(lat, lng);
        mDescr = descr;
        mTypeName = type_name;
        mType = type;
        mIdent = id;
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

    public String getDescr() {
        return mDescr;
    }

    public String getTypeName() {
        return mTypeName;
    }

    public int getType() {
        return mType;
    }

    public int getIdent() { return mIdent; }

    public Pin getPinByID(int id) {

        return this;
    }

}
