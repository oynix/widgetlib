package com.oynix.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * oynix at 2020/3/14 16:25
 * <p>
 * 就是名字的表面意思，菊花形状的loading
 */
public class JuhuaLoadingView extends View {

    private static final int COUNT = 8;
    private static final int PIECE = 360 / COUNT;

    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private RectF mRectDraw = new RectF();
    // 是否需要转转转
    private boolean mState = true;

    // false代表没人使用，true代表有人正在使用
    // 防止多个线程同时绘制
    private boolean mDrawToken = false;

    private int mRotateDegree = 0;
    private int mColor;

    public JuhuaLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.JuhuaLoadingView);
        mColor = ta.getColor(R.styleable.JuhuaLoadingView_juhua_color, 0xffffffff);
        ta.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(mColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 如果模式都不是EXACTLY，那么使用默认长度 50dp
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            width = Integer.MAX_VALUE;
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            height = Integer.MAX_VALUE;
        }
        int size = Math.min(width, height);
        if (size == Integer.MAX_VALUE) {
            width = dp2px(50);
            height = width;
        } else {
            width = size;
            height = size;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        float radius = w / 2f;
        float width = radius * 0.6f;
        float height = width * 0.333f;
        mRectDraw.set(radius - width, -height / 2f, radius, height / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mWidth / 2f, mHeight / 2f);
        canvas.rotate(mRotateDegree);

        for (int i = 0; i < COUNT; i++) {
            mPaint.setAlpha(255 - 30 * i);
            canvas.drawRoundRect(mRectDraw, mRectDraw.height() / 2, mRectDraw.height() / 2, mPaint);
            canvas.rotate(-PIECE);
        }

        canvas.restore();

        if (mState && !mDrawToken) {
            mDrawToken = true;
            postDelayed(this::redraw, 100);
        }
    }

    private void redraw() {
        mRotateDegree += PIECE;
        mDrawToken = false;
        postInvalidate();
    }

    private int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        mState = visibility == View.VISIBLE;
        invalidate();
    }

    /** 显示 */
    public void show() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
    }

    /** 隐藏 */
    public void hide() {
        if (getVisibility() != GONE) {
            setVisibility(GONE);
        }
    }

    /** 是否显示 */
    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

}
