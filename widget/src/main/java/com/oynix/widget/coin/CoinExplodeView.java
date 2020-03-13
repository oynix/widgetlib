package com.oynix.widget.coin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

import com.oynix.widget.R;

import androidx.annotation.Nullable;

/**
 * oynix at 2020-02-13 10:52
 */
public class CoinExplodeView extends View {

    private Bitmap[] mBitmaps = new Bitmap[23];

    private CoinParticle[] particles;

    public CoinExplodeView(Context context) {
        super(context);
        init();
    }

    public CoinExplodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CoinExplodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int[] res = new int[] {
                R.drawable.jinbi_00,
                R.drawable.jinbi_01,
                R.drawable.jinbi_02,
                R.drawable.jinbi_03,
                R.drawable.jinbi_04,
                R.drawable.jinbi_05,
                R.drawable.jinbi_06,
                R.drawable.jinbi_07,
                R.drawable.jinbi_08,
                R.drawable.jinbi_09,
                R.drawable.jinbi_10,
                R.drawable.jinbi_11,
                R.drawable.jinbi_12,
                R.drawable.jinbi_13,
                R.drawable.jinbi_14,
                R.drawable.jinbi_15,
                R.drawable.jinbi_16,
                R.drawable.jinbi_17,
                R.drawable.jinbi_18,
                R.drawable.jinbi_19,
                R.drawable.jinbi_20,
                R.drawable.jinbi_21,
                R.drawable.jinbi_22
        };

        for (int i = 0; i < res.length; i++) {
            mBitmaps[i] = BitmapFactory.decodeResource(getResources(), res[i]);
        }

        particles = new CoinParticle[60];
    }
}
