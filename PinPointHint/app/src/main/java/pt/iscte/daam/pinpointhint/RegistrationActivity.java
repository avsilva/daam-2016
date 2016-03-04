package pt.iscte.daam.pinpointhint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class represents the VIEW associated to the registration
 */
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
    }
}
