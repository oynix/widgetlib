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
 * oynix at 2020/3/15 17:22
 *
 * 首位互相追逐的环形进度条
 */
public class PursueLoadingView extends View {

    // 静止状态
    private static final int STATE_IDLE = 0x01;
    // 首尾僵持状态
    private static final int STATE_MIDDLE = 0x02;
    // 首加速状态
    private static final int STATE_HEAD = 0x03;
    // 尾加速状态
    private static final int STATE_TAIL = 0x04;
    // 刷新间隔
    private static final long FRAME_INTERVAL = 16;
    // 弧度增加/减小的速度
    private static final float VELOCITY = 10;
    // 僵持阶段停留的帧数
    private static final  int BALANCE_FRAME_THRESHOLD = 10;
    // 弧度最小值
    private static final float BALANCE_DEGREE_LOWER = 60;
    // 弧度最大值
    private static final float BALANCE_DEGREE_UPPER = 360 - BALANCE_DEGREE_LOWER;

    private int mState = STATE_MIDDLE;

    private int mWidth;
    private int mHeight;

    private Paint mPaint;
    private int mColor;

    private RectF mRectDrawArc = new RectF();

    private float headDegree = 60;
    private float tailDegree = 0;
    private int balanceFrames = 0;
    private float rotate = 0;
    private boolean mDrawToken = false;


    public PursueLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PursueLoadingView);
        mColor = ta.getColor(R.styleable.PursueLoadingView_radian_color, 0xff0000ff);
        ta.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
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

    private int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        // 描边的宽度
        float strokeWidth = mWidth / 12f;
        mPaint.setStrokeWidth(strokeWidth);

        float left = strokeWidth / 2f;
        float top = strokeWidth / 2f;
        float right = w - strokeWidth / 2f;
        float bottom = h - strokeWidth / 2f;
        mRectDrawArc.set(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(rotate, mWidth / 2f, mHeight / 2f);
        canvas.drawArc(mRectDrawArc, headDegree, -getSweepDegree(), false, mPaint);
        canvas.restore();

        if (mState != STATE_IDLE && !mDrawToken) {
            mDrawToken = true;
            postDelayed(this::redraw, FRAME_INTERVAL);
        }
    }

    private void redraw() {
        if (mState == STATE_IDLE) {
            mDrawToken = false;
            return;
        }
        float degreeDelta = getSweepDegree();
        if (mState == STATE_MIDDLE) {
            if (balanceFrames >= BALANCE_FRAME_THRESHOLD) {
                mState = degreeDelta > BALANCE_DEGREE_LOWER ? STATE_TAIL : STATE_HEAD;
                balanceFrames = 0;
            } else {
                balanceFrames += 1;
            }
        } else if (mState == STATE_HEAD) {
            if (degreeDelta >= BALANCE_DEGREE_UPPER) {
                mState = STATE_MIDDLE;
            } else {
                headDegree = (headDegree + VELOCITY) % 360;
            }
        } else if (mState == STATE_TAIL) {
            if (degreeDelta <= BALANCE_DEGREE_LOWER) {
                mState = STATE_MIDDLE;
            } else {
                tailDegree = (tailDegree + VELOCITY) % 360;
            }
        }
        rotate = (rotate + 5) % 360;
        mDrawToken = false;
        invalidate();
    }

    private float getSweepDegree() {
        return (headDegree - tailDegree + 360) % 360;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility == View.VISIBLE) {
            mState = STATE_MIDDLE;
            balanceFrames = 0;
            headDegree = 60;
            tailDegree = 0;
        } else {
            mState = STATE_IDLE;
        }
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
