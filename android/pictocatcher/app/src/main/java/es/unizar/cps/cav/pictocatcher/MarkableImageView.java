package es.unizar.cps.cav.pictocatcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

/**
 * Created by almul0 on 5/17/17.
 */

public class MarkableImageView extends ImageView {
    private boolean checked = false;

    private static Drawable frame;
    private static Drawable tick;

    /*final Bitmap check = BitmapFactory.decodeResource(
            getResources(), R.drawable.checked2);

    final Bitmap picframe = BitmapFactory.decodeResource(
            getResources(), R.drawable.woodenframe1);*/

    public MarkableImageView(Context context) {
        super(context);
    }

    public MarkableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void loadDrawables(){
        if (frame == null) {
            frame = ContextCompat.getDrawable(getContext(), R.drawable.woodenframe1);
        }
        if (tick == null) {
            tick = ContextCompat.getDrawable(getContext(), R.drawable.checked2);
        }
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

        loadDrawables();

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //Drawable frame =  ContextCompat.getDrawable(getContext(),R.drawable.woodenframe1);

        frame.setBounds(0, 0, width, height);
        frame.draw(canvas);


        if(checked) {

            int imgSize = this.getWidth();//getContext().getResources().getDimensionPixelSize(R.id.gridImage);

            tick.setBounds(imgSize/2, imgSize/2, imgSize, imgSize);
            tick.draw(canvas);
        }
    }

    boolean mAdjustViewBounds;

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        mAdjustViewBounds = adjustViewBounds;
        super.setAdjustViewBounds(adjustViewBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable mDrawable = getDrawable();
        if (mDrawable == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (mAdjustViewBounds) {
            int mDrawableWidth = mDrawable.getIntrinsicWidth();
            int mDrawableHeight = mDrawable.getIntrinsicHeight();
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);

            if (heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY) {
                // Fixed Height & Adjustable Width
                int height = heightSize;
                int width = height * mDrawableWidth / mDrawableHeight;
                if (isInScrollingContainer())
                    setMeasuredDimension(width, height);
                else
                    setMeasuredDimension(Math.min(width, widthSize), Math.min(height, heightSize));
            } else if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
                // Fixed Width & Adjustable Height
                int width = widthSize;
                int height = width * mDrawableHeight / mDrawableWidth;
                if (isInScrollingContainer())
                    setMeasuredDimension(width, height);
                else
                    setMeasuredDimension(Math.min(width, widthSize), Math.min(height, heightSize));
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p != null && p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }
}
