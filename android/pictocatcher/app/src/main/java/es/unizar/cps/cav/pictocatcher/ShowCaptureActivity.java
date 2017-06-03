package es.unizar.cps.cav.pictocatcher;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
        ImageView catchImageView = (ImageView) findViewById(R.id.catch_image);
        TextView catchDateView = (TextView) findViewById(R.id.catch_date);
        TextView catchTitleView = (TextView) findViewById(R.id.catch_title);
        TextView descriptionView = (TextView) findViewById(R.id.catch_description);

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

        descriptionView.setText("La regla graduada es un instrumento de medición con forma de plancha delgada y rectangular que incluye una escala graduada dividida en unidades de longitud, por ejemplo, centímetros o pulgadas; es un instrumento útil para trazar segmentos rectilíneos con la ayuda de un bolígrafo o lápiz, y puede ser rígido, semirrígido o muy flexible, construido de madera, metal y material plástico, entre otros.");

        String catchPhotoPath = c.getString(c.getColumnIndex("catch_file"));
        Log.d("PICTOCATCHER", catchPhotoPath);

            Bitmap catchImage = BitmapFactory.decodeFile(catchPhotoPath);

            //Matrix mtx = new Matrix();
            //mtx.postRotate(90);
            // Rotating Bitmap
            //final Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

            //if (rotatedBMP != bitmap)
            //    bitmap.recycle();
            catchImageView.setImageBitmap(catchImage);

    }
}
