package pt.iscte.daam.pinpointhint;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cruz on 16/04/2016.
 */
public class RegistrationRequest extends StringRequest {
    private static final String REGISTRATION_REQUEST_URL = "http://daam.coolpage.biz/registration.php";
    private Map<String,String> params;

    public RegistrationRequest(String nome, String email, String password, Response.Listener<String> listener){
        super(Method.POST, REGISTRATION_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("nome", nome);
        params.put("email", email);
        params.put("password", password);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
