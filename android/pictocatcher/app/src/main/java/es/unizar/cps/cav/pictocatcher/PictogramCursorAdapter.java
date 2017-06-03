package es.unizar.cps.cav.pictocatcher;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by almul0 on 5/12/17.
 */

public class PictogramCursorAdapter extends CursorAdapter {

    AssetManager assetManager;

    private LayoutInflater cursorInflater;

    Context context;

    public PictogramCursorAdapter(Context c, Cursor cursor) {
        super(c, cursor, 0);
        assetManager = c.getAssets();
        this.context = c;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return this.getCursor().getCount();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        /*MarkableImageView imageView = new MarkableImageView(context);
        imageView.setAdjustViewBounds(true);
        int imgSize = context.getResources().getDimensionPixelSize(R.dimen.gridimage);
        int imgPadding = context.getResources().getDimensionPixelSize(R.dimen.gridimage_padding);
        imageView.setLayoutParams(new GridView.LayoutParams(imgSize, imgSize));
        imageView.setPadding(imgPadding,imgPadding,imgPadding,imgPadding);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);*/
        return cursorInflater.inflate(R.layout.grid_cell, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        MarkableImageView imageView = (MarkableImageView) view.findViewById(R.id.gridImage);
        TextView textview= (TextView) view.findViewById(R.id.gridCaption);
        Typeface tfb = Typeface.createFromAsset(assetManager,
                String.format("fonts/%s", "babelsans_bold.ttf"));

        String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
        textview.setTypeface(tfb);
        textview.setText(word);
        view.setTag(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
        if (cursor.getInt(cursor.getColumnIndexOrThrow("caught")) == 1) {
            imageView.setChecked(true);
        } else {
            imageView.setChecked(false);
        }
        Picasso.with(context)
            .load("file:///android_asset/pictograms/"+cursor.getString(cursor.getColumnIndexOrThrow("imagename")))
            .placeholder(R.drawable.placeholder)
            .into(imageView);
    }

}
