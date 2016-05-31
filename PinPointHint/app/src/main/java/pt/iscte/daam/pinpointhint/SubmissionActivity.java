package pt.iscte.daam.pinpointhint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pt.iscte.daam.pinpointhint.common.ActivityUtils;
import pt.iscte.daam.pinpointhint.model.PinType;

/**
 * This class represents the VIEW associated to the suggestion submission.
 */
public class SubmissionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etSubDescription;
    private ImageView ivSub;
    private Button btSubNext;
    private Spinner spinnerSubType;
    private String encoded_string, image_name;
    private Bitmap bitmap;
    private final static String mLogTag = "pin point log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

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
    }

    /**
     * This method is used to take and load a photo.
     *
     * @param v The view on which the event to take and load a photo is performed.
     */
    public void photo(View v) {

        //Aceder a função de foto do sistema operativo e guardar a imagem em memória
        ivSub.setVisibility(View.VISIBLE);
        btSubNext.setVisibility(View.VISIBLE);

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, 10);
        } else {
            Log.e(mLogTag, "NOT AVAILABLE");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10 && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivSub.setImageBitmap(imageBitmap);
            new Encode_image().execute(imageBitmap);
        }
    }
    private class Encode_image extends AsyncTask<Bitmap, Void, Void> {
        @Override
        protected Void doInBackground(Bitmap... bmap) {

            bitmap = bmap[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Log.e(mLogTag, "encoded_string2 = " + encoded_string);
            //makeRequest();
        }
    }

    /**
     * This method is used to handle the event to go to the next activity.
     *
     * @param v The view on which the event to go to the next activity
     *          is performed.
     */
    public void next(View v) {

        Intent result = new Intent();




        if (encoded_string == null) {
            Toast.makeText(getBaseContext(), "Por favor inclua uma foto da sugestão!", Toast.LENGTH_LONG).show();
        } else{
            PinType type = (PinType)spinnerSubType.getSelectedItem();
            result.putExtra("TYPE", type.id);
            result.putExtra("DESCR", etSubDescription.getText().toString());
            result.putExtra("FILE", encoded_string);
            setResult(Activity.RESULT_OK, result);
            finish();
        }



    }

    /**
     * This method is used to setup listeners on the components of this activity.
     */
    private void addListeners() {
        etSubDescription.setOnClickListener(this);
    }

    /**
     * Thist method is used to retrieved the data associated to each edit text defined
     * in this activity, in order to setup the instance attributes of this activity.
     */
    private void retrieveFieldInformation() {
        etSubDescription = (EditText) findViewById(R.id.etSubDescription);
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
