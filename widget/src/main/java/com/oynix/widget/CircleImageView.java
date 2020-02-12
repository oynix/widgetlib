package com.oynix.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * oynix at 2020-02-10 20:56
 */
public class CircleImageView extends AppCompatImageView {

    private Paint mPaint;
    private Xfermode mXfermodeDstOut;
    private Path mPath;

    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 禁止HW
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        mXfermodeDstOut = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPath == null) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            mPath = new Path();
            mPath.moveTo(0, 0);
            mPath.lineTo(getWidth(), 0);
            mPath.lineTo(getWidth(), getHeight());
            mPath.lineTo(0, getHeight());
            mPath.addCircle(cx, cy, Math.min(cx, cy), Path.Direction.CCW);
        }
        canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        mPaint.setXfermode(mXfermodeDstOut);
        canvas.drawPath(mPath, mPaint);
        mPaint.setXfermode(null);
        canvas.restore();
    }
}
