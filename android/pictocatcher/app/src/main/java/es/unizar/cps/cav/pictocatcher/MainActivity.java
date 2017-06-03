package es.unizar.cps.cav.pictocatcher;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {


    private MySQLiteHelper dbHelper;


    private GridView gridview;

    Cursor pictoCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        dbHelper = new MySQLiteHelper(this);

    }

    protected void onResume() {
        super.onResume();

        pictoCursor  = dbHelper.getAllPictos();
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setVisibility(View.VISIBLE);
        gridview.setAdapter(new PictogramCursorAdapter(this, pictoCursor));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Cursor c = dbHelper.getPictoById((int) v.getTag());
                c.moveToFirst();

                if ( c.getInt(c.getColumnIndex("caught")) == 1) {
                    showCapture((int) v.getTag());
                } else {
                    sendCapture((int) v.getTag());
                }
            }
        });
    }

    private void sendCapture(int pictoId) {
        Intent intent = new Intent(this, SendCaptureActivity.class);
        intent.putExtra("pictoId",pictoId);
        finish();
        startActivity(intent);
    }

    private void showCapture(int pictoId) {
        Intent intent = new Intent(this, ShowCaptureActivity.class);
        intent.putExtra("pictoId",pictoId);
        finish();
        startActivity(intent);
    }
}
