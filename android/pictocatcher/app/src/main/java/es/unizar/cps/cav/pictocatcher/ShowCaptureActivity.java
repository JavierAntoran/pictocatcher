package es.unizar.cps.cav.pictocatcher;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowCaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_capture);
        AssetManager assetManager = this.getAssets();

        int pictoId = getIntent().getExtras().getInt("pictoId");
        MarkableImageView catchImageView = (MarkableImageView) findViewById(R.id.catch_image);
        TextView catchDateView = (TextView) findViewById(R.id.catch_date);
        TextView catchTitleView = (TextView) findViewById(R.id.catch_title);
        TextView descriptionView = (TextView) findViewById(R.id.catch_description);
        TextView descriptionTitleView = (TextView) findViewById(R.id.catch_description_title);

        Typeface tfb = Typeface.createFromAsset(assetManager,
                String.format("fonts/%s", "babelsans_bold.ttf"));
        Typeface tf = Typeface.createFromAsset(assetManager,
                String.format("fonts/%s", "babelsans_bold.ttf"));

        catchTitleView.setTypeface(tfb);
        catchDateView.setTypeface(tf);
        descriptionView.setTypeface(tfb);
        descriptionTitleView.setTypeface(tfb);



        MySQLiteHelper dbHelper = new MySQLiteHelper(this);

        Cursor c = dbHelper.getPictoById(pictoId);
        c.moveToFirst();

        SimpleDateFormat idf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = idf.parse(c.getString(c.getColumnIndex("catch_date")));
            SimpleDateFormat odf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            catchDateView.setText(odf.format(d));
        } catch (Exception e) {
            e.printStackTrace();
        }

        catchTitleView.setText(c.getString(c.getColumnIndex("word")));

        descriptionView.setText("Proin vulputate vestibulum faucibus. Donec a ultrices dolor. Maecenas quis nibh non mi sodales vehicula id at urna. Vestibulum ac tristique ante. Morbi eros felis, pretium sit amet dictum a, tincidunt eu tortor. In mattis dolor eu diam euismod, at suscipit velit accumsan. Mauris maximus gravida nunc, quis volutpat ex. Phasellus in augue eu felis ullamcorper vehicula. Suspendisse potenti. Nulla cursus lorem sed neque maximus, egestas aliquet urna suscipit. Cras ornare urna a justo laoreet semper.");

        String catchPhotoPath = c.getString(c.getColumnIndex("catch_file"));
        Log.d("PICTOCATCHER", catchPhotoPath);

            //Bitmap catchImage = BitmapFactory.decodeFile(catchPhotoPath);

            //Matrix mtx = new Matrix();
            //mtx.postRotate(90);
            // Rotating Bitmap
            //final Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

            //if (rotatedBMP != bitmap)
            //    bitmap.recycle();
            Picasso.with(this)
                .load("file://"+catchPhotoPath)
                .placeholder(R.drawable.placeholder_photo)
                .into(catchImageView);
            //catchImageView.setImageBitmap(catchImage);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }



}
