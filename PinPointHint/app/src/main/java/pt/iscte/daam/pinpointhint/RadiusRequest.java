package pt.iscte.daam.pinpointhint;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cruz on 28/05/2016.
 */
public class RadiusRequest extends StringRequest {
    private static final String RADIUS_REQUEST_URL = "http://daam.coolpage.biz/radiusUpdate.php";
    private Map<String, String> params;

    public RadiusRequest(String email, String radius, Response.Listener<String> listener){
        super(Request.Method.POST,RADIUS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("raio", radius);
    }
    @Override
    public Map<String, String> getParams(){
        return params;
    }
}

