package pt.iscte.daam.pinpointhint;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cruz on 28/05/2016.
 */
public class IncrementPinRequest extends StringRequest {
    private static final String INCREMENT_PIN_REQUEST_URL = "http://daam.coolpage.biz/incrementpin.php";
    private Map<String,String> params;

    public IncrementPinRequest(String email, Response.Listener<String> listener){
        super(Request.Method.POST, INCREMENT_PIN_REQUEST_URL, listener, null);
        params = new HashMap<>();

        params.put("email", email);


    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}


