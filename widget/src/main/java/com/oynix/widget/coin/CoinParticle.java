package com.oynix.widget.coin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.Random;

/**
 * oynix at 2020-02-13 10:54
 */
public class CoinParticle {

    // 开始绘制的图片角标
    private int startIndex;

    // x方向速度
    private float velocityX;
    private boolean forwardX;
    // x方向加速度，空气阻力
    private float accelerateX;
    // y方向速度
    private float velocityY;
    // y方向加速度，重力
    private float accelerateY;

    // coin边长，coin为正方形
    private float edge;

    private RectF rect;

    private Paint paint;

    // 旋转角度
    private float rotation = 0;

    public CoinParticle(int x, int y) {
        CoinRandom cr = new CoinRandom();
        this.startIndex = cr.getRandomInt(20);
        float velocity = cr.getRandomFloat(100);
        int degree = cr.getRandomInt(359);
        int edge = cr.getRandomInt(100, 200);
        this.velocityX = (float) (velocity * Math.cos(degree / 2 / Math.PI));
        // 初速度大于0（向右），加速度向左（小于0）；初速度小于0（向左），加速度向右（大于0）。总之与速度相反
        this.accelerateX = this.velocityX > 0 ? -1 : 1;

        this.velocityY = (float) (velocity * Math.sin(degree / 2 / Math.PI));
        this.accelerateY = 5;

        rect = new RectF(x, y, x + edge, y + edge);
        this.edge = edge;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    public void draw(Canvas canvas, Bitmap[] bitmaps) {
        // 超过左右边界，超出底边界，就停止
        if (rect.right < 0 || rect.right > canvas.getWidth() || rect.top > canvas.getHeight()) {
            Log.e("CoinParticle", "out of bound");
            return;
        }
        // 速度为正时，距离为正；速度为负时，距离为负
        float distanceX = velocityX;
        float distanceY = velocityY;

        // 更新位移
        float left = rect.left + distanceX;
        float right = rect.right + distanceX;
        float top = rect.top + distanceY;
        float bottom = rect.bottom + distanceY;
        this.rect.set(left, top, right, bottom);

        // 绘制
        canvas.drawBitmap(bitmaps[startIndex % bitmaps.length], null, rect, paint);
        startIndex ++;

        velocityX += accelerateX;
        velocityY += accelerateY;

    }

    private static class CoinRandom {
        static Random sRandom = new Random();

        public int getRandomInt(int upper) {
            return sRandom.nextInt(upper);
        }

        public int getRandomInt(int lower, int upper) {
            int d = Math.min(lower, upper);
            int t = Math.max(lower, upper);
            return d + getRandomInt(t - d);
        }

        public float getRandomFloat(float upper) {
            return sRandom.nextFloat() * upper;
        }

    }

}
