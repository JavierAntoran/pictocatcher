package es.unizar.cps.cav.pictocatcher;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

public class SendCaptureActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.send_capture);
    }

    protected void onStart(){
        super.onStart();



        ImageView imageView = (ImageView) findViewById(R.id.sendCapView);


        String capturePath = getIntent().getExtras().getString("capturePath");

        String[] image_data = capturePath.split("_");
        final int pictoId = Integer.parseInt(image_data[1]);
        Bitmap bmp = BitmapFactory.decodeFile(capturePath);

        // Get the dimensions of the View
        int targetW = imageView.getLayoutParams().width;
        int targetH = imageView.getLayoutParams().height;

        Log.d("PICTOCATCHER", targetW + " : " + targetH);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(capturePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor << 1;
        Bitmap bitmap = BitmapFactory.decodeFile(capturePath, bmOptions);

        Matrix mtx = new Matrix();
        mtx.postRotate(90);
        // Rotating Bitmap
        final Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

        if (rotatedBMP != bitmap)
            bitmap.recycle();

        imageView.setImageBitmap(rotatedBMP);

        ImageView sendCapPicto = (ImageView) findViewById(R.id.sendCapPicto);
        AssetManager assetManager = this.getAssets();

        try {
            InputStream is = assetManager.open("pictograms/bike.png");
            Bitmap bitmapPicto = BitmapFactory.decodeStream(is);
            sendCapPicto.setImageBitmap(bitmapPicto);
        }catch (Exception e) {
            e.printStackTrace();
        }

        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://httpbin.org/post",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject r;
                        boolean result = false;
                        try {
                            r = new JSONObject(response);
                            //Disimissing the progress dialog
                            loading.dismiss();
                            //Showing toast message of the response
                            Log.d("PICTOCATCHER", response);
                            result = r.getBoolean("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if ( result ) {
                            Toast.makeText(SendCaptureActivity.this, "Cooool! ;-)", Toast.LENGTH_LONG).show();
                            captureOK();
                        } else {
                            Toast.makeText(SendCaptureActivity.this, "Ohhhhh! :_(", Toast.LENGTH_LONG).show();
                            tryAgain();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(SendCaptureActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(rotatedBMP);

                //Getting Image Name
                String picto_id = Integer.toString(pictoId);

                //Creating parameters
                Map<String,String> params = new Hashtable<String,String>();

                //Adding parameters
                params.put("captured_picto", image);
                params.put("picto_id", picto_id);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);


    }

    protected void tryAgain(){

    }

    protected void captureOK() {

    }

    protected String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
