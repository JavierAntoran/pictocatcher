package es.unizar.cps.cav.pictocatcher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public class SendCaptureActivity extends AppCompatActivity {

    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private int pictoId;
    private MySQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pictoId = getIntent().getExtras().getInt("pictoId");

        dbHelper = new MySQLiteHelper(this);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(pictoId);
            } catch (IOException ex) {}
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * http://developer.android.com/training/camera/photobasics.html
     */
    private File createImageFile(int pictoId) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + pictoId + "_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        image.mkdirs();

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE ) {
            if (resultCode == RESULT_OK) {

                setContentView(R.layout.send_capture);


                ImageView imageView = (ImageView) findViewById(R.id.sendCapView);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1/4;
                final Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,bmOptions);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(mCurrentPhotoPath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Picasso.with(this)
                        .load("file://"+mCurrentPhotoPath)
                        .placeholder(R.drawable.placeholder_photo)
                        .into(imageView);
                //imageView.setImageBitmap(bitmap);

                ImageView sendCapPicto = (ImageView) findViewById(R.id.sendCapPicto);
                AssetManager assetManager = this.getAssets();

                final Cursor pictoCursor  = dbHelper.getPictoById(pictoId);
                pictoCursor.moveToFirst();
                //try {
                    //InputStream is = assetManager.open("pictograms/" + pictoCursor.getString(pictoCursor.getColumnIndex("imagename")));
                    //Bitmap bitmapPicto = BitmapFactory.decodeStream(is);
                    //sendCapPicto.setImageBitmap(bitmapPicto);
                    Picasso.with(this)
                            .load("file:///android_asset/pictograms/"+pictoCursor.getString(pictoCursor.getColumnIndex("imagename")))
                            .placeholder(R.drawable.placeholder)
                            .into(sendCapPicto);
                //}catch (Exception e) {
                //    e.printStackTrace();
                //}

                final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
                final StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://voz07.cps.unizar.es:8080/post_capture",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d("PICTOCATCHER", response);
                                JSONObject r;
                                boolean result = false;
                                try {
                                    r = new JSONObject(response);
                                    //Disimissing the progress dialog
                                    loading.dismiss();
                                    //Showing toast message of the response
                                    Log.d("PICTOCATCHER", r.toString());
                                    result = r.getBoolean("result");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if ( result ) {
                                    Toast.makeText(SendCaptureActivity.this, "Cooool! ;-)", Toast.LENGTH_LONG).show();
                                    saveResult();
                                } else {
                                    Toast.makeText(SendCaptureActivity.this, "Ohhhhh! :_(", Toast.LENGTH_LONG).show();
                                    tryAgain();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.d("PICTOCATCHER", volleyError.toString());
                                //Dismissing the progress dialog
                                loading.dismiss();
                                tryAgain();
                                //Showing toast
                                Log.d("PICTOCATCHER", volleyError.toString());
                                Toast.makeText(SendCaptureActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        //Converting Bitmap to String
                        String image = getStringImage(bitmap);

                        //Getting Image Name
                        String wnids = pictoCursor.getString(pictoCursor.getColumnIndex("wnids"));

                        //Creating parameters
                        Map<String,String> params = new Hashtable<String,String>();

                        //Adding parameters
                        params.put("captured_picto", image);
                        params.put("wnid", wnids);

                        //returning parameters
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        30000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                //Creating a Request Queue
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                //Adding request to the queue
                requestQueue.add(stringRequest);
            } else {
                File f = new File(mCurrentPhotoPath);
                if (f.exists()) {
                    f.delete();
                }
                finish();
            }
        }

    }

    protected void tryAgain(){
        Button b = (Button) findViewById(R.id.sendCapRetryButton);
        b.setVisibility(View.VISIBLE);

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    protected void saveResult(){
        if (dbHelper.setCatchInfo(pictoId, new Date(), mCurrentPhotoPath) != 1) {
            Toast.makeText(SendCaptureActivity.this, "An error ocurred", Toast.LENGTH_LONG).show();
            tryAgain();
        }
        Intent intent = new Intent(this, ShowCaptureActivity.class);
        intent.putExtra("pictoId",pictoId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    protected String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
