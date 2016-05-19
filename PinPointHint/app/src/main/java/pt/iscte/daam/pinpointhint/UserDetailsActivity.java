package pt.iscte.daam.pinpointhint;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.iscte.daam.pinpointhint.LoginActivity;
public class UserDetailsActivity extends AppCompatActivity {
    public TextView emailLayout;
    public TextView nomeLayout;
    public EditText etMudarNome;
    public EditText etMudarPw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Button btActualizarPerfil = (Button) findViewById(R.id.btActualizarPerfil);
        emailLayout = (TextView) findViewById(R.id.tvUserEmail);
        nomeLayout = (TextView) findViewById(R.id.tvUserNome);
        etMudarNome = (EditText) findViewById(R.id.etMudarNome);
        etMudarPw = (EditText) findViewById(R.id.etMudarPw);


        SharedPreferences sharedPreferences = getSharedPreferences("user_data",MODE_PRIVATE);
        final String email = sharedPreferences.getString("username","");
        //String password = sharedPreferences.getString("password","");
        String nome = sharedPreferences.getString("nome","");

        emailLayout.setText(email);
        nomeLayout.setText(nome);

        String s = "Perfil de " + nome;

        getSupportActionBar().setTitle(s);

        btActualizarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nome = etMudarNome.getText().toString();
                final String password = etMudarPw.getText().toString();

                if(!isValidNome(nome)&&!isValidPassword(password)) {
                    etMudarNome.setError("Insira um nome com 3 letras pelo menos");
                    etMudarPw.setError("Insira uma pw com 8 caracteres pelo menos");
                    etMudarNome.requestFocus();
                    etMudarPw.requestFocus();
                }
                else if(isValidNome(nome) && !isValidPassword(password)){
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                String nome = jsonResponse.getString("nome");
                                if (success) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                                    sharedPreferences.edit().putString("nome", nome).apply();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    UserDetailsRequest userDetailsRequest = new UserDetailsRequest(email, nome, "", responseListener);
                    RequestQueue queue = Volley.newRequestQueue(UserDetailsActivity.this);
                    queue.add(userDetailsRequest);
                    }
                 else if(isValidPassword(password) && !isValidNome(nome)){
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {

                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    UserDetailsRequest userDetailsRequest = new UserDetailsRequest(email, "", password, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(UserDetailsActivity.this);
                    queue.add(userDetailsRequest);
                }
            }


        });


    }
    private boolean isValidNome(String nome){
        if(nome.length()>4 && !nome.matches(".*\\d.*")){
            return true;
        }
        return false;


    }
    private boolean isValidPassword(String password){
        if (password != null && password.length() > 6) {
            return true;
        }
        return false;
    }

}
