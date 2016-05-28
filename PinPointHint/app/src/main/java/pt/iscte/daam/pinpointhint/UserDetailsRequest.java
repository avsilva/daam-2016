package pt.iscte.daam.pinpointhint;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cruz on 14/05/2016.
 */
public class UserDetailsRequest extends StringRequest{
    private static final String USER_REQUEST_URL = "http://daam.coolpage.biz/userUpdate.php";
    private Map<String,String> params;

    public UserDetailsRequest(String email, String nome, String password, Response.Listener<String> listener){
        super(Request.Method.POST,USER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("nome", nome);
        params.put("password", password);


    }
    @Override
    public Map<String,String> getParams(){
        return params;
    }
}


