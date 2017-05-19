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
        ImageView iv = (ImageView) findViewById(R.id.showCapture);
        TextView tv = (TextView) findViewById(R.id.caughtDate);

        MySQLiteHelper dbHelper = new MySQLiteHelper(this);

        Cursor c = dbHelper.getPictoById(pictoId);
        c.moveToFirst();

        SimpleDateFormat idf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = idf.parse(c.getString(c.getColumnIndex("catch_date")));
            SimpleDateFormat odf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            tv.setText(odf.format(d));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream is = assetManager.open("pictograms/" + c.getString(c.getColumnIndexOrThrow("imagename")));
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            iv.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
