package es.unizar.cps.cav.pictocatcher;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by almul0 on 5/12/17.
 */

public class PictogramCursorAdapter extends CursorAdapter {

    AssetManager assetManager;

    public PictogramCursorAdapter(Context c, Cursor cursor) {
        super(c, cursor, 0);
        assetManager = c.getAssets();
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
        MarkableImageView imageView = new MarkableImageView(context);
        imageView.setAdjustViewBounds(true);
        int imgSize = context.getResources().getDimensionPixelSize(R.dimen.gridimage);
        int imgPadding = context.getResources().getDimensionPixelSize(R.dimen.gridimage_padding);
        imageView.setLayoutParams(new GridView.LayoutParams(imgSize, imgSize));
        imageView.setPadding(imgPadding,imgPadding,imgPadding,imgPadding);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        MarkableImageView imageView = (MarkableImageView) view;

        try {

            InputStream is = assetManager.open("pictograms/"+cursor.getString(cursor.getColumnIndexOrThrow("imagename")));
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bitmap);
            imageView.setTag(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
            if (cursor.getInt(cursor.getColumnIndexOrThrow("caught")) == 1) {
                imageView.setChecked(true);
            } else {
                imageView.setChecked(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
