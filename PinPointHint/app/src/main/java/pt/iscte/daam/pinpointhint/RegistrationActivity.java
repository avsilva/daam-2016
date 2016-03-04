package pt.iscte.daam.pinpointhint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String EMPTY_STRING = "";

    private EditText etRegName;
    private EditText etRegEmail;
    private EditText etRegPhone;
    private EditText etRegPassword;
    private EditText etRegPasswordConfirm;


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
            etRegName.setText(EMPTY_STRING);
        }

        if (v.getId() == R.id.etRegEmail) {
            etRegEmail.setText(EMPTY_STRING);
        }

        if (v.getId() == R.id.etRegPhone) {
            etRegPhone.setText(EMPTY_STRING);
        }

        if (v.getId() == R.id.etRegPassword) {
            etRegPassword.setText(EMPTY_STRING);
        }

        if (v.getId() == R.id.etRegPasswordConfirm) {
            etRegPasswordConfirm.setText(EMPTY_STRING);
        }

    }

    /**
     *
     * @param v
     */
    public void submit(View v) {
        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.regSubmitMessageOk, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * TODO
     */
    private void addListeners() {
        etRegName.setOnClickListener(this);
        etRegEmail.setOnClickListener(this);
        etRegPhone.setOnClickListener(this);
        etRegPassword.setOnClickListener(this);
        etRegPasswordConfirm.setOnClickListener(this);
    }

    /**
     * TODO
     */
    private void retrieveFieldInformation() {
        etRegName = (EditText) findViewById(R.id.etRegName);
        etRegEmail = (EditText) findViewById(R.id.etRegEmail);
        etRegPhone = (EditText) findViewById(R.id.etRegPhone);
        etRegPassword = (EditText) findViewById(R.id.etRegPassword);
        etRegPasswordConfirm = (EditText) findViewById(R.id.etRegPasswordConfirm);
    }
}
