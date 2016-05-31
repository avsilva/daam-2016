package pt.iscte.daam.pinpointhint;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

    }

    public void showRegistar(View v){
        Intent regIntent = new Intent(this, RegistrationActivity.class);
        startActivity(regIntent);
    }
    public void showLogin(View v){
        Intent loginIntent = new Intent (this, LoginActivity.class);
        startActivity(loginIntent);
    }
}
