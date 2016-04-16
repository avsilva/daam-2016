package pt.iscte.daam.pinpointhint;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import pt.iscte.daam.pinpointhint.common.ActivityUtils;

/**
 * This class represents the VIEW associated to the registration
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etRegName;
    private EditText etRegEmail;
    private EditText etRegPhone;
    private EditText etRegPassword;
    private EditText etRegPasswordConfirm;
    private Button bRegSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Retrieving information from activity fields
        retrieveFieldInformation();

        //Adding onClick listeners
        addListeners();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.etRegName) {
            etRegName.setText(ActivityUtils.EMPTY_STRING);
        }

        if (v.getId() == R.id.etRegEmail) {
            etRegEmail.setText(ActivityUtils.EMPTY_STRING);
        }

        if (v.getId() == R.id.etRegPhone) {
            etRegPhone.setText(ActivityUtils.EMPTY_STRING);
        }

        if (v.getId() == R.id.etRegPassword) {
            etRegPassword.setText(ActivityUtils.EMPTY_STRING);
        }

        if (v.getId() == R.id.etRegPasswordConfirm) {
            etRegPasswordConfirm.setText(ActivityUtils.EMPTY_STRING);
        }
    }

    /**
     * This method is used to handle the on click event performed in the submission button.
     *
     * @param v The view on which the event is performed.
     */
    public void submit(View v) {
        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.regSubmitMessageOk, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This method is used to setup listeners on the components of this activity.
     */
    private void addListeners() {
        etRegName.setOnClickListener(this);
        etRegEmail.setOnClickListener(this);
        etRegPhone.setOnClickListener(this);
        etRegPassword.setOnClickListener(this);
        etRegPasswordConfirm.setOnClickListener(this);
        bRegSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nome = etRegName.getText().toString();
                final String email = etRegEmail.getText().toString();
                final String password = etRegPassword.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success){
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                RegistrationActivity.this.startActivity(intent);
                            } else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                                builder.setMessage("O registo falhou").setNegativeButton("Retry",null).create().show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegistrationRequest registrationRequest = new RegistrationRequest(nome,email,password,responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegistrationActivity.this);
                queue.add(registrationRequest);
            }
        });
    }

    /**
     * Thist method is used to retrieved the data associated to each edit text defined
     * in this activity, in order to setup the instance attributes of this activity.
     */
    private void retrieveFieldInformation() {
        etRegName = (EditText) findViewById(R.id.etRegName);
        etRegEmail = (EditText) findViewById(R.id.etRegEmail);
        etRegPhone = (EditText) findViewById(R.id.etRegPhone);
        etRegPassword = (EditText) findViewById(R.id.etRegPassword);
        etRegPasswordConfirm = (EditText) findViewById(R.id.etRegPasswordConfirm);
        bRegSubmit = (Button) findViewById(R.id.bRegSubmit);

    }
}
