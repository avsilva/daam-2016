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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

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
        emailLayout = (TextView) findViewById(R.id.tvUserEmail);
        nomeLayout = (TextView) findViewById(R.id.tvUserNome);
        etMudarNome = (EditText) findViewById(R.id.etMudarNome);
        etMudarPw = (EditText) findViewById(R.id.etMudarPw);


        SharedPreferences sharedPreferences = getSharedPreferences("user_data",MODE_PRIVATE);
        String email = sharedPreferences.getString("username","");
        String password = sharedPreferences.getString("password","");
        String nome = sharedPreferences.getString("nome","");

        emailLayout.setText(email);
        nomeLayout.setText(nome);

    }

}
