package pt.iscte.daam.pinpointhint;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import pt.iscte.daam.pinpointhint.common.ActivityUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etLoginEmail;
    private EditText etLoginPassword;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //final EditText etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
        //final EditText etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        final Button bLoginSubmit = (Button) findViewById(R.id.bLoginSubmit);

        //Retrieving information from activity fields
        retrieveFieldInformation();

        //Adding onClick listeners
        addListeners();


        assert bLoginSubmit != null;
        bLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etLoginEmail.getText().toString();
                final String password = etLoginPassword.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            String nome = jsonResponse.getString("name");
                            int n_pins = jsonResponse.getInt("n_pins");
                            int raio=jsonResponse.getInt("raio");
                            if (success) {
                                SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", email);
                                editor.putString("password", password);
                                editor.putString("nome", nome);
                                editor.putInt("n_pins", n_pins);
                                editor.putInt("raio", raio);
                                editor.commit();
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                LoginActivity.this.startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login failed").setNegativeButton("Retry", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }


        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * This method is used to setup listeners on the components of this activity.
     */
    private void addListeners() {
        etLoginEmail.setOnClickListener(this);
        etLoginPassword.setOnClickListener(this);
    }

    /**
     * Thist method is used to retrieved the data associated to each edit text defined
     * in this activity, in order to setup the instance attributes of this activity.
     */
    private void retrieveFieldInformation() {
        etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.etLoginEmail) {
            etLoginEmail.setText(ActivityUtils.EMPTY_STRING);
        }

        if (v.getId() == R.id.etLoginPassword) {
            etLoginPassword.setText(ActivityUtils.EMPTY_STRING);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://pt.iscte.daam.pinpointhint/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://pt.iscte.daam.pinpointhint/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}



