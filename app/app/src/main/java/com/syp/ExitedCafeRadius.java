package com.syp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class ExitedCafeRadius extends Activity {

    private Button exitedCafeViewCafeMenu;
    private Button exitedCafeViewChekout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exited_cafe);

        exitedCafeViewCafeMenu = findViewById(R.id.exitedCafeViewCafeMenu);
        setViewCafeMenuOnClickListener();
        exitedCafeViewChekout = findViewById(R.id.exitedCafeViewCheckout);
        setDismissOnClickListener();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.6));
    }

    private void setViewCafeMenuOnClickListener(){
        exitedCafeViewCafeMenu.setOnClickListener((View v)-> {
            Intent i = getIntent().putExtra("action", "viewCafeMenu");
            setResult(RESULT_OK, i);
            finish();
        });
    }

    private void setDismissOnClickListener(){
        exitedCafeViewChekout.setOnClickListener((View v)-> {
            Intent i = getIntent().putExtra("action", "viewCheckout");
            setResult(RESULT_OK, i);
            finish();
        });
    }
}

