package com.oynix.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * oynix at 2020/3/12 23:26
 */
public class CircleLoadingView extends View {

    private static final int STATE_IDLE = 0x01;
    private static final int STATE_ROLLING = 0x02;

    // 刷新间隔，理论上写每秒有16帧，即每1000/16=62.5毫秒刷新一次，人眼就看不出卡顿
    // 但是我看出来了，所以缩短到每50毫秒刷新一次
    private static final long FRAME_INTERVAL = 50;
    // 每帧旋转的角度
    private static final float FRAME_DEGREE = 30;
    // 720度为一个循环
    private static final float CYCLE_DEGREE = 720;
    // 每个球间隔的角度
    private static final int INTERVAL_DEGREE = 45;
    // 球的数量，6个
    private static final int COUNT = 6;

    private int mState = STATE_ROLLING;

    private int cx;
    private int cy;
    private int radius;

    private Paint mPaint;
    private int mColor;

    public CircleLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleLoadingView);
        mColor = ta.getColor(R.styleable.CircleLoadingView_color, 0xff6ed88c);
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
        // 当没有指定具体的宽高时，设置成50dp
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (wMode != MeasureSpec.EXACTLY) {
            width = dp2px(50);
        }
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (hMode != MeasureSpec.EXACTLY) {
            height = width;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w / 2;
        cy = h / 2;
        radius = Math.min(cx, cy);

        // 最大球半径为1/6，最小球半径为1/12，这比例效果看着还可以
        maxRadius = radius / 6f;
        // 半径的递增量 最大-最小，再除以间隔
        stepRadius = (maxRadius - maxRadius / 2) / (COUNT - 1);
    }

    private float rotate = 0;
    private float maxRadius;

    private float stepRadius;
    // 防止多个线程同时绘制
    private boolean mDrawToken = false;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(cx, cy);
        // 从12点方向开始绘制，顺时针 clockwise
        canvas.rotate(-90);

        for (int i = 0; i < COUNT; i++) {
            float r = Math.max(0, Math.min(360, rotate - INTERVAL_DEGREE * i));
            canvas.save();
            canvas.rotate(r);
            canvas.drawCircle(radius - maxRadius * 2, 0, maxRadius - stepRadius * i, mPaint);
            canvas.restore();
        }

        if (mState == STATE_ROLLING && !mDrawToken) {
            mDrawToken = true;
            rotate = (rotate + FRAME_DEGREE) % CYCLE_DEGREE;
            postDelayed(this::tokenInvalidate, FRAME_INTERVAL);
        }
    }

    private void tokenInvalidate() {
        mDrawToken = false;
        invalidate();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility == View.VISIBLE) {
            mState = STATE_ROLLING;
            rotate = 0;
            invalidate();
        } else {
            mState = STATE_IDLE;
            rotate = 0;
            invalidate();
        }
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

    private int dp2px(float dp) {
        return (int) (getContext().getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
