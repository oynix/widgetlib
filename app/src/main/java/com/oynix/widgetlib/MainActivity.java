package com.oynix.widgetlib;

import android.os.Bundle;

import com.oynix.widget.CircleLoadingView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CircleLoadingView loadingView = findViewById(R.id.loading);

        findViewById(R.id.btn_change_visibility).setOnClickListener((v) -> {
            if (loadingView.isShowing()) {
                loadingView.hide();
            } else {
                loadingView.show();
            }
        });
    }
}
