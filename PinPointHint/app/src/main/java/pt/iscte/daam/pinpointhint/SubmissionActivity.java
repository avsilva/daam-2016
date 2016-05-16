package pt.iscte.daam.pinpointhint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import pt.iscte.daam.pinpointhint.common.ActivityUtils;
import pt.iscte.daam.pinpointhint.model.PinType;

/**
 * This class represents the VIEW associated to the suggestion submission.
 */
public class SubmissionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etSubDescription;
    private EditText etSubEmail;
    private EditText etSubGPS;
    private ImageView ivSub;
    private Button btSubNext;
    private Spinner spinnerSubType;

    private Double lat;
    private final static String mLogTag = "pin point log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        // get the Intent from calling activity
        Intent i = getIntent();

        lat = i.getDoubleExtra("LAT", 0.0);
        Double lon = i.getDoubleExtra("LONG", 0.0);

        //Retrieving information from activity fields
        retrieveFieldInformation();

        //Adding onClick listeners
        addListeners();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.etSubDescription) {
            etSubDescription.setText(ActivityUtils.EMPTY_STRING);
        }

        /*if (v.getId() == R.id.etSubEmail) {
            etSubEmail.setText(ActivityUtils.EMPTY_STRING);
        }

        if (v.getId() == R.id.etSubGPS) {
            etSubGPS.setText(ActivityUtils.EMPTY_STRING);
        }*/
    }

    /**
     * This method is used to take and load a photo.
     *
     * @param v The view on which the event to take and load a photo is performed.
     */
    public void photo(View v) {
        //TODO
        //Aceder a função de foto do sistema operativo e guardar a imagem em memória
        ivSub.setVisibility(View.VISIBLE);
        btSubNext.setVisibility(View.VISIBLE);

        Toast toast = Toast.makeText(getApplicationContext(),
                R.string.photoLoaded, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This method is used to handle the event to go to the next activity.
     *
     * @param v The view on which the event to go to the next activity
     *          is performed.
     */
    public void next(View v) {
        //startActivity(new Intent(v.getContext(), SubmissionClassificationActivity.class));

        Intent result = new Intent();

        result.putExtra("DESCR", etSubDescription.getText().toString());

        PinType type = (PinType)spinnerSubType.getSelectedItem();
        result.putExtra("TYPE", type.id);

        result.putExtra("LAT", lat);
        setResult(Activity.RESULT_OK, result);
        finish();

    }

    /**
     * This method is used to setup listeners on the components of this activity.
     */
    private void addListeners() {
        etSubDescription.setOnClickListener(this);
        //etSubEmail.setOnClickListener(this);
        //etSubGPS.setOnClickListener(this);
    }

    /**
     * Thist method is used to retrieved the data associated to each edit text defined
     * in this activity, in order to setup the instance attributes of this activity.
     */
    private void retrieveFieldInformation() {
        etSubDescription = (EditText) findViewById(R.id.etSubDescription);
        //etSubEmail = (EditText) findViewById(R.id.etSubEmail);
        //etSubGPS = (EditText) findViewById(R.id.etSubGPS);
        spinnerSubType = (Spinner)this.findViewById(R.id.spinnerSubType);
        setSpinnerAdapter();

        ivSub = (ImageView) findViewById(R.id.ivSub);
        ivSub.setVisibility(View.INVISIBLE);

        btSubNext = (Button) findViewById(R.id.btSubNext);
        btSubNext.setVisibility(View.INVISIBLE);
    }

    private void setSpinnerAdapter(){

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, new PinType[] {
                new PinType( 1, "Contentores e ecopontos" ),
                new PinType( 2, "Espaços verdes" ),
                new PinType( 3, "Iluminação" ),
                new PinType( 4, "Limpeza urbana" ),
                new PinType( 5, "Mobiliário Urbano" ),
                new PinType( 6, "Situações de risco" ),
                new PinType( 7, "Passeios" ),
                new PinType( 8, "Pavimentos" ),
                new PinType( 9, "Sinalização" ),
                new PinType( 10, "Outros" )
        });

        spinnerSubType.setAdapter(spinnerArrayAdapter);

    }
}
