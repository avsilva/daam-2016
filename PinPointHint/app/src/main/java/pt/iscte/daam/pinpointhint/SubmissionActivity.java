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
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import java.util.HashMap;
import java.util.Map;

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
    //newcode
    private String encoded_string, image_name;
    private Bitmap bitmap;
    private File file;
    private Uri file_uri;



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

        //Toast toast = Toast.makeText(getApplicationContext(),
        //        R.string.photoLoaded, Toast.LENGTH_SHORT);
        //toast.show();

        //newcode
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getFileUri();
        i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
        startActivityForResult(i, 10);


    }

    //newcode
    private void getFileUri() {
        String lat1 = String.valueOf(lat).substring(0,10);
        image_name="" + lat1 + ".jpg";
        // image_name.concat(String.valueOf(lat));
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + image_name
        );

        file_uri = Uri.fromFile(file);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10 && resultCode == RESULT_OK) {
            new Encode_image().execute();
        }
    }
    private class Encode_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeRequest();
        }
    }

    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://daam.coolpage.biz/connection.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("encoded_string",encoded_string);
                map.put("image_name",image_name);

                return map;
            }
        };
        requestQueue.add(request);
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

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("username", "");
        //final int n_pins = sharedPreferences.getInt("n_pins", 0);
        //final int nPinsIncrement = n_pins+1;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        SharedPreferences sharedPreferences = getSharedPreferences("user_data",MODE_PRIVATE);
                        int n_pins = sharedPreferences.getInt("n_pins",0);
                        sharedPreferences.edit().putInt("n_pins", (n_pins + 1)).commit();
                    }
                    if(!success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SubmissionActivity.this);
                        builder.setMessage("Falhou a inserção do pin").setNegativeButton("Retry",null).create().show();
                    }
                        //String password = sharedPreferences.getString("password","");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        IncrementPinRequest incrementRequest = new IncrementPinRequest(email, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SubmissionActivity.this);
        queue.add(incrementRequest);

        //newcode



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
