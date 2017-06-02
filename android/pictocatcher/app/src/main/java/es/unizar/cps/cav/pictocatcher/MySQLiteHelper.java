package es.unizar.cps.cav.pictocatcher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
                "_id INTEGER PRIMARY KEY, wnids text, word TEXT, imagename TEXT, caught INTEGER DEFAULT 0," +
                "catch_date TEXT, catch_file TEXT)";

        // create books table
        db.execSQL(PICTOCATCHER_TABLE);

        Log.d("DATABASE", "Database created");

        String POPULATE_DATA =
                "INSERT INTO pictocatcher (wnids, word, imagename) VALUES " +
                        "('n02834778', 'Bicicleta', 'bicycle.png'), " +
                        "('n00007846', 'Persona', 'person_3.png'), " +
                        "('n00464894', 'Golfista', 'golfer.png'), " +
                        "('n00468480', 'Futbolista', 'football.png'), " +
                        "('n01473806', 'Pez', 'fish.png'), " +
                        "('n01503061', 'Pájaro', 'bird.png'), " +
                        "('n02084071', 'Perro', 'dog.png'), " +
                        "('n02121808', 'Gato', 'cat.png'), " +
                        "('n02510455', 'Oso panda', 'panda.png'), " +
                        "('n02329401', 'Ratón', 'mouse.png'), " +
                        "('n02778669', 'Pelota baloncesto', 'ball.png'), " +
                        "('n02819697', 'Cama', 'bed.png'), " +
                        "('n02858304', 'Barco', 'boat.png'), " +
                        "('n02870526', 'Libro', 'book.png'), " +
                        "('n02958343', 'Coche', 'car.png'), " +
                        "('n03057021', 'Anorak', 'anorak.png'), " +
                        "('n03082979', 'Computadora', 'computer.png'), " +
                        "('n03345837', 'Extintor', 'extinguisher.png'), " +
                        "('n03510583', 'Avión', 'airliner.png'), " +
                        "('n03636248', 'Vela', 'candle.png'), " +
                        "('n03666917', 'Globo aerostático', 'balloon.png'), " +
                        "('n03733925', 'Regla', 'ruler.png'), " +
                        "('n03819448', 'Nido', 'nest.png'), " +
                        "('n03880323', 'Sartén', 'frying pan.png'), " +
                        "('n03908456,n13863020', 'Lapicero', 'pencil.png'), " +
                        "('n03985232', 'Portátil', 'laptop.png'), " +
                        "('n04228054', 'Ski', 'ski.png'), " +
                        "('n04244997', 'Canoa', 'canoe.png'), " +
                        "('n04490091', 'Camión', 'truck.png'), " +
                        "('n04972451', 'Tableta de chocolate', 'chocolate_bar.png'), " +
                        "('n06874185', 'Semáforo', 'traffic_lights.png'), " +
                        "('n07611358', 'Tarrina de helado', 'ice_cream_tub.png'), " +
                        "('n07739125', 'Manzana', 'apple.png'), " +
                        "('n07753592', 'Plátano', 'banana.png'), " +
                        "('n09239740', 'Estrellas', 'stars.png'), " +
                        "('n11669921', 'Flor', 'flower.png'), " +
                        "('n12992868', 'Seta', 'mushroom.png'), " +
                        "('n13103136', 'Árbol', 'tree.png');";

        db.execSQL(POPULATE_DATA);
        /*POPULATE_DATA =
                "INSERT INTO pictocatcher (pictoid, word, imagename, caught,catch_date) VALUES " +
                        "(3, 'gato', 'cat.png',1, '2017-05-10 12:31:55'), " +
                        "(11, 'boligrafo', 'writing pen.png',1,'2017-04-20 17:41:30')";
        db.execSQL(POPULATE_DATA);*/


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

    public int setCatchInfo(int pictoId, Date date, String catch_file) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ContentValues updateValues = new ContentValues();
        updateValues.put("caught", 1);
        updateValues.put("catch_date", dateFormat.format(date));
        updateValues.put("catch_file", catch_file);

        return db.update("pictocatcher", updateValues, "_id = ?", new String[] {Integer.toString(pictoId)});
    }

}
