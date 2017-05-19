package es.unizar.cps.cav.pictocatcher;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by almul0 on 5/12/17.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pictocatcher.db";
    private static final int DATABASE_VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        // Database creation sql statement
        String PICTOCATCHER_TABLE = "CREATE TABLE pictocatcher (" +
                "_id INTEGER PRIMARY KEY, pictoid INTEGER, word TEXT, imagename TEXT, caught INTEGER DEFAULT 0," +
                "catch_date TEXT, catch_file TEXT)";

        // create books table
        db.execSQL(PICTOCATCHER_TABLE);

        Log.d("DATABASE", "Database created");

        String POPULATE_DATA =
                "INSERT INTO pictocatcher (pictoid, word, imagename) VALUES " +
                        "(1, 'bicicleta', 'bike.png'), " +
                        "(2, 'zanahoria', 'carrot.png'), " +

                        "(4, 'vaso', 'glass.png'), " +
                        "(5, 'casco', 'helmets.png'), " +
                        "(6, 'oveja', 'lamb.png'), " +
                        "(7, 'farola', 'light_post.png'), " +
                        "(8, 'pincel', 'paintbrush.png'), " +
                        "(9, 'piano', 'piano.png'), " +
                        "(10, 'portatil', 'portable.png') ";

        db.execSQL(POPULATE_DATA);
        POPULATE_DATA =
                "INSERT INTO pictocatcher (pictoid, word, imagename, caught,catch_date) VALUES " +
                        "(3, 'gato', 'cat.png',1, '2017-05-10 12:31:55'), " +
                        "(11, 'boligrafo', 'writing pen.png',1,'2017-04-20 17:41:30')";
        db.execSQL(POPULATE_DATA);


        Log.d("DATABASE", "Database populated");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS pictocatcher");

        // create fresh books table
        this.onCreate(db);
    }

    public Cursor getAllPictos(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM pictocatcher", null);

        return res;
    }

    public Cursor getPictoById(int pictoId){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[] { Integer.toString(pictoId) };
        Cursor res = db.rawQuery("SELECT * FROM pictocatcher WHERE _id = ?", args);

        return res;
    }

}
