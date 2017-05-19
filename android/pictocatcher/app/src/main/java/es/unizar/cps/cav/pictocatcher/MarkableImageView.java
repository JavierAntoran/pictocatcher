package es.unizar.cps.cav.pictocatcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by almul0 on 5/17/17.
 */

public class MarkableImageView extends ImageView {
    private boolean checked = false;

    public MarkableImageView(Context context) {
        super(context);
    }

    public MarkableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        invalidate();
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(checked) {
            Bitmap check = BitmapFactory.decodeResource(
                    getResources(), R.drawable.checked);

            Rect r1 = new Rect(0,0,check.getWidth(),check.getHeight());
            Rect r2 = new Rect(canvas.getWidth()-200,0, canvas.getWidth(),200);

            canvas.drawBitmap(check, r1, r2, new Paint());
        }
    }
}
