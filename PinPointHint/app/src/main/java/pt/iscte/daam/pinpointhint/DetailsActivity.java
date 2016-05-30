package pt.iscte.daam.pinpointhint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import pt.iscte.daam.pinpointhint.common.ActivityUtils;


public class DetailsActivity extends AppCompatActivity {

    public static String APIURL = "http://46.101.41.76/pinsgeojson/";
    private URL url;
    private final static String mLogTag = "pin point log";
    private ActivityUtils pinUtils;

    private TextView tvDescr1;
    private TextView tvType1;
    private TextView tvStatus1;
    private TextView tvData1;

    private ImageView ivDetails;
    private TextView tvDetails;
    private Double lat;
    String imgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //allow networking operation on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //setContentView(R.layout.activity_details_test_imagem);
        setContentView(R.layout.activity_details);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        tvDescr1 = (TextView) findViewById(R.id.tvDescr1);
        tvType1 = (TextView) findViewById(R.id.tvType1);
        tvStatus1 = (TextView) findViewById(R.id.tvStatus1);
        tvData1 = (TextView) findViewById(R.id.tvData1);
        ivDetails = (ImageView) findViewById(R.id.ivDetails);


        Intent i = getIntent();
        final String id = i.getStringExtra("ID");
        final String descr = i.getStringExtra("DESCR");
        lat = i.getDoubleExtra("LAT",0.0);
        imgName = String.valueOf(lat).substring(0, 10);
        final Double lon = i.getDoubleExtra("LONG",0.0);

        //satellite();

        try {
            url = new URL(APIURL + id.toString());
            Log.e(mLogTag, "URL = "+ APIURL + id.toString());

            getPinDetails details = new getPinDetails();
            details.execute();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action" + id.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
            }
        });*/
    }


    private class getPinDetails extends AsyncTask<String, Void, String> {

        public String json;
        Drawable drawable;

        @Override
        protected String doInBackground(String... params)  {

            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                pinUtils = new ActivityUtils();
                json = pinUtils.convertInputStreamToString(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {

                JSONObject jobj = new JSONObject(result);
                JSONObject t_poi_properties = jobj.getJSONObject("properties");
                String descr = t_poi_properties.getString("descr");
                String type_name = t_poi_properties.getString("type_name");
                String status_name = t_poi_properties.getString("status_name");
                String data = t_poi_properties.getString("created");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

                Date d = format.parse(data);


                String pic = t_poi_properties.getString("pic");

                if (pic != "null"){
                    Log.e(mLogTag, "pic = "+ pic);
                    drawable  =  LoadImageFromWeb(pic);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    Bitmap newbitMap = setImageViewContent(bitmap);
                    ivDetails.setImageBitmap(newbitMap);

                }

                tvDescr1.setText(descr);
                tvType1.setText(type_name);
                tvStatus1.setText(status_name);
                tvData1.setText(d.toString());

            } catch (Exception e) {

                Log.e(mLogTag, "An exception has occured in the connection - = " + e.toString());
            }
        }
    }

    private void satellite() {

        new yourTask().execute();
    }

    /*private Drawable LoadImageFromWeb(String url){



    }*/

    private Drawable LoadImageFromWeb(String url){
        try{
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }catch (Exception e) {
            Log.e(mLogTag, "LoadImageFromWeb - = " + e.toString());
            System.out.println("Exc="+e);
            return null;
        }
    }

    private class yourTask extends AsyncTask<Integer, Void, Integer> {
        Drawable drawable;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show a progress bar
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            Log.e(mLogTag, "imgName = "+ imgName.toString());
            drawable  =  LoadImageFromWeb("http://daam.coolpage.biz/images/" + imgName + ".jpg");
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            Log.e(mLogTag, "result = "+ result.toString());

            Bitmap bitmap = drawableToBitmap(drawable);
            int currentBitmapWidth = bitmap.getWidth();

            int currentBitmapHeight = bitmap.getHeight();

            int ivWidth = ivDetails.getWidth();
            int ivHeight = ivDetails.getHeight();
            int newWidth = ivWidth;

            int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));

            Bitmap newbitMap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            ivDetails.setImageBitmap(newbitMap);

            //ivDetails.setImageDrawable(drawable);

        }
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getCurrent()!=null) {
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Bitmap setImageViewContent(Bitmap bitmap){
        int currentBitmapWidth = bitmap.getWidth();
        int currentBitmapHeight = bitmap.getHeight();
        int ivWidth = ivDetails.getWidth();
        int newWidth = ivWidth;
        int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));

        Bitmap newbitMap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return newbitMap;

    }

}
