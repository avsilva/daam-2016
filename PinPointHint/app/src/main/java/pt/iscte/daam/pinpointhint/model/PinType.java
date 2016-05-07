package pt.iscte.daam.pinpointhint.model;

/**
 * Created by andre.silva on 01/05/2016.
 */
public class PinType {

    public int id = 0;
    public String name = "";

    // A simple constructor for populating our member variables for this tutorial.
    public PinType( int _id, String _name )
    {
        id = _id;
        name = _name;
    }

    public String toString()
    {
        return( name );
    }

}
