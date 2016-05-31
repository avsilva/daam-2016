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
    private EditText etSubEmail;
    private EditText etSubGPS;
    private ImageView ivSub;
    private Button btSubNext;
    private Spinner spinnerSubType;
    //newcode
    private String encoded_string, image_name;
    private Bitmap bitmap;
    private File file;
    protected Uri file_uri;



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

        //newcode
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        /*file_uri = getFileUri();

        i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
        i.putExtra("FILE", file_uri.getPath());

        startActivityForResult(i, 10);*/
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, 10);
        } else {
            Log.e(mLogTag, "NOT AVAILABLE");
        }


    }

    //newcode
    private Uri getFileUri() {
        String lat1 = String.valueOf(lat).substring(0, 10);
        image_name="" + lat1 + ".jpg";
        // image_name.concat(String.valueOf(lat));
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + image_name
        );

        return Uri.fromFile(file);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10 && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivSub.setImageBitmap(imageBitmap);

            //Log.e(mLogTag, "file_uri2 = " + file_uri.getPath());
            //Log.e(mLogTag, "FILE = " + data.getStringExtra("FILE"));

            new Encode_image().execute(imageBitmap);
            /*Bitmap imageRetrieved = (Bitmap) data.getExtras().get("data");
            Log.e(mLogTag, "FILE = " + imageRetrieved.toString());*/
        }
    }
    private class Encode_image extends AsyncTask<Bitmap, Void, Void> {
        @Override
        protected Void doInBackground(Bitmap... bmap) {

            //Log.e(mLogTag, "file_uri3 = " + file_uri.getPath());
            //bitmap = BitmapFactory.decodeFile(file_uri.getPath());
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

        result.putExtra("FILE", encoded_string);
        setResult(Activity.RESULT_OK, result);



        /*SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
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
        queue.add(incrementRequest);*/

        //newcode
        finish();

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
